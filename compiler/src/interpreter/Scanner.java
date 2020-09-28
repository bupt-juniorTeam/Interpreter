package interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private String source;
    private List<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> keywords;
    private int start = 0;
    private int current = 0;
    private int line = 1;

    static { // 为了区分标识符和关键字，先存储关键字
        keywords = new HashMap<>();
        //keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        //keywords.put("or", TokenType);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LEFT_PAREN);  break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE);  break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA);       break;
            case '.': addToken(TokenType.DOT);         break;
            case '-': addToken(TokenType.MINUS);       break;
            case '+': addToken(TokenType.PLUS);        break;
            case ';': addToken(TokenType.SEMICOLON);   break;
            case '*': addToken(TokenType.STAR);        break;
            case '!': addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;
            case '=': addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL); break;
            case '<': addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
            case '>': addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
            case '|':
                if (match('|')) { // 将或和与判断转为上面三元运算符形式
                    addToken(TokenType.OR);
                } else {
                    // 按位或
                }
                break;
            case '&':
                if (match('&')) {
                    addToken(TokenType.AND);
                } else {
                    // 按位与
                }
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd())
                        current++;
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            case ' ':
            case '\t': break;
            case '\n':
                line++;
                break;
            case '"':
                strings();
                break;
            default:
                if(isDigit(c)) {
                    number();
                }else if (isAlpha(c)) {
                    identifier();
                }
                else {
                    System.out.println(c);
                    Main.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private char advance() { // 返回下一个字符(current), 并增加current
        current++;
        return source.charAt(current-1);
    }

    private Boolean match(char expected) { // 匹配
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private char peek() { // 返回下一个字符(current)
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isDigit(char c) {
        return '0'<=c && c<='9';
    }

    private boolean isAlpha(char c) {
        return ('A'<=c && c<='Z') ||
                ('a'<=c && c<='z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void number() {
        while (isDigit(peek())) current++;

        if (peek() == '.' && isDigit(peekNext())) {
            current++;

            while (isDigit(peek())) current++;
        }

        addToken(TokenType.NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) current++;

        String text = source.substring(start, current);

        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }

    private void strings() {
        while (peek()!='"') current++;

        String text = source.substring(start+1, current);
        current++;

        addToken(TokenType.STRING, text);
    }
}
