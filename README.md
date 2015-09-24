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
