package de.thm.asc.tiel.interpreter.ast.stmt;

import de.thm.asc.tiel.interpreter.ast.Positionable;
import de.thm.asc.tiel.interpreter.lexical.Token;

public sealed abstract class Stmt implements Positionable<Stmt>
        permits BlockStmt, ExpressionStmt, FunctionDeclStmt, IfStmt, ReturnStmt, VarDeclStmt, WhileStmt {

    private Token.Position position;

    @Override
    public Stmt withPosition(Token.Position position) {
        this.position = position;

        return this;
    }

    @Override
    public Token.Position getPosition() {
        return position;
    }
}