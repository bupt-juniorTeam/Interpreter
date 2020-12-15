package interpreter.LRParse;

import interpreter.TokenType;

import java.util.List;
import java.util.Vector;

public class LRExpression {
    public String content;
    List<TokenType> elements;

    //获取产生式长度
    public int getBodyLength(){
        return this.elements.size()-1;
    }
    //获取产生式头
    public TokenType getHead(){
        return this.elements.get(0);
    }
    public LRExpression(String content, List<TokenType> elements){
        this.elements=elements;
        this.content=content;
    }

}
