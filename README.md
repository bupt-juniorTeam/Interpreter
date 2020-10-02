<!-- TOC -->

- [相关教程](#相关教程)
- [写在前面](#写在前面)
- [搭建过程](#搭建过程)
  - [词法分析器](#词法分析器)
    - [词法单元](#确定词法单元种类)
    - [推导](#确定推导规则)
    - [归约](#根据推导规则对输入进行规约)
    - [符号表](#插入符号表)
- [笔记](#笔记)
  - [基本概念](#基本概念)
  - [总览](#总览)
  - [词法单元](#词法单元种类)
- [计算机网络](#计算机网络)

<!-- /TOC -->

***
## 相关教程
[interpreter](http://www.craftinginterpreters.com/)

[shell](https://brennan.io/2015/01/16/write-a-shell-in-c/)

Write a C compiler
  - [Part 1: Integers, Lexing and Code Generation](https://norasandler.com/2017/11/29/Write-a-Compiler.html)
  - [Part 2: Unary Operators](https://norasandler.com/2017/12/05/Write-a-Compiler-2.html)
  - [Part 3: Binary Operators](https://norasandler.com/2017/12/15/Write-a-Compiler-3.html)
  - [Part 4: Even More Binary Operators](https://norasandler.com/2017/12/28/Write-a-Compiler-4.html)
  - [Part 5: Local Variables](https://norasandler.com/2018/01/08/Write-a-Compiler-5.html)
  - [Part 6: Conditionals](https://norasandler.com/2018/02/25/Write-a-Compiler-6.html)
  - [Part 7: Compound Statements](https://norasandler.com/2018/03/14/Write-a-Compiler-7.html)
  - [Part 8: Loops](https://norasandler.com/2018/04/10/Write-a-Compiler-8.html)
  - [Part 9: Functions](https://norasandler.com/2018/06/27/Write-a-Compiler-9.html)
  - [Part 10: Global Variables](https://norasandler.com/2019/02/18/Write-a-Compiler-10.html)

## 写在前面

这学期我觉得很忙，有计算机网络和编译原理这两门计算机的大课，更不用说硬核的数值分析和其他专业选修。

所以，我创建这个项目的目的就是希望能集思广益，大家分别负责一些部分的学习，然后分享出来，促进各自的理解。

关于看书的方面，从我的个人角度来讲，绝不应该从头到尾一步一步看完；而是首先应该了解知识的框架，知道有哪些部分，每部分讲了什么。知道了框架，然后再确定自己要钻研具体的部分。

毕竟，我们是来解决问题的，不是当专家。我们是以一个程序员的视角：**A programmer's perspective**

具体参见：

[计算机学习的思考](https://www.zhihu.com/question/22608820/answer/21968467)

[编译原理学习经验](https://www.zhihu.com/question/27500017/answer/36958332)

> **L** short for Lexical analysis

> **P** short for Parsing

> **S** short for Semantic analysis

> **O** short for Optimization

> **CG** short for Code Generation

![](./doc/resource/focus.jpg)

***

## 搭建过程

### 词法分析器
##### 确定词法单元种类
```
 数据类型 datatype
    布尔值 数字(整数 浮点数 指数) 字符串 空值

 表达式 expression: 产生值(value)
    算术运算符
       + - * / = += -= *= /=
    比较运算符
       < <= > >= == !=
    逻辑运算符
       ! || &&
    位运算符
       | &
    括号
       ( )

 语句 statement: 产生效果(effect)
    print("Hello, world");

 闭包 
    { }

 变量
    var name = "Trump";

 控制流
    if() {}
    while() {}
    for() {}

 函数 
    type func(形参parameter) {}
    func(实参argument);

 类
    class name() {}

 注释
    // /**/
```

##### 确定推导规则
- 由一般到到个别(演绎)
- 使用正则文法规定格式(DFA与正则表达式等价)

以数字为例, number分三部分: 整数/浮点数/指数
$$
digit \rightarrow [0-9]
\\
digits \rightarrow digit^+
\\
number \rightarrow digits (. digits)? (E[+-]? digits)?
$$

##### 根据推导规则对输入进行规约
- 由个别到一般(归纳)
- 识别输入字符，如果词素符合某个推导模式，则保存为对应词法单元
```java
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

##### 插入符号表
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

## 笔记

### 基本概念

[参考链接](https://juejin.im/post/6844903853805027335)

![](./doc/resource/main.jpg)

### 总览
![](./doc/resource/overview.jpg)
***

***

## 计算机网络

未完成，欢迎补充

![](./doc/resource/network.jpg)