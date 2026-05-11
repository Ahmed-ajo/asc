package de.thm.asc.tiel.interpreter.evaluation;

import de.thm.asc.tiel.interpreter.ast.expr.*;
import de.thm.asc.tiel.interpreter.ast.stmt.*;
import de.thm.asc.tiel.interpreter.error.RuntimeError;
import de.thm.asc.tiel.interpreter.lexical.Token;

import java.util.*;

/**
 * The Evaluator class interprets and executes TiEL language expressions and statements.
 * It maintains an execution environment and supports variable resolution, function calls,
 * and basic control flow operations.
 */
public class Evaluator {

    /**
     * The current environment.
     */
    private Environment environment;

    public final Environment globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    /**
     * Constructs an Evaluator and defines built-in functions.
     */
    public Evaluator(Environment globals) {
        this.globals = globals;
        this.environment = globals;
    }

    /**
     * Interprets a list of statements by executing them sequentially.
     *
     * @param statements The statements to interpret.
     */
    public void interpret(List<Stmt> statements) {
        for (var s : statements) {
            execute(s);
        }
    }

    /**
     * Puts the environment depth of an expression in the locals table.
     *
     * @param expr  The expression to resolve.
     * @param depth The depth at which the expression is to be resolved.
     */
    public void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    /**
     * Looks up the value of a variable.
     *
     * @param name The name of the variable to lookup (used for 'this').
     * @param expr The variable to lookup.
     * @return The value of the variable.
     */
    //<< 09-expr, lookup, Methode lookupVariable()
    private TiELValue lookupVariable(String name, Expr expr) {
        var distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name);
        } else {
            return globals.get(name, expr.getPosition());
        }
    }
    //>>

    /**
     * Evaluates an expression.
     *
     * @param expr The expression to evaluate.
     * @return The evaluated value-
     */
    private TiELValue evaluate(Expr expr) {
        return switch (expr) {
            case AssignExpr assignExpr -> {
                var value = evaluate(assignExpr.value);

                if (assignExpr.target instanceof VariableExpr v) {
                    var distance = locals.get(expr);
                    if (distance != null) {
                        environment.assignAt(distance, v.name, value);
                    } else {
                        globals.assign(v.name, value, assignExpr.getPosition());
                    }
                } else if (assignExpr.target instanceof IndexExpr indexExpr) {
                    var array = evaluateArrayTarget(indexExpr.target, assignExpr.getPosition());
                    var index = evaluateArrayIndex(indexExpr.index, assignExpr.getPosition());
                    checkArrayBounds(index, array, assignExpr.getPosition());

                    array.value().set(index, value);
                } else {
                    throw new IllegalStateException("Unexpected assignment target type");
                }

                yield value;
            }
            case BinaryExpr binaryExpr -> {
                var left = evaluate(binaryExpr.left);
                var right = evaluate(binaryExpr.right);

                yield switch (binaryExpr.operator) {
                    case EQ -> wrap(left.equals(right));
                    case NEQ -> wrap(!left.equals(right));
                    //<< 09-expr, post-order, Methode evaluate()
                    case LT -> {
                        checkNumberOperands(binaryExpr.operator, left, right, binaryExpr.getPosition());
                        yield wrap(((TiELValue.TNumber) left).value() < ((TiELValue.TNumber) right).value());
                    }
                    //>>
                    case LE -> {
                        checkNumberOperands(binaryExpr.operator, left, right, binaryExpr.getPosition());
                        yield wrap(((TiELValue.TNumber) left).value() <= ((TiELValue.TNumber) right).value());
                    }
                    case GT -> {
                        checkNumberOperands(binaryExpr.operator, left, right, binaryExpr.getPosition());
                        yield wrap(((TiELValue.TNumber) left).value() > ((TiELValue.TNumber) right).value());
                    }
                    case GE -> {
                        checkNumberOperands(binaryExpr.operator, left, right, binaryExpr.getPosition());
                        yield wrap(((TiELValue.TNumber) left).value() >= ((TiELValue.TNumber) right).value());
                    }
                    case SUB -> {
                        checkNumberOperands(binaryExpr.operator, left, right, binaryExpr.getPosition());
                        yield wrap(((TiELValue.TNumber) left).value() - ((TiELValue.TNumber) right).value());
                    }
                    case ADD -> {
                        checkNumberOperands(binaryExpr.operator, left, right, binaryExpr.getPosition());
                        yield wrap(((TiELValue.TNumber) left).value() + ((TiELValue.TNumber) right).value());
                    }
                    case DIV -> {
                        checkNumberOperands(binaryExpr.operator, left, right, binaryExpr.getPosition());
                        yield wrap(((TiELValue.TNumber) left).value() / ((TiELValue.TNumber) right).value());
                    }
                    case MUL -> {
                        checkNumberOperands(binaryExpr.operator, left, right, binaryExpr.getPosition());
                        yield wrap(((TiELValue.TNumber) left).value() * ((TiELValue.TNumber) right).value());
                    }
                };
            }
            case CallExpr callExpr -> {
                var callee = evaluate(callExpr.callee);

                var arguments = new ArrayList<TiELValue>();
                for (var argument : callExpr.arguments) {
                    arguments.add(evaluate(argument));
                }

                if (!(callee instanceof TiELCallable function)) {
                    throw new RuntimeError("Can only call functions and classes.", callExpr.getPosition());
                }

                if (arguments.size() != function.arity()) {
                    throw new RuntimeError(
                            String.format("Expected %s arguments but got %s.", function.arity(), arguments.size()),
                            callExpr.getPosition()
                    );
                }

                yield function.call(this, arguments, callExpr.getPosition());
            }
            case ArrayExpr arrayExpr -> {
                var elements = new ArrayList<TiELValue>();
                for (var element : arrayExpr.elements) {
                    elements.add(evaluate(element));
                }

                yield new TiELValue.TArray(elements);
            }
            case IndexExpr indexExpr -> {
                var array = evaluateArrayTarget(indexExpr.target, indexExpr.getPosition());
                var index = evaluateArrayIndex(indexExpr.index, indexExpr.getPosition());
                checkArrayBounds(index, array, indexExpr.getPosition());

                yield array.value().get(index);
            }
            case LiteralExpr literalExpr -> literalExpr.value;
            case LogicalExpr logicalExpr -> {
                var left = evaluate(logicalExpr.left);

                if (logicalExpr.operator == LogicalExpr.Operator.OR) {
                    if (isTruthy(left)) yield left;
                } else {
                    if (!isTruthy(left)) yield left;
                }

                yield evaluate(logicalExpr.right);
            }
            case UnaryExpr unaryExpr -> {
                var right = evaluate(unaryExpr.right);

                yield switch (unaryExpr.operator) {
                    case NOT -> wrap(!isTruthy(right));
                    case MINUS -> {
                        checkNumberOperand(unaryExpr.operator, right, unaryExpr.getPosition());
                        yield wrap(-((TiELValue.TNumber) right).value());
                    }
                };
            }
            case VariableExpr variableExpr -> lookupVariable(variableExpr.name, variableExpr);
        };
    }

    public void executeStatementsInEnvironment(List<Stmt> statements, Environment environment) {
        var previousEnv = this.environment;

        try {
            this.environment = environment;

            interpret(statements);
        } finally {
            this.environment = previousEnv;
        }
    }

    /**
     * Executes a statement.
     *
     * @param stmt The statement to execute.
     */
    private void execute(Stmt stmt) {
        switch (stmt) {
            case BlockStmt blockStmt ->
                    executeStatementsInEnvironment(blockStmt.statements, new Environment(environment));
            case ExpressionStmt expressionStmt -> evaluate(expressionStmt.expression);
            //<< 09-expr, declaration, Methode execute()
            case FunctionDeclStmt functionDeclStmt -> {
                var function = new TiELFunction(functionDeclStmt, environment, false);

                environment.define(functionDeclStmt.name, function);
            }
            //>>
            case IfStmt ifStmt -> {
                if (isTruthy(evaluate(ifStmt.condition))) {
                    execute(ifStmt.thenBranch);
                } else if (ifStmt.elseBranch != null) {
                    execute(ifStmt.elseBranch);
                }
            }
            case ReturnStmt returnStmt -> {
                TiELValue value = TiELValue.NIL;
                if (returnStmt.value != null) value = evaluate(returnStmt.value);

                throw new ReturnException(value);
            }
            case VarDeclStmt varDeclStmt -> {
                var value = evaluate(varDeclStmt.initializer);

                environment.define(varDeclStmt.name, value);
            }
            case WhileStmt whileStmt -> {
                while (isTruthy(evaluate(whileStmt.condition))) {
                    execute(whileStmt.body);
                }
            }
        }
    }

    /**
     * Ensures that two operands are numbers, throwing an error if not.
     *
     * @param operator      The operator token.
     * @param left          The left operand.
     * @param right         The right operand.
     * @param errorPosition The position at which an error will be thrown.
     */
    private void checkNumberOperands(BinaryExpr.Operator operator, TiELValue left, TiELValue right, Token.Position errorPosition) {
        if (left instanceof TiELValue.TNumber && right instanceof TiELValue.TNumber) return;
        throw new RuntimeError(String.format("Operands to '%s' must be numbers.", operator.toString()), errorPosition);
    }
    private TiELValue.TArray evaluateArrayTarget(Expr targetExpr, Token.Position errorPosition) {
        var target = evaluate(targetExpr);
        if (target instanceof TiELValue.TArray array) {
            return array;
        }
        throw new RuntimeError("Index target must be an array.", errorPosition);
    }

    private int evaluateArrayIndex(Expr indexExpr, Token.Position errorPosition) {
        var indexValue = evaluate(indexExpr);
        if (!(indexValue instanceof TiELValue.TNumber(var rawIndex))) {
            throw new RuntimeError("Array index must be a number.", errorPosition);
        }

        if (rawIndex != Math.floor(rawIndex)) {
            throw new RuntimeError("Array index must be an integer.", errorPosition);
        }

        var asInt = (int) rawIndex;
        return asInt;
    }

    private void checkArrayBounds(int index, TiELValue.TArray array, Token.Position errorPosition) {
        if (index >= 0 && index < array.value().size()) {
            return;
        }
        throw new RuntimeError("Array index out of bounds.", errorPosition);
    }

    /**
     * Ensures that an operand is a number, throwing an error if not.
     *
     * @param operator      The operator token.
     * @param operand       The operand.
     * @param errorPosition The position at which an error will be thrown.
     */
    private void checkNumberOperand(UnaryExpr.Operator operator, TiELValue operand, Token.Position errorPosition) {
        if (operand instanceof TiELValue.TNumber) return;
        throw new RuntimeError(String.format("Operand to '%s' must be a number.", operator.toString()), errorPosition);
    }

    /**
     * Checks if a value is truthy according to TiEL's rules.
     *
     * @param value The value to check.
     * @return True if the object is truthy, false otherwise.
     */
    private static boolean isTruthy(TiELValue value) {
        if (value instanceof TiELValue.TNil) return false;
        if (value instanceof TiELValue.TBoolean(boolean v)) return v;
        return true;
    }

    /**
     * Wraps a double value in a TiEL number type.
     *
     * @param number The value to wrap.
     * @return The wrapped value.
     */
    private static TiELValue wrap(double number) {
        return new TiELValue.TNumber(number);
    }

    /**
     * Wraps a boolean value in a TiEL boolean type.
     *
     * @param bool The value to wrap.
     * @return The wrapped value.
     */
    private static TiELValue wrap(boolean bool) {
        return new TiELValue.TBoolean(bool);
    }
}
