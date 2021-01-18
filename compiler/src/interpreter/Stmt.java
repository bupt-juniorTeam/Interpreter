package interpreter;

import java.util.List;

abstract class Stmt {
    abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        R visitExpressionStmt(Expression Stmt);
        R visitPrintStmt(Print Stmt);
    }

    static class Expression extends Stmt {
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        Expression(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;
    }

    static class Print extends Stmt {
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        Print(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;
    }

}
