package de.thm.asc.tiel.interpreter.lexical;

/**
 * The TokenType enum defines the various types of tokens
 * that can be recognized in the source code during lexical analysis.
 */
//<< 03-scan, token-types, Aufzählungswerte Grundsymbole
public enum TokenType {
    IDENTIFIER,     // Variable or function name

    // Special symbols
    LEFT_PAREN,     // (
    RIGHT_PAREN,    // )
    LEFT_BRACE,     // {
    RIGHT_BRACE,    // }
    LEFT_BRACKET,   // [
    RIGHT_BRACKET,  // ]
    COMMA,          // ,
    PLUS,           // +
    MINUS,          // -
    //>>
    STAR,           // *
    SLASH,          // /
    SEMICOLON,      // ;
    EQUAL,          // =
    EQUAL_EQUAL,    // ==
    NOT_EQUAL,      // !=
    LESS,           // <
    LESS_EQUAL,     // <=
    GREATER,        // >
    GREATER_EQUAL,  // >=


    // Keywords
    FUN,        // Function definition keyword
    VAR,        // Variable declaration keyword
    IF,         // Conditional statement keyword
    ELSE,       // Begins an else block in conditional statements
    ELIF,       // Begins an else-if block in conditional statements
    WHILE,      // Loop keyword
    RETURN,     // Return statement keyword
    AND,        // Logical AND operator
    OR,         // Logical OR operator
    NOT,        // Logical NOT operator
    TRUE,       // Boolean literal 'true'
    FALSE,      // Boolean literal 'false'
    NIL,        // Null-like literal keyword

    // Literals
    NUMBER,         // Numeric literal
    STRING,         // String literal

    // End of file marker
    EOF            // Signals the end of the source input
    ;
}
