package de.thm.asc.tiel.interpreter.error;

import de.thm.asc.tiel.interpreter.lexical.Token;

/**
 * Represents a runtime error in the TiEL interpreter.
 * This exception is thrown when an operation encounters an invalid state,
 * such as accessing an undefined variable or performing an illegal operation.
 */
public class RuntimeError extends Error {

    /**
     * Constructs a new runtime error with the specified message and position.
     *
     * @param message The error message describing the issue.
     * @param position The position where the error occurred.
     */
    public RuntimeError(String message, Token.Position position) {
        super(message, position);
    }
}
