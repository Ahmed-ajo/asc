package de.thm.asc.tiel.interpreter.evaluation;

import de.thm.asc.tiel.interpreter.lexical.Token;

import java.util.List;

/**
 * Represents a callable entity in the TiEL programming language.
 * Classes implementing this interface can be invoked as functions.
 */
non-sealed public interface TiELCallable extends TiELValue {

    /**
     * Returns the number of parameters required by the callable entity.
     *
     * @return The number of parameters (arity) expected by the callable.
     */
    int arity();

    /**
     * Executes the callable entity with the given arguments.
     *
     * @param evaluator     The evaluator which will execute this function when called.
     * @param arguments     The list of arguments provided to the callable entity.
     * @param errorPosition The position at which a potential error will be thrown.
     * @return The result of the function execution.
     */
    TiELValue call(Evaluator evaluator, List<TiELValue> arguments, Token.Position errorPosition);
}
