package de.thm.asc.tiel.interpreter.ast.stmt;

import de.thm.asc.tiel.interpreter.ast.expr.Expr;

public final class WhileStmt extends Stmt {

    public final Expr condition;
    public final Stmt body;

    public WhileStmt(Expr condition, Stmt body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof WhileStmt o)) return false;

        return condition.equals(o.condition) && body.equals(o.body);
    }
}
