package interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    static boolean hadError = false;

    public static void main(String[] args) throws IOException {
//        if(args.length > 1) {
//            System.out.println("Usage: c [script]"); // 参数错误
//            System.exit(64);
//        } else if (args.length == 1) {
//            runFile(args[0]); // 读取源文件的代码
//        } else {
//            runPrompt(); // shell, 实时运行
//        }
        String filePath = "";
        runFile(filePath);
    }

    private static void runFile(String path) throws IOException { // 读取源文件的代码
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) System.exit(65);
    }

    private static void runPrompt() throws IOException { // 实时运行命令
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print(">> ");
            String line = reader.readLine();
            if(line == null) continue;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) { // 运行
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        // for now, just print the tokens.
        for(Token token : tokens) {
            System.out.println(token);
        }
    }

    static void error(int line, String message) { // 错误处理
        report(line, ' ', message);
    }

    public static void report(int line, char c, String message) { // 错误处理
        System.err.println(
                "[line " + line + "] Error " + "the ascii value is" + (int)c + ": " + message);
        hadError = true;
    }
}

