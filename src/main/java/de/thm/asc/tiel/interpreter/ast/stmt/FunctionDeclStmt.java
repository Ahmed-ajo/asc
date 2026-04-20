package de.thm.asc.tiel.interpreter.ast.stmt;

import java.util.List;

public final class FunctionDeclStmt extends Stmt {

    public final String name;
    public final List<String> params;
    public final BlockStmt body;

    public FunctionDeclStmt(String name, List<String> params, BlockStmt body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof FunctionDeclStmt o)) return false;

        return name.equals(o.name) && params.equals(o.params) && body.equals(o.body);
    }
}
