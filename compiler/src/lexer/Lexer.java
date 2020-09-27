package lexer;

import java.io.IOException;
import java.util.Hashtable;

public class Lexer {
    public int line = 1;
    private char peek = ' ';
    private Hashtable words = new Hashtable();

    void reserve(Word t) { words.put(t.lexme, t); }

    public Lexer() {
        reserve( new Word(Tag.TRUE, "true") ); // 保留字
        reserve( new Word(Tag.FALSE, "false") );
    }

    public Token scan() throws IOException {
        for( ; ; peek = (char)System.in.read()) { // 处理空格、换行、制表符
            if (peek == ' ' || peek == '\t' ) ; //do nothing: continue;
            else if( peek == '\n' ) line = line + 1;
            else break;
        }
        if(Character.isDigit(peek)) { // 数字
            int v = 0;
            do {
                v = 10*v + Character.digit(peek, 10);
                peek = (char)System.in.read();
            } while (Character.isDigit(peek));
            return new Num(v);
        }
        if (Character.isLetter(peek)) { // 字符串
            StringBuffer b = new StringBuffer();
            do {
                b.append(peek);
                peek = (char)System.in.read();
            } while (Character.isLetter(peek));
            String s = b.toString();
            Word w = (Word)words.get(s);
            if( w != null) return w; // 保留字
            w = new Word(Tag.ID, s);
            words.put(s, w);
            return w;
        }
        Token t = new Token(peek); // 不需要预处理的字符
        peek = ' ';
        return t;
    }
}
