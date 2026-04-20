package de.thm.asc.tiel.interpreter.evaluation;

import de.thm.asc.tiel.interpreter.error.RuntimeError;
import de.thm.asc.tiel.interpreter.lexical.Token;

import java.io.PrintStream;
import java.util.List;

/**
 * Provides and initializes the global environment with built-in native functions.
 * This class defines standard library functions (e.g., {@code print}, {@code floor})
 * that are available to every program. Each function is implemented as a native
 * {@link TiELCallable}.
 */
public class Globals {

    /**
     * Creates and initializes the global environment.
     * The returned environment contains all built-in functions and serves as the
     * root scope for program execution.
     *
     * @param out The output stream used by native functions such as {@code print}.
     * @return a fully initialized global environment
     */
    public static Environment initEnv(PrintStream out) {
        var globals = new Environment();

        globals.define("print", new TiELCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public TiELValue call(Evaluator evaluator, List<TiELValue> arguments, Token.Position errorPosition) {
                out.println(arguments.getFirst().toString());
                return TiELValue.NIL;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });

        globals.define("floor", new TiELCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public TiELValue call(Evaluator evaluator, List<TiELValue> arguments, Token.Position errorPosition) {
                if (arguments.getFirst() instanceof TNumber(double value)) {
                    return new TNumber(Math.floor(value));
                }

                throw new RuntimeError("Expected argument to floor to be a number", errorPosition);
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });

        return globals;
    }
}
