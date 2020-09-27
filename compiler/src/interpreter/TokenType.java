package interpreter;

enum TokenType {
    // 一元运算符
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, // () {}
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR, // , . - + ; / *

    // 一元和二元运算符
    BANG, BANG_EQUAL, // ! !=
    EQUAL, EQUAL_EQUAL, // = ==
    GREATER, GREATER_EQUAL, // > >=
    LESS, LESS_EQUAL, // < <=

    // 字面量
    IDENTIFIER, STRING, NUMBER, // 标识符、字符串、数字(整数/浮点数)

    // 关键字
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR, // & class else false true fun for if nil or
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE, // print return super this true var while

    EOF // 文件末尾标识符
}

