package interpreter;

import java.util.List;

abstract class Expr {
    abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        R visitBinaryExpr(Binary Expr);
        R visitGroupingExpr(Grouping Expr);
        R visitLiteralExpr(Literal Expr);
        R visitUnaryExpr(Unary Expr);
    }

    static class Binary extends Expr {
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    static class Grouping extends Expr {
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        Grouping(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;
    }

    static class Literal extends Expr {
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        Literal(Object value) {
            this.value = value;
        }

        final Object value;
    }

    static class Unary extends Expr {
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        final Token operator;
        final Expr right;
    }

}
