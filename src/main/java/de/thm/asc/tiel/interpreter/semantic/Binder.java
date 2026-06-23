package de.thm.asc.tiel.interpreter.semantic;

import de.thm.asc.tiel.interpreter.ast.expr.*;
import de.thm.asc.tiel.interpreter.ast.stmt.*;
import de.thm.asc.tiel.interpreter.error.RuntimeError;
import de.thm.asc.tiel.interpreter.evaluation.Evaluator;
import de.thm.asc.tiel.interpreter.lexical.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * The Binder performs a pre-execution pass over the AST to resolve lexical scopes. It determines where each variable
 * is defined and records how far to look it up at runtime. It also enforces static semantic rules (e.g., invalid
 * returns and no-use-before-define).
 */
public class Binder {

    /**
     * Describes the function context currently being resolved.
     */
    private enum FunctionType {
        /**
         * Not currently inside a function.
         */
        NONE,
        /**
         * Inside a regular function.
         */
        FUNCTION
    }
    /**
     * Evaluator that receives resolved scope distances.
     */
    private final Evaluator evaluator;

    /**
     * Stack of lexical scopes currently in effect.
     */
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();

    /**
     * Current enclosing function context.
     */
    private FunctionType currentFunction = FunctionType.NONE;

    /**
     * Creates a new binder for the given evaluator.
     *
     * @param evaluator Evaluator that stores resolution results.
     */
    public Binder(Evaluator evaluator) {
        this.evaluator = evaluator;

        var predefinedGlobals = evaluator.globals.values.keySet().stream()
                .collect(Collectors.toMap(name -> name, _ -> true));

        scopes.push(predefinedGlobals);
    }

    /**
     * Resolves all statements in the given program fragment.
     *
     * @param statements Statements to resolve.
     */
    public void resolve(List<Stmt> statements) {
        for (var s : statements) {
            resolve(s);
        }
    }

    /**
     * Opens a new lexical scope.
     */
    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    /**
     * Closes the innermost lexical scope.
     */
    private void endScope() {
        scopes.pop();
    }

    /**
     * Declares a name in the current scope without defining it yet.
     *
     * @param name     Declared variable name.
     * @param position Source position for error reporting.
     */
    private void declare(String name, Token.Position position) {
        if (scopes.isEmpty()) return;

        var scope = scopes.peek();

        if (scope.containsKey(name)) {
            throw new RuntimeError("%s is already defined on this scope".formatted(name), position);
        }

        scope.put(name, false);
    }

    /**
     * Marks a previously declared name as fully defined.
     *
     * @param name Variable name to define.
     */
    private void define(String name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name, true);
    }

    /**
     * Resolves a variable access relative to the current scope stack.
     *
     * @param expr Expression to associate with the resolved depth.
     * @param name Referenced variable name.
     */
    private void resolveLocal(Expr expr, String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                evaluator.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    /**
     * Resolves a function body in its own scope and context.
     *
     * @param function Function declaration to resolve.
     * @param type     Kind of function currently being resolved.
     */
    private void resolveFunction(FunctionDeclStmt function, FunctionType type) {
        var enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (var param : function.params) {
            declare(param, function.getPosition());
            define(param);
        }
        resolve(function.body);
        endScope();

        currentFunction = enclosingFunction;
    }

    /**
     * Resolves a block statement in a nested scope.
     *
     * @param stmt Block statement.
     */
    private void resolveBlockStmt(BlockStmt stmt) {
        beginScope();
        for (var s : stmt.statements) {
            resolve(s);
        }
        endScope();
    }

    /**
     * Resolves an expression statement.
     *
     * @param stmt Expression Statement.
     */
    private void resolveExpressionStmt(ExpressionStmt stmt) {
        resolve(stmt.expression);
    }

    /**
     * Resolves a function declaration.
     *
     * @param stmt Function declaration.
     */
    private void resolveFunctionDeclStmt(FunctionDeclStmt stmt) {
        declare(stmt.name, stmt.getPosition());
        define(stmt.name);

        resolveFunction(stmt, FunctionType.FUNCTION);
    }

    /**
     * Resolves a function declaration.
     *
     * @param stmt function declaration.
     */
    private void resolveIfStmt(IfStmt stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) {
            resolve(stmt.elseBranch);
        }
    }

    /**
     * Resolves a return statement and validates its context.
     *
     * @param stmt Return statement.
     */
    private void resolveReturnStmt(ReturnStmt stmt) {
        if (currentFunction == FunctionType.NONE) {
            throw new RuntimeError("Can't return from top-level code.", stmt.getPosition());
        }

        if (stmt.value != null) {
            resolve(stmt.value);
        }
    }

    /**
     * Resolves a return statement and validates its context.
     *
     * @param stmt Return statement.
     */
    private void resolveVarDeclStmt(VarDeclStmt stmt) {
        declare(stmt.name, stmt.getPosition());
        resolve(stmt.initializer);
        define(stmt.name);
    }

    /**
     * Resolves a return statement and validates its context.
     *
     * @param stmt Return statement.
     */
    private void resolveWhileStmt(WhileStmt stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
    }

    /**
     * Dispatches statement resolution based on concrete statement type.
     *
     * @param stmt Statement to resolve.
     */
    private void resolve(Stmt stmt) {
        switch (stmt) {
            case BlockStmt blockStmt -> resolveBlockStmt(blockStmt);
            case ExpressionStmt expressionStmt -> resolveExpressionStmt(expressionStmt);
            case FunctionDeclStmt functionDeclStmt -> resolveFunctionDeclStmt(functionDeclStmt);
            case IfStmt ifStmt -> resolveIfStmt(ifStmt);
            case ReturnStmt returnStmt -> resolveReturnStmt(returnStmt);
            case VarDeclStmt varDeclStmt -> resolveVarDeclStmt(varDeclStmt);
            case WhileStmt whileStmt -> resolveWhileStmt(whileStmt);
        }
    }

    /**
     * Resolves all elements of an array literal.
     *
     * @param expr Array literal expression.
     */
    private void resolveAssignExpr(AssignExpr expr) {
        resolve(expr.value);

        if (expr.target instanceof VariableExpr v) {
            resolveLocal(expr, v.name);
        } else {
            resolve(expr.target);
        }
    }

    /**
     * Resolves all elements of an array literal.
     *
     * @param expr Array literal expression.
     */
    private void resolveBinaryExpr(BinaryExpr expr) {
        resolve(expr.left);
        resolve(expr.right);
    }

    /**
     * Resolves a function or method call expression.
     *
     * @param expr Call expression.
     */
    private void resolveCallExpr(CallExpr expr) {
        resolve(expr.callee);

        for (var a : expr.arguments) {
            resolve(a);
        }
    }

    /**
     * Resolves all array literal elements.
     *
     * @param expr Array literal expression.
     */
    private void resolveArrayExpr(ArrayExpr expr) {
        for (var element : expr.elements) {
            resolve(element);
        }
    }

    /**
     * Resolves index expression target and index.
     *
     * @param expr Index expression.
     */
    private void resolveIndexExpr(IndexExpr expr) {
        resolve(expr.target);
        resolve(expr.index);
    }

    /**
     * Resolves a function or method call expression.
     *
     * @param expr Call expression.
     */
    private void resolveLogicalExpr(LogicalExpr expr) {
        resolve(expr.left);
        resolve(expr.right);
    }

    /**
     * Resolves the operand of a unary expression.
     *
     * @param expr Unary expression.
     */
    private void resolveUnaryExpr(UnaryExpr expr) {
        resolve(expr.right);
    }

    /**
     * Resolves the operand of a unary expression.
     *
     * @param expr Unary expression.
     */
    private void resolveVariableExpr(VariableExpr expr) {
        if (!scopes.isEmpty() && scopes.peek().get(expr.name) == Boolean.FALSE) {
            throw new RuntimeError("Can't read local variable in its own initializer.", expr.getPosition());
        }

        resolveLocal(expr, expr.name);
    }

    /**
     * Dispatches expression resolution based on concrete expression type.
     *
     * @param expr Expression to resolve.
     */
    private void resolve(Expr expr) {
        switch (expr) {
            case AssignExpr assignExpr -> resolveAssignExpr(assignExpr);
            case ArrayExpr arrayExpr -> resolveArrayExpr(arrayExpr);
            case BinaryExpr binaryExpr -> resolveBinaryExpr(binaryExpr);
            case CallExpr callExpr -> resolveCallExpr(callExpr);
            case IndexExpr indexExpr -> resolveIndexExpr(indexExpr);
            case LiteralExpr _ -> {
            }
            case LogicalExpr logicalExpr -> resolveLogicalExpr(logicalExpr);
            case UnaryExpr unaryExpr -> resolveUnaryExpr(unaryExpr);
            case VariableExpr variableExpr -> resolveVariableExpr(variableExpr);
        }
    }
}
