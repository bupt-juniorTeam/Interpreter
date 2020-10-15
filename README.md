<!-- TOC -->

- [相关教程](#相关教程)
- [搭建过程](#搭建过程)
  - [词法分析器](#词法分析器)
    - [词法单元](#确定词法单元种类)
    - [推导](#确定推导规则)
    - [归约](#根据推导规则对输入进行规约)
    - [符号表](#插入符号表)
  - [语法分析器](#语法分析器)
    - [语法树](#语法树)
    - [语法分析](#语法分析)

<!-- /TOC -->

***
## 相关教程
[interpreter](http://www.craftinginterpreters.com/)

[shell](https://brennan.io/2015/01/16/write-a-shell-in-c/)

[c-compiler in java](https://github.com/yuanmie/KCC)

***

## 搭建过程

### 词法分析器
#### 确定词法单元种类
```
    // \t \v \ n \f 占位符 直接忽略
    // /**/ // 注释 直接忽略

    // 标点符号
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, // () {}
    LEFT_BRACKET, RIGHT_BRACKET, LEFT_ANGLE, RIGHT_ANGLE, // [] <>
    COMMA, DOT, SEMICOLON, QUESTION, COLON, // , . ; ? :

    // 一元运算符和二元运算符
    PLUS, PLUS_PLUS, PLUS_EQUAL, // + ++ +=
    MINUS, MINUS_MINUS, MINUS_EQUAL, POINT, // - -- -= ->
    DIVIDE, DIVIDE_EQUAL, // / /=
    MULTIPLY, MULTIPLY_EQUAL, // * *=
    MOD, MOD_EQUAL, // % %=
    BANG, BANG_EQUAL, // ! !=
    EQUAL, EQUAL_EQUAL, // = ==
    GREATER, SHIFT_RIGHT, GREATER_EQUAL, // > >> >=
    LESS, SHIFT_LEFT, LESS_EQUAL, // < << <=
    AND, AND_AND, AND_EQUAL, // & && &=
    OR, OR_OR, OR_EQUAL, // | || |=
    XOR, XOR_AND, // ^ ^=
    NOT, NOT_EQUAL, // ~ ~=

    // 关键字
    RETURN, VOID, EXTERN, SIZEOF, TYPEDEF, REGISTER, VOLATILE,// return void extern sizeof typedef register volatile

    FLOAT, INT, CHAR, DOUBLE, AUTO, // float int char double auto
    LONG, SHORT, CONST, SIGNED, UNSIGNED, STATIC, // long short const signed unsigned static
    ENUM, STRUCT, UNION, // enum struct union
    FALSE, TRUE, NULL, // false true

    FOR, IF, WHILE, DO, ELSE, SWITCH, CASE, // for if while do else switch case
    BREAK, CONTINUE, DEFAULT, GOTO, // break continue default goto

    IDENTIFIER, NUMBER, STRING, // 标识符 数字 字符串
    EOF // 文件末尾标识符
```

#### 确定推导规则
- 由一般到到个别(演绎)
- 使用正则文法规定格式(DFA与正则表达式等价)

以数字为例, number分三部分: 整数/浮点数/指数
```
digit -> [0-9]
digits -> digit+
number -> digits (. digits)? (E[+-]? digits)?
```
<!--
$$
digit \rightarrow [0-9]
\\
digits \rightarrow digit^+
\\
number \rightarrow digits (. digits)? (E[+-]? digits)?
$$
-->

#### 根据推导规则对输入进行规约
- 由个别到一般(归纳)
- 识别输入字符，如果词素符合某个推导模式，则保存为对应词法单元
```
switch (c) {
            // 标点符号: 包括左右括号 逗号 分号
            case '(': addToken(TokenType.LEFT_PAREN);  break;
            // 一元运算符
            case '+': addToken(TokenType.PLUS);        break;
            // 一元运算符或二元运算符
            case '!': addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;
            // 空白符 换行符 制表符
            case ' ':
            case '\t': break;
            case '\n':
                line++;
                break;
            // 字符串
            case '"':
                strings();
                break;
            default:
                if(isDigit(c)) { // 数字
                    number();
                }else if (isAlpha(c)) { // 标识符或关键字
                    identifier();
                }
                break;
        }
```

#### 插入符号表
- 符号类
```
public class Token {
    final TokenType type; // 种类
    final String lexeme; // 标识符
    final Object literal; // 字面量
    final int line; // 行数
}
```

- 运算符 标点符号 关键字
  
  不需要属性值
  
  `tokens.add(new Token(type, lexeme, line));`

- 标识符
  
  属性值: 指向符号表中该标识符对应条目的指针
  
  符号表条目: 词素 类型 出现位置

  `tokens.add(new Token(type, lexeme, literal, line));`

***

### 语法分析器

#### 语法树

##### 自动生成语法树

从根节点(非终结符)出发，自顶向下构建语法树。

每个语法(推导式)对应一个语法树(类)，每个语法树类包括成员变量、相应的操作函数

```
Binary -> left operator right
```

```java
abstract class Expr { 
  static class Binary extends Expr {
    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    final Expr left;
    final Token operator;
    final Expr right;
      
    String parse(){}
  }

  // Other expressions...
}
```

我们会设计很多语法推导式，然后编写很多语法树类，但这样很麻烦。我们希望定义语法推导式，然后自动生成语法树类。

于是，我们编写了一个工具类`tool/GenerateAst.java`，根据指定语法生成语法树类`Expr.java`



##### 访问者模式(*visitor pattern*)

假设我们已经生成了许多语法树类，考虑下面两个问题：

- 新增一个语法树类，需要再次实现我们规定的操作方法

![](./doc/resource/rows.png)

- 新增一个操作方法，需要在每一个类中提供相应的实现

![](./doc/resource/columns.png)



其实，这两个问题对应两种不同风格的语言：

- 对于面向对象式语言，我们定义一个抽象基类和相关的抽象方法，让其他子类继承，即可解决前者

- 对于函数式语言，我们将数据与操作分开，这让后者很容易解决

我们是否可以试着结合这两种模式，做一些妥协：

- 定义操作接口类，每个操作类都要实现并重写(override)该接口类

这让添加新操作变得容易

- 定义语法树类基类，每个语法树类都要继承基类，调用操作接口，通过重载(overload)操作函数访问

这让添加新语法树类变得容易



综合这两种思想，就是*visitor pattern*，具体见[访问者模式](https://www.jianshu.com/p/1f1049d0a0f4)

代码实现见`Expr.java`和`VisitAst.java`



#### 语法分析

