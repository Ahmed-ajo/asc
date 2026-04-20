package de.thm.asc.tiel.interpreter.ast.expr;

public final class AssignExpr extends Expr {

    public final Expr target;
    public final Expr value;

    public AssignExpr(Expr target, Expr value) {
        this.target = target;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof AssignExpr o)) return false;

        return target.equals(o.target) && value.equals(o.value);
    }
}
