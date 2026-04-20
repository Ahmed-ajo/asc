package de.thm.asc.tiel.interpreter.ast.expr;

public final class LogicalExpr extends Expr {

    public enum Operator {
        AND,
        OR
    }

    public final Expr left;
    public final Operator operator;
    public final Expr right;

    public LogicalExpr(Expr left, Operator operator, Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof LogicalExpr o)) return false;

        return left.equals(o.left) && operator.equals(o.operator) && right.equals(o.right);
    }
}
