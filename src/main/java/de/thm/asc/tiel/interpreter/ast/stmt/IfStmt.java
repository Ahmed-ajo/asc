package de.thm.asc.tiel.interpreter.ast.stmt;

import de.thm.asc.tiel.interpreter.ast.expr.Expr;

import java.util.Objects;

//<< 06-parse, ast-ifstmt, Klasse IfStmt
public final class IfStmt extends Stmt {

    public final Expr condition;
    public final Stmt thenBranch;
    public final Stmt elseBranch;
    //>>

    public IfStmt(Expr condition, Stmt thenBranch, Stmt elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof IfStmt o)) return false;

        return condition.equals(o.condition)
                && thenBranch.equals(o.thenBranch)
                && Objects.equals(elseBranch, o.elseBranch);
    }
}
