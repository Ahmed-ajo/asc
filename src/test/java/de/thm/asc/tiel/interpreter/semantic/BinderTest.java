package de.thm.asc.tiel.interpreter.semantic;

import de.thm.asc.tiel.interpreter.evaluation.Evaluator;
import de.thm.asc.tiel.interpreter.evaluation.Globals;
import de.thm.asc.tiel.interpreter.error.RuntimeError;
import de.thm.asc.tiel.interpreter.lexical.Scanner;
import de.thm.asc.tiel.interpreter.syntax.Parser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BinderTest {

    @Test
    void rejectsReturnOutsideFunction() {
        var error = assertThrows(RuntimeError.class, () -> bind("""
                return 1;
                """));

        assertEquals("Can't return from top-level code.", error.getMessage());
    }

    @Test
    void rejectsReadingVariableInOwnInitializer() {
        var error = assertThrows(RuntimeError.class, () -> bind("""
                {
                    var value = value;
                }
                """));

        assertEquals("Can't read local variable in its own initializer.", error.getMessage());
    }

    @Test
    void acceptsNestedFunctionsAndShadowing() {
        assertDoesNotThrow(() -> bind("""
                var x = 1;
                fun outer(y) {
                    var x = y;
                    fun inner(z) {
                        return x + z;
                    }
                    return inner(2);
                }
                outer(3);
                """));
    }

    @Test
    void rejectsGlobalRedeclaration() {
        var error = assertThrows(RuntimeError.class, () -> bind("""
                fun foo(x) {
                    var foo = 5;
                    print(foo);
                }
                var a = foo;
                var foo = 4;
                """));

        assertEquals("foo is already defined on this scope", error.getMessage());
    }

    @Test
    void rejectsPredefinedGlobalRedeclaration() {
        var error = assertThrows(RuntimeError.class, () -> bind("""
                var print = 1;
                """));

        assertEquals("print is already defined on this scope", error.getMessage());
    }

    @Test
    void acceptsForwardDeclaration() {
        assertDoesNotThrow(() -> bind("""
                var g = 0;
                {
                    fun f() {
                        print(a);
                    }
                    var a = 2;
                    g = f;
                }
                var a = 1;
                g();
                """));
    }

    private static void bind(String source) {
        var ast = new Parser(new Scanner(source).scan()).parse();
        var evaluator = new Evaluator(Globals.initEnv(new java.io.PrintStream(new ByteArrayOutputStream())));

        new Binder(evaluator).resolve(ast);
    }
}
