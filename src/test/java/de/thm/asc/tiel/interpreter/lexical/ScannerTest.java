package de.thm.asc.tiel.interpreter.lexical;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScannerTest {

    @Test
    void scansVariableDeclarationAndFunctionCall() {
        var tokens = new Scanner("""
                var answer = 12.5;
                print(answer);
                """).scan();

        assertTokenTypes(tokens, List.of(
                TokenType.VAR,
                TokenType.IDENTIFIER,
                TokenType.EQUAL,
                TokenType.NUMBER,
                TokenType.SEMICOLON,
                TokenType.IDENTIFIER,
                TokenType.LEFT_PAREN,
                TokenType.IDENTIFIER,
                TokenType.RIGHT_PAREN,
                TokenType.SEMICOLON,
                TokenType.EOF
        ));

        assertEquals("answer", tokens.get(1).value());
        assertEquals(12.5, tokens.get(3).value());
    }

    @Test
    void skipsLineComments() {
        var tokens = new Scanner("""
                var x = 1; // ignore this
                return x;
                """).scan();

        assertTokenTypes(tokens, List.of(
                TokenType.VAR,
                TokenType.IDENTIFIER,
                TokenType.EQUAL,
                TokenType.NUMBER,
                TokenType.SEMICOLON,
                TokenType.RETURN,
                TokenType.IDENTIFIER,
                TokenType.SEMICOLON,
                TokenType.EOF
        ));
    }

    @Test
    void scansMultilineString() {
        var tokens = new Scanner("""
                var message = "hello
                world";
                print(message);
                """).scan();

        assertTokenTypes(tokens, List.of(
                TokenType.VAR,
                TokenType.IDENTIFIER,
                TokenType.EQUAL,
                TokenType.STRING,
                TokenType.SEMICOLON,
                TokenType.IDENTIFIER,
                TokenType.LEFT_PAREN,
                TokenType.IDENTIFIER,
                TokenType.RIGHT_PAREN,
                TokenType.SEMICOLON,
                TokenType.EOF
        ));

        assertEquals("hello\nworld", tokens.get(3).value());
    }

    @Test
    void scansSimpleFunction() {
        var tokens = new Scanner("""
                fun test(flag) {
                    if not flag and true or false {
                        return 1 != 2;
                    }
                }
                """).scan();

        assertTokenTypes(tokens, List.of(
                TokenType.FUN,
                TokenType.IDENTIFIER,
                TokenType.LEFT_PAREN,
                TokenType.IDENTIFIER,
                TokenType.RIGHT_PAREN,
                TokenType.LEFT_BRACE,
                TokenType.IF,
                TokenType.NOT,
                TokenType.IDENTIFIER,
                TokenType.AND,
                TokenType.TRUE,
                TokenType.OR,
                TokenType.FALSE,
                TokenType.LEFT_BRACE,
                TokenType.RETURN,
                TokenType.NUMBER,
                TokenType.NOT_EQUAL,
                TokenType.NUMBER,
                TokenType.SEMICOLON,
                TokenType.RIGHT_BRACE,
                TokenType.RIGHT_BRACE,
                TokenType.EOF
        ));
    }

    private static void assertTokenTypes(List<Token> tokens, List<TokenType> expectedTypes) {
        assertEquals(expectedTypes.size(), tokens.size());

        for (var i = 0; i < expectedTypes.size(); i++) {
            assertEquals(expectedTypes.get(i), tokens.get(i).type(), "token index " + i);
        }
    }
}
