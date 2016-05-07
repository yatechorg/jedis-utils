[![Build Status](https://travis-ci.org/yatechorg/jedis-utils.svg)](https://travis-ci.org/yatechorg/jedis-utils)
[![Download](https://api.bintray.com/packages/yatech/maven/jedis-utils/images/download.svg)](https://bintray.com/yatech/maven/jedis-utils/_latestVersion)
[![Coverage Status](https://coveralls.io/repos/yatechorg/jedis-utils/badge.svg?branch=master&service=github)](https://coveralls.io/github/yatechorg/jedis-utils?branch=master)

# jedis-utils
Utilities for common tasks using [Jedis](https://github.com/xetorthio/jedis) (the java redis client).

## Lua Script Builder
A builder facility for building lua scripts to be executed in Redis using Jedis. 
The builder uses an API similar to the API provided by Jedis. 

For example:

```java
import static org.yatech.jedis.utils.lua.LuaScriptBuilder.*;
import org.yatech.jedis.utils.lua.LuaScriptBuilder;
import org.yatech.jedis.utils.lua.LuaScript;
import org.yatech.jedis.utils.lua.LuaLocalArray;

...

LuaScriptBuilder builder = startScript();
builder.select(0);
LuaLocalArray data = builder.hgetAll("key1");
builder.select(1);
       .hmset("key1", data)
       .select(0)
       .del("key1");
LuaScript script = builder.endScript();

Jedis jedis = ...
script.exec(jedis);
```

It is also possible to create prepared scripts with arguments and with some control statements.
For example (this time in Groovy):

```groovy
import static org.yatech.jedis.utils.lua.LuaScriptBuilder.*
import static org.yatech.jedis.utils.lua.LuaConditoins.*

...

def key1 = newKeyArgument('key1')
def key2 = newKeyArgument('key2')
def member = newStringValueArgument('member')
def score = newDoubleValueArgument('score')
def db0 = newIntValueArgument('db0')
def db1 = newIntValueArgument('db1')

def script = startScript().with {
    select(db0)
    def payload = hgetAll(key1)
    select(db1)
    def existingScore = zscore(key2, member)
    ifCondition(isNull(existingScore)).then(startBlock(it).with {
        zadd(key2, score, member)
        endBlock()
    }).endIf()
    hmset(key1, payload)
    select(db0)
    del(key1)
    endPreparedScript()
}

script.setKeyArgument('key1','myhash1')
script.setKeyArgument('key2','myzset1')
script.setValueArgument('db0',7)
script.setValueArgument('db1',8)
script.setValueArgument('member','thefirstmember')
script.setValueArgument('score',1.234)
Jedis jedis = ...
script.exec(jedis)
```

### Thread-safe Lua Prepared Script
If you need to have a prepared lua script which should be used in a multithreaded application, it is advised to create it as thread-safe. In order to do so, use `LuaScriptConfig` and set `threadSafe` to `true`. See example below:

```java
import org.yatech.jedis.utils.lua.LuaPreparedScript;
import org.yatech.jedis.utils.lua.LuaScriptConfig;
import org.yatech.jedis.utils.lua.LuaValue;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.yatech.jedis.utils.lua.LuaScriptBuilder.*;

public class MyService {
    private final LuaPreparedScript script;
    private final JedisPool jedisPool;

    public MyService() {
        this.script = createScript();
        this.jedisPool = new JedisPool();
    }

    private LuaPreparedScript createScript() {
        LuaValue<String> zsetKey = newKeyArgument("zset_key");
        LuaValue<Double> score = newDoubleValueArgument("score");
        LuaValue<String> member = newStringValueArgument("member");
        LuaScriptConfig config = LuaScriptConfig.newConfig().threadSafe(true).build();
        LuaPreparedScript script = startScript()
                .zadd(zsetKey, score, member)
                .endPreparedScript(config);
        return script;
    }
    
    public void addMember(String member, double score) {
        script.setKeyArgument("zset_key", "ordered_members");
        script.setValueArgument("member", member);
        script.setValueArgument("score", score);
        try (Jedis jedis = jedisPool.getResource()) {
            script.exec(jedis);
        }
    }
    
    public void destroy() {
        jedisPool.destroy();
    }
}
```

### Script Caching
Script caching is enabled by default and is handled internally. This reduces the need to load the script to redis each time it is executed. If you prefer not to use script caching for any reason, use `LuaScriptConfig` and set `useScriptCaching` to `false`:

```java
LuaScriptConfig config = LuaScriptConfig.newConfig().useScriptCaching(false).build();
```

### Assign Value to an Existing Local Variable
In order to reuse an existing local variable, use the `assign` builder method:

```java
LuaScriptBuilder builder = startScript();
LuaLocalValue myLocal = builder.get("mykey1");
LuaScript script = builder
    .set("mykey2", myLocal)
    .assign(myLocal, builder.get("mykey3"))
    .endScriptReturn(myLocal);
```
