package interpreter;

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
    private static final String[] commands = {"run"};

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
                String filePath = args[1];
                runFile(filePath);
            } else {
                System.out.println("Usage: run <.c file path>"); // 参数错误
                // System.exit(64);
            }
        } else {
            run(line);
        }
    }

    private static void runFile(String path) throws IOException { // 读取源文件的代码
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
    }

    private static void run(String source) { // 运行
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
//        Parser parser = new Parser(tokens);
//        Expr expression = parser.parse();
        for(Token token : tokens) {
            System.out.println(token);
        }
        System.out.println("\n符号表");
        for(Token token : tokens) {
            if (token.type == TokenType.IDENTIFIER) {
                System.out.println(scanner.getId(token.lexeme));
            }
        }
        if (hadCompileError) { // 编译错误
            System.err.println("Compile Error");
        }
//        if(expression != null) {
//            interpreter.interpreter(expression);
//        }
        if(hadRuntimeError){ // 运行错误
            System.err.println("Runtime Error");
        }
    }

    // 错误函数:Token+错误信息
    public static void error(Token token, String message){
        if(token.type == TokenType.EOF){
            report(token.line,"at end", message);
        }else{
            report(token.line, "at '"+token.lexeme + "'", message);
        }
    }
    // 报告编译时的语法错误
    public static void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error " + where + ": " + message);
        hadCompileError = true;
    }
    // 报告运行时的语义错误
    public static void runtimeError(RuntimeError error){
        System.err.println(error.getMessage() +
                "\n[line: " + error.token.line + "]");
        hadRuntimeError = true;
    }

    // 错误函数:行数+错误信息
//    static void error(int line, String message) { // 错误处理
//        report(line, "", message);
//    }
}