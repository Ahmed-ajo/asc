package de.thm.asc.tiel.interpreter.lexical;

import de.thm.asc.tiel.interpreter.error.ScanningError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.thm.asc.tiel.interpreter.lexical.TokenType.*;

/**
 * The Scanner class is responsible for tokenizing a given source code string.
 * It reads the input source character by character and converts it into a list of tokens,
 * which can be used for further processing in the parser
 */
public class Scanner {

    /**
     * A mapping of reserved keywords to their respective token types.
     */
    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("and", AND);
        keywords.put("false", FALSE);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("elif", ELIF);
        keywords.put("else", ELSE);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("return", RETURN);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
        keywords.put("not", NOT);
    }

    private final String source; // The source code to be scanned
    private final List<Token> tokens = new ArrayList<>(); // List of tokens produced by scanning
    private int start = 0; // Start position of the current token
    private int current = 0; // Current position in the source
    private int line = 1; // Current line number in the source
    private int column = 0; // Start position of the current line in the source

    /**
     * Advances the {line} and {column} counters.
     *
     * @param currentChar The character that is currently being consumed.
     */
    private void advanceLine(char currentChar) {
        if (currentChar == '\n') {
            line++;
            column = 0;
        } else {
            column++;
        }
    }

    /**
     * Constructs a Scanner with the given source code.
     *
     * @param source The source code to scan.
     */
    public Scanner(String source) {
        this.source = source;
    }

    /**
     * Scans the source code and returns a list of tokens.
     *
     * @return A list of tokens representing the scanned source code.
     */
    //<< 03-scan, scan-method, Methode scan()
    public List<Token> scan() {
        while (!isAtEnd()) {
            start = current;
            nextToken();
        }
        tokens.add(new Token(EOF, "", null, new Token.Position(line, column + 1)));
        //>>
        return tokens;
    }

    /**
     * Scans the next token in the source code.
     */
    private void nextToken() {
        var c = advance();
        switch (c) {
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case '{' -> addToken(LEFT_BRACE);
            case '}' -> addToken(RIGHT_BRACE);
            case ';' -> addToken(SEMICOLON);
            case ',' -> addToken(COMMA);
            case '-' -> addToken(MINUS);
            case '*' -> addToken(STAR);
            case '+' -> addToken(PLUS);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '!' -> addToken(match('=') ? NOT_EQUAL : NOT);
            case '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
            }
            case ' ', '\r', '\t', '\n' -> {
            } // Ignore whitespace
            case '"' -> string();
            default -> {
                //<< 03-scan, scanning-error, Method nextToken()
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    throw new ScanningError("Unexpected char.", new Token.Position(line, column));
                }
                //>>
            }
        }
    }

    /**
     * Scans an identifier or keyword.
     */
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        var text = source.substring(start, current);
        var type = keywords.getOrDefault(text, IDENTIFIER);
        if (type == IDENTIFIER) {
            addToken(type, text);
        } else {
            addToken(type);
        }
    }

    /**
     * Scans a numeric value.
     */
    private void number() {
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) advance();
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    /**
     * Scans a string value.
     */
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            advance();
        }
        if (isAtEnd()) {
            throw new ScanningError("Unterminated string.", new Token.Position(line, column));
        }
        advance();
        var value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    /**
     * Checks if the next character matches the expected character.
     *
     * @param expected The expected character.
     * @return True if matched, false otherwise.
     */
    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) return false;
        advance();
        return true;
    }

    /**
     * Peeks at the current character without advancing.
     *
     * @return The current character or '\0' if at the end.
     */
    private char peek() {
        return isAtEnd() ? '\0' : source.charAt(current);
    }

    /**
     * Peeks at the next character without advancing.
     *
     * @return The next character or '\0' if at the end.
     */
    private char peekNext() {
        return (current + 1 >= source.length()) ? '\0' : source.charAt(current + 1);
    }

    /**
     * Checks if a character is alphabetic.
     *
     * @param c The character to check.
     * @return True if alphabetic, false otherwise.
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    /**
     * Checks if a character is alphanumeric.
     *
     * @param c The character to check.
     * @return True if alphanumeric, false otherwise.
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Checks if a character is a digit.
     *
     * @param c The character to check.
     * @return True if a digit, false otherwise.
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Checks if the scanner has reached the end of the source.
     *
     * @return True if at the end, false otherwise.
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Advances to the next character and returns the current character.
     *
     * @return The next character.
     */
    private char advance() {
        var c = source.charAt(current++);
        advanceLine(c);
        return c;
    }

    /**
     * Adds a token to the token list.
     *
     * @param type The type of token.
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Adds a token with a value to the token list.
     *
     * @param type  The type of token.
     * @param value The value of the token.
     */
    private void addToken(TokenType type, Object value) {
        var text = source.substring(start, current);
        tokens.add(new Token(type, text, value, new Token.Position(line, column - text.length() + 1)));
    }
}
