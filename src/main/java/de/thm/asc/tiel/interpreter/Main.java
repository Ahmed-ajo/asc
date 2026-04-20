package de.thm.asc.tiel.interpreter;

import de.thm.asc.tiel.interpreter.ast.AstPrinter;
import de.thm.asc.tiel.interpreter.ast.stmt.Stmt;
import de.thm.asc.tiel.interpreter.evaluation.Evaluator;
import de.thm.asc.tiel.interpreter.evaluation.Globals;
import de.thm.asc.tiel.interpreter.error.Error;
import de.thm.asc.tiel.interpreter.lexical.Token;
import de.thm.asc.tiel.interpreter.semantic.Binder;
import de.thm.asc.tiel.interpreter.syntax.Parser;
import de.thm.asc.tiel.interpreter.lexical.Scanner;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "tiel", mixinStandardHelpOptions = true, version = "tiel 1.0")
public class Main implements Callable<Integer> {

    @Parameters(index = "0", description = "Source code file to execute.", arity = "1")
    private File input;

    @Option(names = {"--tokens"}, description = "Print scanned tokens.", arity = "0")
    private boolean printTokens;

    @Option(names = {"--ast"}, description = "Print generated AST.", arity = "0")
    private boolean printAst;

    @Override
    public Integer call() throws Exception {
        try {
            String src;
            try {
                src = new String(Files.readAllBytes(input.toPath()));
            } catch (NoSuchFileException e) {
                System.out.printf("File %s could not be read%n", input.getPath());
                return 1;
            }

            //<< 01-implement, interpreter-phases, Methode call()
            var tokens = new Scanner(src).scan(); printTokens(tokens);
            var ast = new Parser(tokens).parse(); printAST(ast);
            var evaluator = new Evaluator(Globals.initEnv(System.out));
            new Binder(evaluator).resolve(ast);
            evaluator.interpret(ast);
            //>>
        } catch (Error e) {
            System.err.printf("An error occurred at %s:\n", e.position);
            System.err.println(e.getMessage());
            return 1;
        }

        return 0;
    }

    private void printAST(List<Stmt> ast) {
        if (printAst) {
            System.out.println("Generated AST:");
            System.out.println(new AstPrinter().print(ast));
            System.out.println();
        }
    }

    private void printTokens(List<Token> tokens) {
        if (printTokens) {
            System.out.println("Scanned tokens:");
            for (var t : tokens) {
                System.out.println(t);
            }
            System.out.println();
        }
    }

    public static void main(String... args) {
        var exitCode = new CommandLine(new Main()).setCaseInsensitiveEnumValuesAllowed(true).execute(args);
        System.exit(exitCode);
    }
}
