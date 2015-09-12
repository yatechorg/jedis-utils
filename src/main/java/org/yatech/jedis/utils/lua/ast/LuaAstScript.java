package org.yatech.jedis.utils.lua.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaAstScript implements LuaAstVisitable {

    private final List<LuaAstStatement> statements;

    public LuaAstScript() {
        this.statements = new ArrayList<>();
    }

    public List<LuaAstStatement> getStatements() {
        return statements;
    }

    @Override
    public void accept(LuaAstVisitor visitor) {
        visitor.visit(this);
    }

}
