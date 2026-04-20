package de.thm.asc.tiel.interpreter.error;

import de.thm.asc.tiel.interpreter.lexical.Token;

/**
 * Represents a scanning error in the TiEL interpreter.
 * This exception is thrown when the scanner encounters an invalid token.
 */
public class ScanningError extends Error {

    /**
     * Constructs a new scanning error with the specified message and line number.
     *
     * @param message  The error message describing the issue.
     * @param position The position where the error occurred.
     */
    public ScanningError(String message, Token.Position position) {
        super(message, position);
    }
}
