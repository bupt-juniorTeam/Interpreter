package lexer;

public class Num extends Token{
    public final int value;
    public Num(int v) {super(TokenType.NUM); value=v;}
}
