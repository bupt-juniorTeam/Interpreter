package interpreter;

import java.util.List;

public class Parser {
    List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    void parse() {}
}
