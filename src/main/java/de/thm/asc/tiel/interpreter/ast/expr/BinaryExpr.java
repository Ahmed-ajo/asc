package de.thm.asc.tiel.interpreter.ast.expr;

public final class BinaryExpr extends Expr {

    public enum Operator {
        ADD,
        SUB,
        MUL,
        DIV,
        EQ,
        NEQ,
        LT,
        LE,
        GT,
        GE
    }
    
    public final Expr left;
    public final Operator operator;
    public final Expr right;

    public BinaryExpr(Expr left, Operator operator, Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof BinaryExpr o)) return false;

        return left.equals(o.left) && operator.equals(o.operator) && right.equals(o.right);
    }
}
