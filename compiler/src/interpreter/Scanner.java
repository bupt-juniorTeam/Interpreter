package interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private String source;
    private List<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> keywords; // 关键字
    private static Map<String, List<Token>> id = new HashMap<>(); // 符号表
    // 双指针
    private int start = 0;
    private int current = 0;
    private int line = 1;
    // 字符统计
    private int num_identifier = 0;
    private int num_number = 0;
    private int num_string = 0;
    private int num_keyword = 0;

    static { // 为了区分标识符和关键字，先存储关键字
        keywords = new HashMap<>();
        keywords.put("return", TokenType.RETURN);
        keywords.put("void", TokenType.VOID);
        keywords.put("extern", TokenType.EXTERN);
        keywords.put("sizeof", TokenType.SIZEOF);
        keywords.put("typedef", TokenType.TYPEDEF);
        keywords.put("register", TokenType.REGISTER);
        keywords.put("volatile", TokenType.VOLATILE);
        keywords.put("include", TokenType.INCLUDE);

        keywords.put("float", TokenType.FLOAT);
        keywords.put("int", TokenType.INT);
        keywords.put("char", TokenType.CHAR);
        keywords.put("double", TokenType.DOUBLE);
        keywords.put("auto", TokenType.AUTO);
        keywords.put("long", TokenType.LONG);
        keywords.put("short", TokenType.SHORT);
        keywords.put("const", TokenType.CONST);
        keywords.put("signed", TokenType.SIGNED);
        keywords.put("unsigned", TokenType.UNSIGNED);
        keywords.put("static", TokenType.STATIC);
        keywords.put("enum", TokenType.ENUM);
        keywords.put("struct", TokenType.STRUCT);
        keywords.put("union", TokenType.UNION);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("NULL", TokenType.NULL);

        keywords.put("for", TokenType.FOR);
        keywords.put("if", TokenType.IF);
        keywords.put("while", TokenType.WHILE);
        keywords.put("do", TokenType.DO);
        keywords.put("else", TokenType.ELSE);
        keywords.put("switch", TokenType.SWITCH);
        keywords.put("case", TokenType.CASE);
        keywords.put("break", TokenType.BREAK);
        keywords.put("continue", TokenType.CONTINUE);
        keywords.put("default", TokenType.DEFAULT);
        keywords.put("goto", TokenType.GOTO);
    }

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        System.out.println("LEXICAL ANALYSIS RESULT");
        System.out.println("line: "+line + ", token: "+ tokens.size());
        System.out.println("identifier: "+num_identifier+", number: "+num_number
                +", string: "+num_string + ", keyword: "+num_keyword);

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LEFT_PAREN);    break;
            case ')': addToken(TokenType.RIGHT_PAREN);   break;
            case '{': addToken(TokenType.LEFT_BRACE);    break;
            case '}': addToken(TokenType.RIGHT_BRACE);   break;
            case '[': addToken(TokenType.LEFT_BRACKET);  break;
            case ']': addToken(TokenType.RIGHT_BRACKET); break;
            case ',': addToken(TokenType.COMMA);         break;
            case '.': addToken(TokenType.DOT);           break;
            case ';': addToken(TokenType.SEMICOLON);     break;
            case '?': addToken(TokenType.QUESTION);      break;
            case ':': addToken(TokenType.COLON);         break;
            case '#':
                while (peek() != '\n')
                    current++;
                break;
                // addToken(TokenType.HASH);          break;
            case '*': addToken(match('=') ? TokenType.MULTIPLY_EQUAL : TokenType.MULTIPLY); break;
            case '%': addToken(match('=') ? TokenType.MOD_EQUAL : TokenType.MOD);           break;
            case '!': addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);         break;
            case '=': addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);       break;
            case '^': addToken(match('=') ? TokenType.XOR_EQUAL : TokenType.XOR);           break;
            case '~': addToken(match('=') ? TokenType.NOT_EQUAL : TokenType.NOT);           break;

            // - -= -- ->
            case '-':
                if (match('=')) {
                    addToken(TokenType.MINUS_EQUAL);
                } else if (match('-')) {
                    addToken(TokenType.MINUS_MINUS);
                } else if (match('>')) {
                    addToken(TokenType.POINT);
                } else {
                    addToken(TokenType.MINUS);
                }
                break;
            // + += ++
            case '+':
                if (match('=')) {
                    addToken(TokenType.PLUS_EQUAL);
                } else if (match('+')) {
                    addToken(TokenType.PLUS_PLUS);
                } else {
                    addToken(TokenType.PLUS);
                }
                break;
            // < <= << <<=
            case '<':
                if (match('=')) {
                    addToken(TokenType.LESS_EQUAL);
                } else if (match('<')) {
                    if(match('=')){
                        addToken(TokenType.SHIFT_LEFT_EQUAL);
                    }else {
                        addToken(TokenType.SHIFT_LEFT);
                    }
                } else {
                    addToken(TokenType.LESS);
                }
                break;
            // > >> >= >>=
            case '>':
                if (match('-')) {
                    addToken(TokenType.GREATER_EQUAL);
                } else if (match('>')) {
                    if(match('=')){
                        addToken(TokenType.SHIFT_RIGHT_EQUAL);
                    }else {
                        addToken(TokenType.SHIFT_RIGHT);
                    }
                } else {
                    addToken(TokenType.GREATER);
                }
                break;
            // | || |=
            case '|':
                if (match('|')) { // 将或和与判断转为上面三元运算符形式
                    addToken(TokenType.OR_OR);
                } else if (match('=')) {
                    addToken(TokenType.OR_EQUAL);
                } else {
                    addToken(TokenType.OR);
                }
                break;
            // & && &=
            case '&':
                if (match('&')) {
                    addToken(TokenType.AND_AND);
                } else if (match('=')) {
                    addToken(TokenType.AND_EQUAL);
                } else {
                    addToken(TokenType.AND);
                }
                break;
            // / /= // /*
            case '/':
                if (match('/')) { // 注释 //
                    while (peek() != '\n' && !isAtEnd()) {
                        current++;
                    }
                } else if (match('*')) { // 注释 /*
                    while (!isAtEnd()) {
                        if (match('\n')) {
                            line++;
                        }
                        if (match('*') && match('/')) {
                            break;
                        }
                        current++;
                    }
                    Main.report(line,"at end","missing */ to close /*");
                }
                else if (match('=')){
                    addToken(TokenType.DIVIDE_EQUAL);
                } else {
                    addToken(TokenType.DIVIDE);
                }
                break;
            // 占位符
            case ' ':
            case '\0':
            case '\r':
            case '\f':
            case '\t': break;
            case '\n':
                line++;
                break;
            case '"':
                strings();
                break;
            case '\'' :
                character();
                break;
            default:
                if(isDigit(c)) {
                    number();
                }else if (isAlpha(c)) {
                    identifier();
                }
                else {
                    Main.report(line,"at "+ current,"Unexpected Character(ASCII value) " + (int)c + ".");
                }
                break;
        }
    }

    private char advance() { // 返回下一个字符(current), 并增加current
        current++;
        return source.charAt(current-1);
    }

    private Boolean match(char expected) { // 匹配
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }
        current++;
        return true;
    }

    private char peek() { // 返回下一个字符(current)
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
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
    // 遇到number 执行此函数
    private void number() {
        while (isDigit(peek())) {
            current++;
        }

        if (peek() == '.' && isDigit(peekNext())) {
            current++;
            while (isDigit(peek())) {
                current++;
            }
        }

        if (peek() == 'E' || peek() == 'e') {
            current++;
            if (peek() == '+' || peek() == '-') {
                current++;
            }
            while (isDigit(peek())) {
                current++;
            }
        }

        addToken(TokenType.NUMBER,
                Double.parseDouble(source.substring(start, current)));

        num_number++;
    }
    // 保留字或参数名等
    private void identifier() {
        while (isAlphaNumeric(peek())) {
            current++;
        }

        String text = source.substring(start, current);

        TokenType type = keywords.get(text);
        if (type == null) {
            type = TokenType.IDENTIFIER;
            if(id.get(text)!=null) {
                id.get(text).add(new Token(type, text, "", line));
            }
            else{
                List<Token> newIdentifier = new ArrayList<>();
                newIdentifier.add(new Token(type, text, "", line));
                id.put(text,newIdentifier);
            }

            num_identifier++;
        } else {
            num_keyword++;
        }
//        addToken(type);
        tokens.add(new Token(type, text, "", line));
    }
    // 遇到字符串 执行此函数
    private void strings() {
        while (peek()!='"') {
            if(peek()=='\0'){
                Main.report(line,"at end","missing \" character");
                break;
            }
            current++;
        }
        start+=1;
        String text = source.substring(start, current);
        addToken(TokenType.STRING, text);

        current++;
        num_string++;
    }
    // 遇到字符 执行此函数
    private void character() {
        int length = 0;
        while (peek()!='\'') {
            if(peek()=='\0'){

                Main.report(line,"at end","missing ' character");
                break;
            }
            length++;
            current++;
        }
        System.out.println("1");
        if(length > 1){
            Main.report(line,"at"+source.substring(start+1, current),"illegal chracter");
            current++;
            num_string++;
            return;
        }
        addToken(TokenType.CHAR, (int) source.charAt(start + 1));
        current++;
        num_string++;
    }
}
