package interpreter;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>(); // 符号表

    // 插入
    void define(String name, Object value) {
        if (!values.containsKey(name)) // 避免重定义
            values.put(name, value);

        throw new RuntimeError((Token) values.get(name), "redefined variable '" + name + "'.");
    }

    // 检索
    Object get(Token name) {
        if (values.containsKey(name.lexeme))
            return values.get(name.lexeme);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'");
    }

    // 定位
    // 重定位
}
