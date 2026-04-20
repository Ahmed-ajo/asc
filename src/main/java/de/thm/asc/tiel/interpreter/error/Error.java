package de.thm.asc.tiel.interpreter.error;

import de.thm.asc.tiel.interpreter.lexical.Token;

/**
 * Utility class for handling errors in the TiEL interpreter.
 * Provides methods for reporting errors and terminating execution.
 */
public class Error extends RuntimeException {

    public final Token.Position position;

    protected Error(String message, Token.Position position) {
        super(message);
        this.position = position;
    }
}
