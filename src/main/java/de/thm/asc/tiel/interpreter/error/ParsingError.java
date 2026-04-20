package de.thm.asc.tiel.interpreter.error;

import de.thm.asc.tiel.interpreter.lexical.Token;

/**
 * Represents a parsing error in the TiEL interpreter.
 * This exception is thrown when the parser encounters an invalid syntax or structure.
 */
public class ParsingError extends Error {

    /**
     * Constructs a new parsing error with the specified message and position.
     *
     * @param message  The error message describing the issue.
     * @param position The position where the error occurred.
     */
    public ParsingError(String message, Token.Position position) {
        super(message, position);
    }
}

