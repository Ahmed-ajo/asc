package de.thm.asc.tiel.interpreter.ast.expr;

public final class IndexExpr extends Expr {

    public final Expr target;
    public final Expr index;

    public IndexExpr(Expr target, Expr index) {
        this.target = target;
        this.index = index;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof IndexExpr o)) return false;

        return target.equals(o.target) && index.equals(o.index);
    }
}
