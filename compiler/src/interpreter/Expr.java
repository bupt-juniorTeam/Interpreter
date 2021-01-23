package interpreter;

import java.util.List;

abstract class Expr {
    abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        R visitUnaryExpr(Unary Expr);
        R visitLogicalExpr(Logical Expr);
        R visitBinaryExpr(Binary Expr);
        R visitGroupingExpr(Grouping Expr);
        R visitLiteralExpr(Literal Expr);
        R visitVariableExpr(Variable Expr);
        R visitAssignExpr(Assign Expr);
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

    static class Logical extends Expr {
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }

        Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        final Expr left;
        final Token operator;
        final Expr right;
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

    static class Variable extends Expr {
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        Variable(Token name) {
            this.name = name;
        }

        final Token name;
    }

    static class Assign extends Expr {
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

        Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        final Token name;
        final Expr value;
    }

}
