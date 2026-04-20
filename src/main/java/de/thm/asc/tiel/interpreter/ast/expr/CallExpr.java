package de.thm.asc.tiel.interpreter.ast.expr;

import java.util.List;

public final class CallExpr extends Expr {

    public final Expr callee;
    public final List<Expr> arguments;

    public CallExpr(Expr callee, List<Expr> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof CallExpr o)) return false;

        return callee.equals(o.callee) && arguments.equals(o.arguments);
    }
}
