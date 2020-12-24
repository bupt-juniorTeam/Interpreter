package interpreter.LRParse;

import interpreter.Token;
import interpreter.TokenType;

public class LRState {
    public int index;
    public TokenType token;

    public LRState(int index, TokenType token){
        this.index=index;
        this.token=token;
    }

    @Override
    public int hashCode() {
        return (index+token.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode()==obj.hashCode();
    }
}
