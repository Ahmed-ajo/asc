package de.thm.asc.tiel.interpreter.lexical;

/**
 * A Token represents a lexical token in the source code.
 *
 * @param type     Type of the token
 * @param lexeme   The actual text of the token as read in the source program.
 * @param value    A value that is optional and, hence, may be null.
 * @param position Position (line/column) of the source program in which token occurs.
 */
//<< 03-scan, token-record, Signatur des Records
public record Token(TokenType type,
                    String lexeme,
                    Object value,
                    Position position) { // line number used for logging purposes
//>>

    /**
     * Returns a string representation of the token for debugging purposes.
     *
     * @return A formatted string representing the token.
     */
    @Override
    public String toString() {
        return "TOKEN(%s, %s, %s) @ line %s, column %s".formatted(type, lexeme, value, position.line, position.column);
    }

    public record Position(int line, int column) {

        public static final Position ZERO = new Position(0, 0);

        @Override
        public String toString() {
            return String.format("line %s, column %s", line, column);
        }
    }
}

