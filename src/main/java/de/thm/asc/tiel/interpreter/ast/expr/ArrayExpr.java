package de.thm.asc.tiel.interpreter.ast.expr;

import java.util.List;

public final class ArrayExpr extends Expr {

    public final List<Expr> elements;

    public ArrayExpr(List<Expr> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ArrayExpr o)) return false;

        return elements.equals(o.elements);
    }
}
