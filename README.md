<!-- TOC -->

- [相关教程](#相关教程)
- [写在前面](#写在前面)
- [搭建过程](#搭建过程)
  - [09/25/2020](#09252020)
  - [09/26/2020](#09262020)
  - [09/27/2020](#09272020)
  - [09/28/2020](#09282020)
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

### 09/25/2020
根据《编译原理》第二章的词法分析部分，复制了书上的源码

Lexer: 分析处理

Token, Word, Num: 词法单元

Tag: tokentype 词法单元的种类

### 09/26/2020
### 09/27/2020
书上源码放在lexer部分中
根据interpreter(详见相关教程)，编写interpreter部分 

### 09/28/2020
完成词法分析框架

***

## 笔记

### 基本概念

[参考链接](https://juejin.im/post/6844903853805027335)

![](./doc/resource/main.jpg)

### 总览
![](./doc/resource/overview.jpg)
***

### 词法单元种类
```
 数据类型 datatype
    布尔值，数字，字符串，空值

 表达式 expression
    算术运算符
       加减乘除
    比较运算符
       小于，小于等于，大于，大于等于，等于，不等于
    逻辑运算符
       非，或，且
    前缀、括号()
    赋值

 语句 statement
    闭包{}
    变量声明 var
    控制流 if/while/for
    函数(形参parameter/实参argument)
    类
```

***

## 计算机网络

未完成，欢迎补充

![](./doc/resource/network.jpg)