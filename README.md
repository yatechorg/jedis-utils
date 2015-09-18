[![Build Status](https://travis-ci.org/yatechorg/jedis-utils.svg)](https://travis-ci.org/yatechorg/jedis-utils)
[![Download](https://api.bintray.com/packages/yatech/maven/jedis-utils/images/download.svg)](https://bintray.com/yatech/maven/jedis-utils/_latestVersion)

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
For example (Groovy):
```groovy
import static org.yatech.jedis.utils.lua.LuaScriptBuilder.*
import static org.yatech.jedis.utils.lua.LuaConditoins.*

...

def key1 = newKeyArgument('key1')
def key2 = newKeyArgument('key2')
def member1 = newStringValueArgument('member1')
def score = newDoubleValueArgument('score')
def db0 = newIntValueArgument('db0')
def db1 = newIntValueArgument('db1')

def builder = startScript()
builder.select(db0)
def data = builder.hgetAll(key1)
builder.select(db1)
def existingScore = builder.zscore(key2, member1)
builder.ifCondition(isNull(existingScore)).then(
        startBlock(builder)
        .zadd(key2, score, member1)
        .endBlock())
    .endIf()
.hmset(key1, data)
.select(db0)
.del(key1)
def preparedScript = builder.endPreparedScript()

preparedScript.setKeyArgument('key1','myhash1')
preparedScript.setKeyArgument('key2','myzset1')
preparedScript.setValueArgument('db0',7)
preparedScript.setValueArgument('db1',8)
preparedScript.setValueArgument('member1','thefirstmember')
preparedScript.setValueArgument('score',1.234)
Jedis jedis = ...
preparedScript.exec(jedis)
```
