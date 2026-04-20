package de.thm.asc.tiel.interpreter.evaluation;

import de.thm.asc.tiel.interpreter.error.RuntimeError;
import de.thm.asc.tiel.interpreter.lexical.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an environment that stores variable bindings.
 * Environments can be nested, allowing for lexical scoping.
 */
public class Environment {

    public final Environment enclosing;
    public final Map<String, TiELValue> values = new HashMap<>();

    /**
     * Constructs a global environment with no enclosing scope.
     */
    public Environment() {
        this.enclosing = null;
    }

    /**
     * Constructs a new environment with a given enclosing environment.
     *
     * @param enclosing The enclosing environment, providing outer scope access.
     */
    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    /**
     * Retrieves the value of a variable by name.
     *
     * @param name          The name of the variable.
     * @param errorPosition The position from which this variable was accessed.
     * @return The value of the variable.
     * @throws RuntimeError if the variable is undefined.
     */
    //<< 09-expr, access-path, Methode get()
    public TiELValue get(String name, Token.Position errorPosition) {
        if (values.containsKey(name)) {
            return values.get(name);
        }

        if (enclosing != null) {
            return enclosing.get(name, errorPosition);
        }

        throw new RuntimeError(String.format("Undefined variable '%s'.", name), errorPosition);
    }
    //>>

    /**
     * Assigns a value to an existing variable.
     *
     * @param name          The name of the variable.
     * @param value         The value to assign.
     * @param errorPosition The position from which this variable was accessed.
     * @throws RuntimeError if the variable is not defined in the current or any enclosing scope.
     */
    public void assign(String name, TiELValue value, Token.Position errorPosition) {
        if (values.containsKey(name)) {
            values.put(name, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value, errorPosition);
            return;
        }

        throw new RuntimeError(String.format("Identifier not declared '%s'.", name), errorPosition);
    }

    /**
     * Defines a new variable in the current environment.
     *
     * @param name  The name of the variable.
     * @param value The value of the variable.
     */
    //<< 09-expr, env-define, Methode define()
    public void define(String name, TiELValue value) {
        values.put(name, value);
    }
    //>>

    /**
     * Resolves the binding ancestor.
     *
     * @param distance The distance from the current environment.
     * @return The environment at the given distance.
     */
    private Environment ancestor(int distance) {
        var environment = this;
        for (int i = 0; i < distance; i++) {
            if (environment == null) break;
            environment = environment.enclosing;
        }

        return environment;
    }

    /**
     * Gets a value from the environment at the given distance.
     *
     * @param distance The distance from the current environment.
     * @param name     The name of the variable to get the value of.
     * @return The value of the variable.
     */
    public TiELValue getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    /**
     * Assigns a value to a variable in the environment at the given distance.
     *
     * @param distance The distance from the current environment.
     * @param name     The name of the variable to assign to.
     * @param value    The value to assign.
     */
    void assignAt(int distance, String name, TiELValue value) {
        ancestor(distance).values.put(name, value);
    }

    /**
     * Returns a string representation of the environment, including enclosed scopes.
     *
     * @return A string representation of variable bindings in this environment.
     */
    @Override
    public String toString() {
        var result = values.toString();
        if (enclosing != null) {
            result += " -> " + enclosing;
        }

        return result;
    }
}
