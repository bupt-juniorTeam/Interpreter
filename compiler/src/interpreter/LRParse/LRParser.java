package interpreter.LRParse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import interpreter.Token;
import interpreter.TokenType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class LRParser {
    private List<Token> tokens= Collections.emptyList();
    private Stack<Integer> stateStack;//状态栈
    private Stack<TokenType> symbolStack;//符号栈
    private HashMap<LRstate, LRMovement> actiontable;
    private HashMap<LRstate,LRMovement> gototable;
    private HashMap<Integer,TokenType> typetable;
    private HashMap<Integer,TokenType> tokentable;
    private List<LRExpression> expressions;
    private int current = 0;
    private String ActionTableFilePath="compiler\\src\\LRtable\\input_action.xls";
    private String GotoTableFilePath="compiler\\src\\LRtable\\input_goto.xls";


    public void load_table() throws IOException {
        FileInputStream in = new FileInputStream(ActionTableFilePath);
        Workbook wb=new HSSFWorkbook(in);
        Sheet sheet=wb.getSheetAt(0);
        //the rwo number is state order plus 1
        //state order start from 1
        for(int i=1;i<=sheet.getLastRowNum();i++) {
            Row row = sheet.getRow(i);
            String resultString="";
            for(int j=0;j<row.getLastCellNum();j++){
                resultString=row.getCell(j).getStringCellValue();
                if(resultString.equals("err")){
                    actiontable.put(
                            new LRstate(i-1,tokentable.get(j+1)),
                            new LRMovement(LRAction.Error,0)
                    );
                }
                else if(resultString.substring(0, 1).equals("s")){
                    int option=Integer.parseInt(resultString.substring(1,resultString.length()));
                    actiontable.put(
                            new LRstate(i-1,tokentable.get(j+1)),
                            new LRMovement(LRAction.Shift,option)
                    );
                }
                else if(resultString.substring(0, 1).equals("r")){
                    int option=Integer.parseInt(resultString.substring(1,resultString.length()));
                    actiontable.put(
                            new LRstate(i-1,tokentable.get(j+1)),
                            new LRMovement(LRAction.Reduce,option)
                    );
                }
                else if(resultString.equals("acc")){
                    actiontable.put(
                            new LRstate(i-1,tokentable.get(j+1)),
                            new LRMovement(LRAction.Accept,0)
                    );
                }
            }
        }

        in = new FileInputStream(GotoTableFilePath);
        wb=new HSSFWorkbook(in);
        sheet=wb.getSheetAt(0);
        for(int i=1;i<=sheet.getLastRowNum();i++) {
            Row row = sheet.getRow(i);
            String resultString = "";
            for (int j = 0; j < row.getLastCellNum(); j++) {
                resultString = row.getCell(j).getStringCellValue();
                if (resultString.equals("err")) {
                    gototable.put(
                            new LRstate(i - 1, typetable.get(j+1)),
                            new LRMovement(LRAction.Error, 0)
                    );
                }
                else if (resultString.substring(0, 1).equals("s")) {
                    int option = Integer.parseInt(resultString.substring(1, resultString.length()));
                    gototable.put(
                            new LRstate(i-1,typetable.get(j+1)),
                            new LRMovement(LRAction.Goto,option)
                    );
                }
            }
        }

    }

    public LRParser(List<Token> tokens) throws IOException {
        this.tokens = tokens;
        actiontable=new HashMap<>();
        gototable=new HashMap<>();
        typetable=new HashMap<>();
        tokentable=new HashMap<>();
        expressions=new ArrayList<>();
        stateStack=new Stack<>();
        symbolStack=new Stack<>();
        //为非终结符记录序号
        typetable.put(1,TokenType.S);
        typetable.put(2,TokenType.E);
        typetable.put(3,TokenType.T);
        typetable.put(4,TokenType.F);
        //为终结符填入序号
        tokentable.put(1,TokenType.EOF);
        tokentable.put(2,TokenType.PLUS);
        tokentable.put(3,TokenType.MINUS);
        tokentable.put(4,TokenType.PLUS);
        tokentable.put(5,TokenType.DIVIDE);
        tokentable.put(6,TokenType.LEFT_PAREN);
        tokentable.put(7,TokenType.RIGHT_PAREN);
        tokentable.put(8,TokenType.NUMBER);
        //填入产生式
        expressions.add(new LRExpression("S->E",List.of(TokenType.S,TokenType.E)));
        expressions.add(new LRExpression("E->E+T",List.of(TokenType.E,TokenType.E,TokenType.PLUS,TokenType.T)));
        expressions.add(new LRExpression("E->E-T",List.of(TokenType.E,TokenType.E,TokenType.MINUS,TokenType.T)));
        expressions.add(new LRExpression("E->T",List.of(TokenType.E,TokenType.T)));
        expressions.add(new LRExpression("T->T*F",List.of(TokenType.T,TokenType.T,TokenType.MULTIPLY,TokenType.F)));
        expressions.add(new LRExpression("T->T/F",List.of(TokenType.T,TokenType.T,TokenType.DIVIDE,TokenType.F)));
        expressions.add(new LRExpression("T->F",List.of(TokenType.T,TokenType.F)));
        expressions.add(new LRExpression("F->(E)",List.of(TokenType.F,TokenType.LEFT_PAREN,TokenType.E,TokenType.RIGHT_PAREN)));
        expressions.add(new LRExpression("F->num",List.of(TokenType.F,TokenType.NUMBER)));
        //填入分析表
        try {
                load_table();
        }catch (Exception ex){
            System.console().printf("can't open LRtbale file!");
        }


        //为分析栈填入栈底符号
        stateStack.push(-1);
    }
    private void advance(){
        current++;
    }
    private void advance(int i){
        current+=i;
    }
    public boolean end(){
        if(current==tokens.size())return true;
        else return false;
    }
    public TokenType now(){
        return this.tokens.get(current).type;
    }
    private void move(LRMovement movement){


    }
    public boolean parse() throws Exception {
        stateStack.push(0);
        symbolStack.push(TokenType.S);
        while (!stateStack.empty()&&!end()){
            LRstate newstate=new LRstate(stateStack.peek(),now());
            LRMovement nextMovement=actiontable.get(new LRstate(stateStack.peek(),now()));
            if(nextMovement.action==LRAction.Reduce){
                for(int i=0;i<this.expressions.get(nextMovement.parameter-1).getBodyLength();i++) {
                    symbolStack.pop();//弹出符号
                    stateStack.pop();//弹出状态
                }
                symbolStack.push(this.expressions.get(nextMovement.parameter-1).getHead());//压入符号
                TokenType expressionhead=this.expressions.get(nextMovement.parameter-1).getHead();
                int state_111=stateStack.peek();
                LRMovement nextGoto =gototable.get(new LRstate(stateStack.peek(),this.expressions.get(nextMovement.parameter-1).getHead()));
                if(nextGoto.action==LRAction.Error)return false;
                stateStack.push(nextGoto.parameter);//使用goto语句切换栈顶状态
            }
            else if(nextMovement.action==LRAction.Shift){
                symbolStack.push(now());
                stateStack.push(nextMovement.parameter);//将shift目标状态压入状态栈
                advance();
            }
            else if(nextMovement.action==LRAction.Accept) {
                return true;
            }
            else if(nextMovement.action==LRAction.Error){
                return false;
            }
            else{
                throw new Exception("the LR table has a fatal error!");
            }
        }
        return false;
    }

    public static void main(String[] args) {
        List<Token> tokens = Arrays.asList(
                new Token(TokenType.LEFT_PAREN, "", "", 0),
                new Token(TokenType.NUMBER, "", "", 0),
                new Token(TokenType.RIGHT_PAREN, "", "", 0),
                new Token(TokenType.EOF, "", "", 0)
        );
        try {
            LRParser lrParser = new LRParser(
                    tokens
            );
            boolean answer= lrParser.parse();
            System.out.println(answer);
        } catch (Exception ex) {
            System.console().printf(ex.toString());
        }

    }
}
