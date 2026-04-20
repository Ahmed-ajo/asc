package de.thm.asc.tiel.interpreter.integration;

import de.thm.asc.tiel.interpreter.evaluation.Evaluator;
import de.thm.asc.tiel.interpreter.evaluation.Globals;
import de.thm.asc.tiel.interpreter.lexical.Scanner;
import de.thm.asc.tiel.interpreter.semantic.Binder;
import de.thm.asc.tiel.interpreter.syntax.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class IntegrationTests {

    private String evaluate(String source) {
        var outputStream = new ByteArrayOutputStream();

        var tokens = new Scanner(source).scan();
        var ast = new Parser(tokens).parse();
        var evaluator = new Evaluator(Globals.initEnv(new PrintStream(outputStream)));
        new Binder(evaluator).resolve(ast);
        evaluator.interpret(ast);

        return outputStream.toString();
    }

    private void compare(String sourceFile, String expectedOutputFile) {
        var source = ResourceUtil.getIntegrationTestFile(sourceFile);
        var expectedOutput = ResourceUtil.getIntegrationTestFile(expectedOutputFile);

        var actualOutput = evaluate(source);

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void base() {
        compare("stage_00_base.tiel", "stage_00_base_output.txt");
    }

    @Test
    public void arrays() {
        compare("stage_01_arrays.tiel", "stage_01_arrays_output.txt");
    }

    @Test
    public void classes() {
        compare("stage_02_classes.tiel", "stage_02_classes_output.txt");
    }

    @Test
    public void inheritance() {
        compare("stage_03_inheritance.tiel", "stage_03_inheritance_output.txt");
    }
}
