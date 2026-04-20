package de.thm.asc.tiel.interpreter.evaluation;

import de.thm.asc.tiel.interpreter.ast.stmt.FunctionDeclStmt;
import de.thm.asc.tiel.interpreter.lexical.Token;

import java.util.List;

/**
 * Represents a callable function in the TiEL programming language.
 * This class implements {@link TiELCallable} and wraps a function declaration
 * along with its closure environment.
 */
class TiELFunction implements TiELCallable {

    private final FunctionDeclStmt declaration;
    private final Environment closure;
    private final boolean isInitializer;

    /**
     * Constructs a new TiELFunction instance.
     *
     * @param declaration   The function declaration.
     * @param closure   The environment that is closed over in which the function was declared.
     * @param isInitializer Whether this function is a class initializer.
     */
    TiELFunction(FunctionDeclStmt declaration, Environment closure, boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
    }

    /**
     * Returns the number of parameters the function accepts.
     *
     * @return The arity of the function.
     */
    @Override
    public int arity() {
        return declaration.params.size();
    }

    /**
     * Calls the function with the given arguments.
     *
     * @param evaluator     The valuator for executing this function.
     * @param arguments     The arguments passed to the function.
     * @param errorPosition The position at which a potential error will be thrown.
     * @return The result of function execution, or {@code null} if no return value is specified.
     */
    @Override
    public TiELValue call(Evaluator evaluator, List<TiELValue> arguments, Token.Position errorPosition) {
        var environment = new Environment(this.closure);

        for (var i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i), arguments.get(i));
        }

        try {
            evaluator.executeStatementsInEnvironment(List.of(declaration.body), environment);
        } catch (ReturnException returnValue) {
            if (isInitializer) {
                return closure.getAt(0, "this");
            }

            return returnValue.value;
        }

        if (isInitializer) {
            return closure.getAt(0, "this");
        }

        return TiELValue.NIL;
    }

    /**
     * Returns a string representation of the function.
     *
     * @return A string in the format "<fn functionName>".
     */
    @Override
    public String toString() {
        return String.format("<fn %s>", declaration.name);
    }
}
