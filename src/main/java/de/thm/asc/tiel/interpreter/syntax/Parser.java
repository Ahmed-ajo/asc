package de.thm.asc.tiel.interpreter.syntax;

import de.thm.asc.tiel.interpreter.ast.expr.*;
import de.thm.asc.tiel.interpreter.ast.stmt.*;
import de.thm.asc.tiel.interpreter.error.ParsingError;
import de.thm.asc.tiel.interpreter.evaluation.TiELValue;
import de.thm.asc.tiel.interpreter.lexical.Token;
import de.thm.asc.tiel.interpreter.lexical.TokenType;

import java.util.ArrayList;
import java.util.List;

import static de.thm.asc.tiel.interpreter.lexical.TokenType.*;

/**
 * The Parser class is responsible for parsing a list of tokens into an Abstract Syntax Tree (AST).
 * It processes statements and expressions according to the language's grammar rules.
 */
public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    /**
     * Constructs a new Parser with the provided list of tokens.
     *
     * @param tokens The list of tokens to parse.
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses the token list into a list of statements.
     *
     * @return A list of parsed statements.
     */
    //<< 06-parse, parser-parse, Methode parse()
    public List<Stmt> parse() {
        var statements = new ArrayList<Stmt>();

        while (!isAtEnd()) {
            statements.add(statement());
        }

        return statements;
    }
    //>>

    /**
     * Parses an expression.
     *
     * @return The parsed expression.
     */
    private Expr expression() {
        return assignment();
    }

    /**
     * Parses a statement.
     *
     * @return The parsed statement.
     */
    //<< 06-parse, parser-statement, Methode statement()
    private Stmt statement() {
        if (match(IF)) return ifStatement();
        if (match(RETURN)) return returnStatement();
        if (match(WHILE)) return whileStatement();
        if (match(LEFT_BRACE)) return block();
        if (match(VAR)) return varDeclaration();
        if (match(FUN)) return function();

        return expressionStatement();
        //>>
    }

    /**
     * Parses an if statement.
     *
     * @return The parsed if statement.
     */
    //<< 06-parse, parser-if, Methode ifStatement()
    private Stmt ifStatement() {
        var conditions = new ArrayList<Expr>();
        var branches = new ArrayList<Stmt>();
        var positions = new ArrayList<Token.Position>();

        do {
            positions.add(previous().position());
            conditions.add(expression());
            consume(LEFT_BRACE, "Expected '{' after if statement condition");
            branches.add(block());
        } while (match(ELIF));

        Stmt elseBranch = null;
        if (match(ELSE)) {
            consume(LEFT_BRACE, "Expected '{' after 'else'");
            elseBranch = block();
        }
        //>>
        for (var i = conditions.size() - 1; i >= 0; i--) {
            elseBranch = new IfStmt(conditions.get(i), branches.get(i), elseBranch).withPosition(positions.get(i));
        }

        return elseBranch;
    }

    /**
     * Parses a return statement.
     *
     * @return The parsed return statement.
     */
    private Stmt returnStatement() {
        var position = previous().position();
        Expr value = null;

        if (!check(SEMICOLON)) {
            value = expression();
        }

        consume(SEMICOLON, "Expected ';' after return value");

        return new ReturnStmt(value).withPosition(position);
    }

    /**
     * Parses a variable declaration statement.
     *
     * @return The parsed variable declaration.
     */
    private Stmt varDeclaration() {
        var position = previous().position();
        var name = consume(IDENTIFIER, "Expected variable name");

        consume(EQUAL, "Expected '=' after variable name");

        var initializer = expression();

        consume(SEMICOLON, "Expected ';' after variable declaration");

        return new VarDeclStmt((String) name.value(), initializer).withPosition(position);
    }

    /**
     * Parses a while statement.
     *
     * @return The parsed while statement.
     */
    private Stmt whileStatement() {
        var position = previous().position();
        var condition = expression();

        consume(LEFT_BRACE, "Expected block after while condition");
        var body = block();

        return new WhileStmt(condition, body).withPosition(position);
    }

    /**
     * Parses an expression statement.
     *
     * @return The parsed expression statement.
     */
    private Stmt expressionStatement() {
        var expr = expression();
        consume(SEMICOLON, "Expected ';' after expression");
        return new ExpressionStmt(expr).withPosition(expr.getPosition());
    }

    /**
     * Parses a function declaration.
     *
     * @return The parsed function declaration.
     */
    private Stmt function() {
        var position = previous().position();
        var name = consume(IDENTIFIER, "Expected function name");
        consume(LEFT_PAREN, "Expected '(' after function name");
        var parameters = new ArrayList<Token>();
        if (!check(RIGHT_PAREN)) {

            do {
                parameters.add(consume(IDENTIFIER, "Expected parameter name"));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expected ')' after parameters");

        consume(LEFT_BRACE, "Expected '{' before function body");

        var body = block();

        return new FunctionDeclStmt(
                (String) name.value(),
                parameters.stream().map(p -> (String) p.value()).toList(),
                body
        ).withPosition(position);
    }

    /**
     * Parses a block statement.
     *
     * @return The list of parsed statements inside the block.
     */
    private BlockStmt block() {
        var position = previous().position();
        var statements = new ArrayList<Stmt>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(statement());
        }

        consume(RIGHT_BRACE, "Expected '}' after block");
        return (BlockStmt) new BlockStmt(statements).withPosition(position);
    }

    /**
     * Parses an assignment statement.
     *
     * @return The parsed assignment statement.
     */
    private Expr assignment() {
        var expr = or();

        if (match(EQUAL)) {
            var equals = previous();
            var value = assignment();

            if (expr instanceof VariableExpr) {
                return new AssignExpr(expr, value).withPosition(expr.getPosition());
            }
            if (expr instanceof IndexExpr) {
                return new AssignExpr(expr, value).withPosition(expr.getPosition());
            }

            throw new ParsingError("Expected variable expression left of =", equals.position());
        }

        return expr;
    }

    /**
     * Parses logical OR expressions.
     *
     * @return The parsed OR expression.
     */
    private Expr or() {
        var expr = and();

        while (match(OR)) {
            var position = previous().position();
            var right = and();
            expr = new LogicalExpr(expr, LogicalExpr.Operator.OR, right).withPosition(position);
        }

        return expr;
    }

    /**
     * Parses logical AND expressions.
     *
     * @return The parsed AND expression.
     */
    private Expr and() {
        var expr = equality();

        while (match(AND)) {
            var position = previous().position();
            var right = equality();
            expr = new LogicalExpr(expr, LogicalExpr.Operator.AND, right).withPosition(position);
        }

        return expr;
    }

    /**
     * Parses equality expressions (e.g., ==).
     *
     * @return The parsed equality expression.
     */
    private Expr equality() {
        var expr = comparison();

        while (match(EQUAL_EQUAL, NOT_EQUAL)) {
            var position = previous().position();
            var operator = previous().type() == EQUAL_EQUAL ? BinaryExpr.Operator.EQ : BinaryExpr.Operator.NEQ;

            var right = comparison();
            expr = new BinaryExpr(expr, operator, right).withPosition(position);
        }

        return expr;
    }

    /**
     * Parses comparison expressions (e.g., <).
     *
     * @return The parsed comparison expression.
     */
    private Expr comparison() {
        var expr = term();

        while (match(LESS, LESS_EQUAL, GREATER, GREATER_EQUAL)) {
            var position = previous().position();
            var operator = switch (previous().type()) {
                case LESS -> BinaryExpr.Operator.LT;
                case LESS_EQUAL -> BinaryExpr.Operator.LE;
                case GREATER -> BinaryExpr.Operator.GT;
                case GREATER_EQUAL -> BinaryExpr.Operator.GE;
                default -> throw new RuntimeException("Impossible state");
            };

            var right = term();

            expr = new BinaryExpr(expr, operator, right).withPosition(position);
        }

        return expr;
    }

    /**
     * Parses term expressions (e.g., +, -).
     *
     * @return The parsed term expression.
     */
    //<< 06-parse, parser-term, Methode term()
    private Expr term() {
        var expr = factor();

        while (match(MINUS, PLUS)) {
            var operator = previous();

            var op = operator.type() == MINUS ? BinaryExpr.Operator.SUB : BinaryExpr.Operator.ADD;

            var right = factor();
            expr = new BinaryExpr(expr, op, right).withPosition(operator.position());
        }

        return expr;
    }
    //>>

    /**
     * Parses factor expressions (e.g., *, /).
     *
     * @return The parsed factor expression.
     */
    private Expr factor() {
        var expr = unary();

        while (match(SLASH, STAR)) {
            var operator = previous();

            var op = operator.type() == SLASH ? BinaryExpr.Operator.DIV : BinaryExpr.Operator.MUL;

            var right = unary();
            expr = new BinaryExpr(expr, op, right).withPosition(operator.position());
        }

        return expr;
    }

    /**
     * Parses unary expressions (e.g., not, -).
     *
     * @return The parsed unary expression.
     */
    private Expr unary() {
        if (match(NOT, MINUS)) {
            var operator = previous();

            var op = operator.type() == NOT ? UnaryExpr.Operator.NOT : UnaryExpr.Operator.MINUS;

            var right = unary();
            return new UnaryExpr(op, right).withPosition(operator.position());
        }

        return call();
    }

    /**
     * Completes parsing of a function call by parsing its arguments.
     *
     * @param callee The expression representing the function being called.
     * @return The completed function call expression.
     */
    private Expr finishCall(Expr callee) {
        var arguments = new ArrayList<Expr>();
        if (!check(RIGHT_PAREN)) {
            do {
                arguments.add(expression());
            } while (match(COMMA));
        }

        consume(RIGHT_PAREN, "Expected ')' after arguments");

        return new CallExpr(callee, arguments).withPosition(callee.getPosition());
    }

    /**
     * Parses function call expressions.
     *
     * @return The parsed call expression.
     */
    private Expr call() {
        var expr = primary();

        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(LEFT_BRACKET)) {
                var index = expression();
                consume(RIGHT_BRACKET, "Expected ']' after index expression");
                expr = new IndexExpr(expr, index).withPosition(expr.getPosition());
            }  else {
                break;
            }
        }

        return expr;
    }

    /**
     * Parses primary expressions (literals, identifiers, and grouped expressions).
     *
     * @return The parsed primary expression.
     */
    private Expr primary() {
        if (match(FALSE)) return new LiteralExpr(TiELValue.FALSE).withPosition(previous().position());
        if (match(TRUE)) return new LiteralExpr(TiELValue.TRUE).withPosition(previous().position());
        if (match(NIL)) return new LiteralExpr(TiELValue.NIL).withPosition(previous().position());

        if (match(NUMBER)) {
            return new LiteralExpr(new TiELValue.TNumber((Double) previous().value()))
                    .withPosition(previous().position());
        }

        if (match(STRING)) {
            return new LiteralExpr(new TiELValue.TString((String) previous().value()))
                    .withPosition(previous().position());
        }

        if (match(IDENTIFIER)) {
            return new VariableExpr((String) previous().value())
                    .withPosition(previous().position());
        }

        if (match(LEFT_BRACKET)) {
            var position = previous().position();
            var elements = new ArrayList<Expr>();
            if (!check(RIGHT_BRACKET)) {
                do {
                    elements.add(expression());
                } while (match(COMMA));
            }
            consume(RIGHT_BRACKET, "Expected ']' after array literal");
            return new ArrayExpr(elements).withPosition(position);
        }

        if (match(LEFT_PAREN)) {
            var expr = expression();
            consume(RIGHT_PAREN, "Expected ')' after expression");
            return expr;
        }

        throw parsingErrorWithCurrentToken("Expected expression", peek().position());
    }

    /**
     * Consumes a token of the given type.
     *
     * @param type    The type to consume.
     * @param message The error message to display when the current type does not match the expected type.
     * @return Null (unreachable)
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw parsingErrorWithCurrentToken(message, peek().position());
    }

    /**
     * Checks if the next token is of the given type. A fixed lookahead of one token means we are doing LL(1) parsing.
     *
     * @param type The type to check for.
     * @return True if types match, false otherwise.
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    /**
     * Advances to the next token if not at the end.
     *
     * @return The previous token.
     */
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    /**
     * Checks if the parser has reached the end of the token list.
     *
     * @return True if at the end, false otherwise.
     */
    private boolean isAtEnd() {
        return peek().type() == EOF;
    }

    /**
     * Returns the current token without consuming it.
     *
     * @return The current token.
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Returns the previous token.
     *
     * @return The previous token.
     */
    private Token previous() {
        return tokens.get(current - 1);
    }

    /**
     * Checks if the current token matches any of the given types and advances if true.
     *
     * @param types The token types to match against.
     * @return {@code true} if a match is found, otherwise {@code false}.
     */
    private boolean match(TokenType... types) {
        for (var type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    /**
     * Creates a new instance of ParsingError with an error message containing the expected string and position.
     * @param expected String representing the expected unit.
     * @param position Position in source code at which the error occurred.
     * @return The new instance.
     */
    private ParsingError parsingErrorWithCurrentToken(String expected, Token.Position position) {
        return new ParsingError("%s, but got %s.".formatted(expected, peek().type()), position);
    }
}
