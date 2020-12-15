package interpreter;

public class Token {
    public TokenType type; // 种类
    public String lexeme; // 标识符
    public Object literal; // 字面量(值)
    public final int line; // 行数

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexeme='" + lexeme + '\'' +
                ", literal=" + literal +
                ", line=" + line +
                '}';
    }
}
