package de.thm.asc.tiel.interpreter.ast.expr;

import de.thm.asc.tiel.interpreter.evaluation.TiELValue;

public final class LiteralExpr extends Expr {

    public final TiELValue value;

    public LiteralExpr(TiELValue value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof LiteralExpr o)) return false;

        return value.equals(o.value);
    }
}
