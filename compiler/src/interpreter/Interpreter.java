package interpreter;

import java.util.List;

/**
 * Evaluate Expression Class
 * 将parser域中语法树的节点转换为运行中的值
 */
public class Interpreter implements Expr.Visitor<Object>,
                                    Stmt.Visitor<Void> {
    private Environment environment = new Environment();

    /**
     * 对外接口
     * 接收一个expression 输出 expression的值
     * 如果发生runtime error 向用户汇报
     * @param statements
     */
    public void interprete(List<Stmt> statements){
        try{
            for (Stmt statement:statements) {
                execute(statement);
            }
        }catch (RuntimeError error){
            Main.runtimeError(error);
        }
    }

    // statement部分

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    // expression部分

    /**
     * 根据expr调用4个visit函数
     * 主要目的是可以通过此函数进行递归分析value
     * @param expr
     * @return
     */
    private Object evaluate(Expr expr){
        return expr.accept(this);
    }

    /**
     * visit函数
     * 评估二元运算的值
     * @param expr
     * @return
     */
    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        // 先分析左操作数再分析右操作数
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        switch (expr.operator.type){
            case GREATER:
                checkNumberOperands(expr.operator, left ,right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left ,right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left ,right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left ,right);
                return (double)left <= (double)right;
            case MINUS:
                checkNumberOperands(expr.operator, left ,right);
                return (double)left - (double)right;
            case PLUS:
                if(left instanceof Double && right instanceof Double){
                    return (double)left + (double)right;
                }
                if(left instanceof String && right instanceof String){
                    return (String)left + (String)right;
                }

                throw new RuntimeError(expr.operator,
                        "Operands must be two numbers or two strings.");
            case DIVIDE:
                checkNumberOperands(expr.operator, left ,right);
                return (double)left / (double)right;
            case MULTIPLY:
                checkNumberOperands(expr.operator, left ,right);
                return (double)left * (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            default:
                break;
        }
        return null;
    }

    /**
     * 评估括号内的表达式的值
     * @param expr
     * @return
     */
    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    /**
     * Literal 语法树的值就是 literal的value
     * literal几乎和运行时值一样
     * 只不过literal来自parser的域
     * value来自runtime domain
     * @param expr
     * @return
     */
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    /**
     * visit函数
     * 评估一元运算的值
     * @param expr
     * @return
     */
    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch(expr.operator.type){
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
            case PLUS:
                checkNumberOperand(expr.operator, right);
                return +(double)right;
            case BANG:
                return !isTruthy(right);
            default:
                break;
        }
        // Unreachable
        return null;
    }

    /**
     * 判断两个类型值是否相等
     * 通过java equal method
     * @param a
     * @param b
     * @return
     */
    private boolean isEqual(Object a,Object b){
        // 对null调用equal会抛出异常,所以特殊处理
        if(a == null && b == null){
            return true;
        }
        if(a == null){
            return false;
        }
        return a.equals(b);
    }
    /**
     * 判断一个类型是否为真
     * @param object
     * @return
     */
    private boolean isTruthy(Object object){
        if(object == null) {
            return false;
        }
        if(object instanceof Boolean) {
            return (boolean)object;
        }
        return true;
    }

    /**
     * 验证一元运算符的操作数是不是数字
     * 不是数字抛出runtime error
     * @param operator
     * @param operand
     */
    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    /**
     * 验证二元运算符的操作数是不是数字
     * 不是抛出runtime error
     * @param operator
     * @param left
     * @param right
     */
    private void checkNumberOperands(Token operator, Object left, Object right){
        if(left instanceof Double && right instanceof Double){
            return;
        }
        throw new RuntimeError(operator,"Operands must be numbers.");
    }

    /**
     * 将得到的值(object)转换为string便于输出
     * @param object
     * @return
     */
    private String stringify(Object object){
        if(object == null) {
            return "NULL";
        }
        if(object instanceof Double){
            String text = object.toString();
            if(text.endsWith(".0")){
                text = text.substring(0,text.length() -2);
            }
            return text;
        }
        return object.toString();
    }
}
