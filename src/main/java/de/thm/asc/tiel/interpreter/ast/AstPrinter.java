package de.thm.asc.tiel.interpreter.ast;

import de.thm.asc.tiel.interpreter.ast.expr.*;
import de.thm.asc.tiel.interpreter.ast.stmt.*;
import de.thm.asc.tiel.interpreter.lexical.Token;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AstPrinter {

    /**
     * Prints the string representation of an expression.
     *
     * @param expr The expression to print.
     * @return The formatted string representation.
     */
    public String print(Expr expr) {
        return switch (expr) {
            case AssignExpr assignExpr -> sExpr(AssignExpr.class.getSimpleName(), assignExpr.target, assignExpr.value);
            case BinaryExpr binaryExpr ->
                    sExpr(BinaryExpr.class.getSimpleName(), binaryExpr.operator.toString(), binaryExpr.left, binaryExpr.right);
            case CallExpr callExpr -> sExpr(CallExpr.class.getSimpleName(), callExpr.callee, callExpr.arguments);
            case LiteralExpr literalExpr -> {
                if (literalExpr.value == null) yield sExpr(LiteralExpr.class.getSimpleName(), "nil");
                yield sExpr(LiteralExpr.class.getSimpleName(), literalExpr.value.toString());
            }
            case LogicalExpr logicalExpr ->
                    sExpr(LogicalExpr.class.getSimpleName(), logicalExpr.operator.toString(), logicalExpr.left, logicalExpr.right);
            case UnaryExpr unaryExpr ->
                    sExpr(UnaryExpr.class.getSimpleName(), unaryExpr.operator.toString(), unaryExpr.right);
            case VariableExpr variableExpr -> sExpr(VariableExpr.class.getSimpleName(), variableExpr.name);
        };
    }

    /**
     * Prints the string representation of a statement.
     *
     * @param stmt The statement to print.
     * @return The formatted string representation.
     */
    public String print(Stmt stmt) {
        return switch (stmt) {
            case BlockStmt blockStmt -> sExpr(BlockStmt.class.getSimpleName(), blockStmt.statements.toArray());
            case ExpressionStmt expressionStmt ->
                    sExpr(ExpressionStmt.class.getSimpleName(), expressionStmt.expression);
            case FunctionDeclStmt functionDeclStmt -> {
                var params = functionDeclStmt.params.stream().toArray();
                var body = functionDeclStmt.body.statements.toArray();

                yield sExpr(FunctionDeclStmt.class.getSimpleName(),
                        functionDeclStmt.name,
                        sExpr("Params", params), sExpr("Body", body));
            }
            case IfStmt ifStmt -> {
                if (ifStmt.elseBranch == null)
                    yield sExpr(IfStmt.class.getSimpleName(), ifStmt.condition, ifStmt.thenBranch);
                yield sExpr(IfStmt.class.getSimpleName(), ifStmt.condition, ifStmt.thenBranch, ifStmt.elseBranch);
            }
            case ReturnStmt returnStmt -> {
                if (returnStmt.value == null) yield sExpr(ReturnStmt.class.getSimpleName());
                yield sExpr(ReturnStmt.class.getSimpleName(), returnStmt.value);
            }
            case VarDeclStmt varDeclStmt ->
                    sExpr(VarDeclStmt.class.getSimpleName(), varDeclStmt.name, varDeclStmt.initializer);
            case WhileStmt whileStmt -> sExpr(WhileStmt.class.getSimpleName(), whileStmt.condition, whileStmt.body);
        };
    }

    /**
     * Prints a list of statements, each on a new line.
     *
     * @param stmts The list of statements to print.
     * @return The formatted string representation.
     */
    public String print(List<Stmt> stmts) {
        var sb = new StringBuilder();
        for (var s : stmts) {
            sb.append(print(s)).append("\r\n");
        }
        return sb.toString();
    }

    /**
     * Generates an s-expression from the passed arguments.
     *
     * @param name  The name of the s-expression (the first element).
     * @param parts The arguments of the s-expression.
     * @return The formatted s-expression.
     */
    private String sExpr(String name, Object... parts) {
        var joinedParts = Arrays.stream(parts)
                .map(this::stringify)
                .collect(Collectors.joining(" "));
        return String.format("(%s%s)", name, (joinedParts.isEmpty() ? "" : " " + joinedParts));
    }

    /**
     * Transparently converts a given object to the appropriate string representation.
     *
     * @param o The object to stringify.
     * @return The string representation of the object.
     */
    private String stringify(Object o) {
        return switch (o) {
            case Expr expr -> print(expr);
            case Stmt stmt -> print(stmt);
            case Token token -> token.lexeme();
            case List<?> list -> list.stream().map(this::stringify).collect(Collectors.joining(" "));
            case null -> "nil";
            default -> o.toString();
        };
    }
}
