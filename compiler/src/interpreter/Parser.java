package interpreter;

import java.util.List;

public class Parser {
    /**
     * 语法错误类
     * 语法分析采用递归调用，在分析过程中，每一次调用函数都会在调用栈上保存一层栈帧(call frame)
     * 异常可以清空调用栈。因此通过抛出异常，我们可以清空调用栈，重置分析状态
     */
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * 对外接口
     */
    public Expr parse() {
        try {
            return expression();
        } catch (ParseError error) { // 抓住语法递归分析中的异常，此时调用栈清空，可进行同步(synchronized)继续语法分析
            return null;
        }
    }

    /**
     * expression     → equality ;
     * @return
     */
    private Expr expression(){
        return equality();
    }
    /**
     * equality       → comparison ( ( "!=" | "==" ) comparison )* ;
     * @return
     */
    private Expr equality() {
        Expr expr = comparison();
        while(match(TokenType.BANG_EQUAL,TokenType.EQUAL_EQUAL)){
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr,operator,right);
        }
        return expr;
    }//最先进行优先级最高的等号的判定
    /**
     * comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
     * @return
     */
    private Expr comparison(){
        Expr expr = term();
        while(match(TokenType.GREATER,TokenType.GREATER_EQUAL,TokenType.LESS,TokenType.LESS_EQUAL)){
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr,operator,right);
        }
        return expr;
    }
    /**
     * term           → factor ( ( "-" | "+" ) factor )* ;
     * @return
     */
    private Expr term() {
        Expr expr = factor();
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    /**
     * factor         → unary ( ( "/" | "*" ) unary )* ;
     * @return
     */
    private Expr factor() {
        Expr expr = unary();
        while (match(TokenType.DIVIDE, TokenType.MULTIPLY)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }
    /**
     * unary          → ( "!" | "-" ) unary
     *                | primary ;
     * @return
     */
    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }
    /**
     * primary        → NUMBER | STRING | "true" | "false" | "nil"
     *                | "(" expression ")" ;
     * @return
     */
    private Expr primary() {
        if (match(TokenType.FALSE)) {
            return new Expr.Literal(false);
        }
        if (match(TokenType.TRUE)) {
            return new Expr.Literal(true);
        }
        if (match(TokenType.NULL)) {
            return new Expr.Literal(null);
        }

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(),"Expect expression.");
    }

    /**
     * 返回当前token的type是否匹配types中的某一个,若匹配,隐式 advance()
     * @param types
     * @return
     */
    private boolean match(TokenType... types){
        for (TokenType type:types){
            if(check(type)){
                advance();
                return true;
            }
        }
        return false;
    }
    /**
     * 检查下一个令牌是否符合预期,符合就消耗,不符合就报告错误
     * 与match唯一不同是报告错误
     * @param type
     * @param message
     * @return
     */
    private Token consume(TokenType type, String message){
        if(check(type)){
            return advance();
        }
        throw error(peek(),message);
    }
    /**
     * match的辅助函数,判断当前token的type是否和某一type相同
     * @param type
     * @return
     */
    private boolean check(TokenType type){
        if(isAtEnd()) {
            return false;
        }
        return peek().type == type;
    }

    /**
     * 将检测令牌下标向前推进一个,返回上一个令牌
     * @return
     */
    private Token advance(){
        if(!isAtEnd()){
            ++current;
        }
        return previous();
    }
    /**
     * 是否用完所有令牌
     * @return
     */
    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }
    /**
     * 返回当下还未检测的令牌
     * @return
     */
    private Token peek() {
        return tokens.get(current);
    }
    /**
     * 返回上一个刚检测过的令牌
     * @return
     */
    private Token previous() {
        return tokens.get(current - 1);
    }

    /**
     * 返回语法错误 而不是抛出,
     * 这样可以让程序决定是否进入panic mode(进行同步)
     * 或直接报告错误之后继续parser(不进行同步)
     * 比如:
     * 如果函数参数过多,应该不进入紧急模式,
     * 而继续分析之后的参数
     * @param token
     * @param message
     * @return
     */
    private ParseError error(Token token, String message){
        Main.error(token, message);
        return new ParseError();
    }
    /**
     * 忽略词法单元，减少之前错误的副作用
     * 直到遇到错误语句(statement)的结尾或下一个语句的开头，即同步词法单元。然后继续语法分析
     * 例如 ; } { while if for do
     */
    private void synchronize(){
        advance();
        while(!isAtEnd()){
            // 如果上一个是分号,同步完成,开始分析
            if (previous().type == TokenType.SEMICOLON){
                return;
            }
            // 如果当下令牌type是一般而言语句开始的types,同步完成,直接return
//            switch (peek().type){
//                case TokenType.STRUCT:
//                case TokenType.FOR:
//                case TokenType.IF:
//                case TokenType.RETURN:
//                    return;
//            }
            advance();
        }
    }
}
