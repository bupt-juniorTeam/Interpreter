package lexer;

public class Word extends Token{
    public final String lexme;
    public Word(int t, String s) {
        super(t); lexme = s;
    }
}
