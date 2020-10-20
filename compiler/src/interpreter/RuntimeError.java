package interpreter;

/**
 * 表示在哪个token处发生runtime error
 */
public class RuntimeError extends RuntimeException {
    final Token token;
    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
