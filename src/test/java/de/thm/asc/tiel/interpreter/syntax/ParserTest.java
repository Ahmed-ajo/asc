package de.thm.asc.tiel.interpreter.syntax;

import de.thm.asc.tiel.interpreter.ast.expr.AssignExpr;
import de.thm.asc.tiel.interpreter.ast.expr.BinaryExpr;
import de.thm.asc.tiel.interpreter.ast.expr.CallExpr;
import de.thm.asc.tiel.interpreter.ast.expr.LiteralExpr;
import de.thm.asc.tiel.interpreter.ast.expr.LogicalExpr;
import de.thm.asc.tiel.interpreter.ast.expr.UnaryExpr;
import de.thm.asc.tiel.interpreter.ast.expr.VariableExpr;
import de.thm.asc.tiel.interpreter.ast.stmt.BlockStmt;
import de.thm.asc.tiel.interpreter.ast.stmt.ExpressionStmt;
import de.thm.asc.tiel.interpreter.ast.stmt.FunctionDeclStmt;
import de.thm.asc.tiel.interpreter.ast.stmt.IfStmt;
import de.thm.asc.tiel.interpreter.ast.stmt.ReturnStmt;
import de.thm.asc.tiel.interpreter.ast.stmt.Stmt;
import de.thm.asc.tiel.interpreter.ast.stmt.VarDeclStmt;
import de.thm.asc.tiel.interpreter.ast.stmt.WhileStmt;
import de.thm.asc.tiel.interpreter.evaluation.TiELValue;
import de.thm.asc.tiel.interpreter.lexical.Scanner;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {

    @Test
    void parsesFunctionWithControlFlowAndExpressions() {
        var actual = parse("""
                fun decide(flag, n) {
                    var total = 1 + 2 * 3;
                    if not flag or n < 10 and true {
                        return total;
                    } elif n == 0 {
                        return -1;
                    } else {
                        while n > 0 {
                            n = n - 1;
                        }
                        return foo(total, n);
                    }
                }
                """);

        var expected = List.of(
                new FunctionDeclStmt(
                        "decide",
                        List.of("flag", "n"),
                        new BlockStmt(List.of(
                                new VarDeclStmt(
                                        "total",
                                        new BinaryExpr(
                                                number(1),
                                                BinaryExpr.Operator.ADD,
                                                new BinaryExpr(number(2), BinaryExpr.Operator.MUL, number(3))
                                        )
                                ),
                                new IfStmt(
                                        new LogicalExpr(
                                                new UnaryExpr(UnaryExpr.Operator.NOT, variable("flag")),
                                                LogicalExpr.Operator.OR,
                                                new LogicalExpr(
                                                        new BinaryExpr(variable("n"), BinaryExpr.Operator.LT, number(10)),
                                                        LogicalExpr.Operator.AND,
                                                        bool(true)
                                                )
                                        ),
                                        new BlockStmt(List.of(
                                                new ReturnStmt(variable("total"))
                                        )),
                                        new IfStmt(
                                                new BinaryExpr(variable("n"), BinaryExpr.Operator.EQ, number(0)),
                                                new BlockStmt(List.of(
                                                        new ReturnStmt(
                                                                new UnaryExpr(UnaryExpr.Operator.MINUS, number(1))
                                                        )
                                                )),
                                                new BlockStmt(List.of(
                                                        new WhileStmt(
                                                                new BinaryExpr(variable("n"), BinaryExpr.Operator.GT, number(0)),
                                                                new BlockStmt(List.of(
                                                                        new ExpressionStmt(
                                                                                new AssignExpr(
                                                                                        variable("n"),
                                                                                        new BinaryExpr(
                                                                                                variable("n"),
                                                                                                BinaryExpr.Operator.SUB,
                                                                                                number(1)
                                                                                        )
                                                                                )
                                                                        )
                                                                ))
                                                        ),
                                                        new ReturnStmt(
                                                                new CallExpr(variable("foo"), List.of(variable("total"), variable("n")))
                                                        )
                                                ))
                                        )
                                )
                        ))
                )
        );

        assertEquals(expected, actual);
    }

    private static List<Stmt> parse(String source) {
        return new Parser(new Scanner(source).scan()).parse();
    }

    private static VariableExpr variable(String name) {
        return new VariableExpr(name);
    }

    private static LiteralExpr number(double value) {
        return new LiteralExpr(new TiELValue.TNumber(value));
    }

    private static LiteralExpr bool(boolean value) {
        return new LiteralExpr(new TiELValue.TBoolean(value));
    }
}
