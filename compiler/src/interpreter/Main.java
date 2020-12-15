package interpreter;

import interpreter.LRParse.LRParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadCompileError = false;
    static boolean hadRuntimeError = false;
    // 命令字符串，用于识别命令
    // 后续扩展：可加一个C风格的函数指针数组？不太确定Java是否有类似的用法
    private static final String[] commands = {"run", "setPath", "lrParse"}; // 运行 设置路径 LR分析
    private static String defaultFilePath=System.getProperty("user.dir");

    public static void main(String[] args) throws IOException {
        loop();
    }

    private static void loop() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print(">> ");
            String line = reader.readLine();
            if(line == null) {
                continue;
            }
            String[] tokens = line.split(" ");
            runPrompt(tokens, line);
            hadCompileError = false;
            // 此处sleep是为了让 err 输出流输出完
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    private static void runPrompt(String[] args, String line) throws IOException{ // 实时运行命令
        if (args[0].equals(commands[0])) { // run path
            if(args.length == 2) {
                boolean absolute_route=false;
                if(args[1].length()>3){
                    if ((
                            'a'<args[1].charAt(0)&&args[1].charAt(0)<'z'||
                            'A'<args[1].charAt(0)&&args[1].charAt(0)<'Z')&&
                            args[1].charAt(1)==':'&&
                            args[1].charAt(2)=='\\'
                    ) absolute_route=true;
                }
                String filePath;
                if (absolute_route) {
                    filePath = args[1];
                }
                else{
                    filePath = defaultFilePath + "\\" + args[1];
                    System.out.println("file path is"+filePath);
                }
                runFile(filePath);
            } else {
                System.out.println("Usage: run <.c file path>"); // 参数错误
                // System.exit(64);
            }
        }
        else if (args[0].equals(commands[1])) {
            if(args.length == 2) {
                defaultFilePath = args[1];
            } else {
                System.out.println("Usage: run <.c file path>"); // 参数错误
                // System.exit(64);
            }
        }
        else if (args[0].equals(commands[2])) {
            LRparse(line);
        }
        else {
            run(line);
        }
    }

    private static void runFile(String path) throws IOException { // 读取源文件的代码
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
    }

    private static void run(String source) { // 运行
        // ************************************* 词法分析
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
//        打印记号流
//        for(Token token : tokens) {
//            System.out.println(token);
//        }
//        打印符号表
//        System.out.println("\n符号表");
//        for(Token token : tokens) {
//            if (token.type == TokenType.IDENTIFIER) {
//                System.out.println(token.lexeme);
//            }
//        }
        if (hadCompileError) { // 编译错误
            System.err.println("Compile Error");
        }

        // ************************************* 语法分析
        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        VisitAst visitAst = new VisitAst();
        System.out.println(visitAst.parenthesize("Expression: ", expression));

        // ************************************* 执行
        if(expression != null) {
            interpreter.interpreter(expression);
        }

        if(hadRuntimeError){ // 运行错误
            System.err.println("Runtime Error");
        }
    }

    private static void LRparse(String source) { // 运行
        // ************************************* 词法分析
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
//        打印记号流
//        for(Token token : tokens) {
//            System.out.println(token);
//        }
//        打印符号表
//        System.out.println("\n符号表");
//        for(Token token : tokens) {
//            if (token.type == TokenType.IDENTIFIER) {
//                System.out.println(token.lexeme);
//            }
//        }
        if (hadCompileError) { // 编译错误
            System.err.println("Compile Error");
        }

        // ************************************* 语法分析
        try {
            LRParser parser = new LRParser(tokens.subList(1,tokens.size()));
            boolean LR_result=parser.parse();
            if(LR_result){
                System.out.println("the sentence is correct!");
            }
            else {
                System.out.println("the sentence is wrong!");
            }
        }catch (Exception ex){
            System.out.println("can't find LR file");
        }

    }

    // 报告错误
    public static void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error " + where + ": " + message);
        hadCompileError = true;
    }
    // 错误函数:Token+错误信息
    public static void error(Token token, String message){
        if(token.type == TokenType.EOF){
            report(token.line,"at end", message);
        }else{
            report(token.line, "at '"+token.lexeme + "'", message);
        }
    }
    // 报告运行时的语义错误
    public static void runtimeError(RuntimeError error){
        report(error.token.line, "runTime\n", error.getMessage());
        hadRuntimeError = true;
    }
}