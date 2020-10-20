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
    static boolean hadError = false;
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
            hadError = false;
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
        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();
        // for now, just print the tokens.
//        for(Token token : tokens) {
//            System.out.println(token);
//        }
        //System.exit(65);
        if (hadError) {
            System.err.println("Compile Error");
        }
        if(expression != null) {
            interpreter.interpreter(expression);
        }
        //System.exit(70);
        if(hadRuntimeError){
            System.err.println("Runtime Error");
        }
    }

    // 报告函数待改，不同类型错误使用error[错误类型]函数，error内部调用report
    static void error(int line, String message) { // 错误处理
        report(line, "", message);
    }
    // 报告运行时的语义错误
    static void runtimeError(RuntimeError error){
        System.err.println(error.getMessage() +
            "\n[line: " + error.token.line + "]");
        hadRuntimeError = true;
    }
    public static void report(int line, String lexeme, String message) { // 错误处理
        System.err.println(
                "[line " + line + "] Error" + ": " + message + lexeme);
        hadError = true;
    }
    public static void error(Token token,String message){
        if(token.type == TokenType.EOF){
            report(token.line," at end", message);
        }else{
            report(token.line, "at '"+token.lexeme + "'",message);
        }
    }
}