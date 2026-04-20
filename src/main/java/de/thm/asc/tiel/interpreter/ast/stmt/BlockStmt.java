package de.thm.asc.tiel.interpreter.ast.stmt;

import java.util.List;

public final class BlockStmt extends Stmt {

    public final List<Stmt> statements;

    public BlockStmt(List<Stmt> statements) {
        this.statements = statements;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof BlockStmt o)) return false;

        return statements.equals(o.statements);
    }
}
