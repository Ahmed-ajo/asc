package de.thm.asc.tiel.interpreter.ast.stmt;

import de.thm.asc.tiel.interpreter.ast.expr.Expr;

public final class VarDeclStmt extends Stmt {

    public final String name;
    public final Expr initializer;

    public VarDeclStmt(String name, Expr initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof VarDeclStmt o)) return false;

        return name.equals(o.name) && initializer.equals(o.initializer);
    }
}
