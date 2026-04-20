package de.thm.asc.tiel.interpreter.ast.expr;

public final class UnaryExpr extends Expr {

    public enum Operator {
        MINUS,
        NOT,
    }

    public final Operator operator;
    public final Expr right;

    public UnaryExpr(Operator operator, Expr right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof UnaryExpr o)) return false;

        return operator.equals(o.operator) && right.equals(o.right);
    }
}
