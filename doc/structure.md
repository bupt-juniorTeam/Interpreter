词法分析

```
interpreter
|--Main.java
|--Token.java
|--TokenType.java
|--Scanner.java
```

语法分析

```
interpreter
|--Expr.java
|--Stmt.java
|--Parser.java
```

解释器

```
interpreter
|--Environment.java
|--RuntimeError.java
|--Interpreter.java
```



其他部分

```
interpreter
|--tool
|   |--GenerateAst.java：生成抽象语法树结点
|--LRParse：LR语法分析
|	|--LRAction
|	|--LRExpression
|	|--LRMovement
|	|--LRParser
|	|--LRState
```







