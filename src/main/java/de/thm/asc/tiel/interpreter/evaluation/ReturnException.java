package de.thm.asc.tiel.interpreter.evaluation;

/**
 * Exception used to handle return statements in the TiEL programming language.
 * This exception is used for control flow to return values from functions.
 */
class ReturnException extends RuntimeException {

    /**
     * The value being returned by the function.
     */
    final TiELValue value;

    /**
     * Constructs a new ReturnException with the specified return value.
     *
     * @param value The value to be returned.
     */
    ReturnException(TiELValue value) {
        super(null, null, false, false);

        this.value = value;
    }
}
