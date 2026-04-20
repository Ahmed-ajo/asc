package de.thm.asc.tiel.interpreter.ast.stmt;

import de.thm.asc.tiel.interpreter.ast.expr.Expr;

import java.util.Objects;

public final class ReturnStmt extends Stmt {

    public final Expr value;

    public ReturnStmt(Expr value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ReturnStmt o)) return false;

        return Objects.equals(value, o.value);
    }
}
