package de.thm.asc.tiel.interpreter.ast.stmt;

import de.thm.asc.tiel.interpreter.ast.expr.Expr;

public final class ExpressionStmt extends Stmt {

    public final Expr expression;

    public ExpressionStmt(Expr expression) {
        this.expression = expression;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ExpressionStmt o)) return false;

        return expression.equals(o.expression);
    }
}
