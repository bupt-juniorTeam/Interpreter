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
        }
    }

    private static void runPrompt(String[] args, String line) throws IOException{ // 实时运行命令
        if (args[0].equals(commands[0])) { // run path
            if(args.length == 2) {
                String filePath = args[1];
                runFile(filePath);
            } else {
                System.out.println("Usage: run [path]"); // 参数错误
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
        // for now, just print the tokens.
        for(Token token : tokens) {
            System.out.println(token);
        }

        if (hadError) //System.exit(65);
        {
            System.err.println("Compile Error");
        }
    }

    static void error(int line, String message) { // 错误处理
        report(line, ' ', message);
    }

    public static void report(int line, char c, String message) { // 错误处理
        System.err.println(
                "[line " + line + "] Error" + ": " + message + " The ascii value is " + (int)c);
        hadError = true;
    }
}