package de.thm.asc.tiel.interpreter.ast;

import de.thm.asc.tiel.interpreter.lexical.Token;

public interface Positionable<T> {

    T withPosition(Token.Position position);

    Token.Position getPosition();
}
