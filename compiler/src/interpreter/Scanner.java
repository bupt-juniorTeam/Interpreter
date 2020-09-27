package interpreter;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private String source;
    private List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

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
            case '/':
                if (match('/')) {
                    while (peek() != '\n')
                        advance();
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            default:
                Main.error(line, "Unexpected character.");
                break;
        }
    }

    private char advance() { //返回下一个字符
        current++;
        return source.charAt(current-1);
    }

    private Boolean match(char expected) { // 匹配
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private int peekNext() {
        return current + 1;
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
