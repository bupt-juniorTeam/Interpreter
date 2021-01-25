package interpreter;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>(); // 符号表

    public Environment() {
        this.enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    // 插入
    void define(String name, Object value) {
        if (!values.containsKey(name))
            values.put(name, value);
    }

    void define(Token name, Object value) {
        if (!values.containsKey(name.lexeme)) // 避免重定义
            values.put(name.lexeme, value);
        else
            throw new RuntimeError(name, "redefined variable '" + name.lexeme + "'.");
    }

    // 检索
    Object get(Token name) {
        if (values.containsKey(name.lexeme))
            return values.get(name.lexeme);

        if (enclosing != null)
            return enclosing.get(name);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'");
    }

    // 赋值
    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'");
    }
}
