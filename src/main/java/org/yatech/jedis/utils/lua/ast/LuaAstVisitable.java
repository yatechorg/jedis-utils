package org.yatech.jedis.utils.lua.ast;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public interface LuaAstVisitable {

    void accept(LuaAstVisitor visitor);

}
