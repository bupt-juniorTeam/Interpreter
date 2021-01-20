package interpreter;

public class VisitAst implements Expr.Visitor<String> {
    /**
     * 生成后缀表达式
     * 将一颗语法树输出为一串逆波兰string也是语法树的中序遍历结果
     * -123 * (45.67) => (* (- 123) (group 45.67))
     * @param name
     * @param exprs
     * @return
     */
    public String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(print(expr));
        }
        builder.append(")");

        return builder.toString();
    }

    /**
     * 此 visitor 的递归接口
     * @param expr
     * @return
     */
    private String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) {
            return "NULL";
        }
        return expr.value.toString();
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.lexeme;
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }
}
