package de.thm.asc.tiel.interpreter.ast.expr;

public final class VariableExpr extends Expr {

    public final String name;

    public VariableExpr(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof VariableExpr o)) return false;

        return name.equals(o.name);
    }
}
