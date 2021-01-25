package interpreter.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst { // 用于生成抽象语法树Expr.java
    public static void main(String[] args) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        String line = reader.readLine();
        String [] tokens = line.split(" ");

        if (tokens.length != 2) {
            System.out.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }

        String outputDir = tokens[1];
        // 语法产生式: ClassName : field[type, name]
        // expression
        defineAst(outputDir, "Expr", Arrays.asList(
                "Unary    : Token operator, Expr right",
                "Logical  : Expr left, Token operator, Expr right",
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Variable : Token name",
                "Assign   : Token name, Expr value",
                "Call     : Expr callee, Token paren, List<Expr> arguments"
        ));
        // statement
        defineAst(outputDir, "Stmt", Arrays.asList(
                "Expression : Expr expression",
                "Print      : Expr expression",
                "Var        : Token name, Expr initializer",
                "Block      : List<Stmt> statements",
                "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
                "While      : Expr condition, Stmt body",
                "Function   : Token name, List<Token> params, List<Stmt> body"
        ));
    }

    // 定义语法树
    private static void defineAst(String outputDir, String baseName,
                                  List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, String.valueOf(StandardCharsets.UTF_8));

        writer.println("package interpreter;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");
        writer.println();

        defineVisitor(writer, baseName, types);

        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName,
                                      List<String> types) {
        writer.println("    public interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(": ")[0].trim();
            writer.println("        R visit" + typeName + baseName + "(" +
                    typeName + " " + baseName + ");");
        }

        writer.println("    }");
        writer.println();
    }

    // 定义语法
    private static void defineType(PrintWriter writer, String baseName,
                                   String className, String fieldList) {
        writer.println("    static class " + className + " extends " + baseName + " {");

        writer.println("        @Override");
        writer.println("        <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" +
                className + baseName + "(this);");
        writer.println("        }");
        writer.println();

        // 构造函数
        writer.println("        " + className + "(" + fieldList + ") {");

        String[] fields = fieldList.split(", ");
        for (String filed : fields) {
            String name = filed.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }

        writer.println("        }");

        writer.println();
        for (String field : fields) {
            writer.println("        final " + field + ";");
        }

        writer.println("    }");
        writer.println();
    }
}
