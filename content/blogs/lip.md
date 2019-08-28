+++
title = "编程语言实现模式-树文法"
description = "ANTLR3中的树文法介绍，以及在ANTLR4中的尝试替代方案"
date = 2019-08-27
draft = false
in_search_index = true
[taxonomies]
categories = ["读书笔记", "编译"]
+++

["编程语言实现模式"](https://www.amazon.com/Language-Implementation-Patterns-Domain-Specific-Programming/dp/193435645X)讲解了构建语言应用所需的实用型编译技术， 是一本不可多得的好书。唯一遗憾的是书中使用ANLTR3作为教程，而ANTLR4与ANTLR3相比，有很大的变化，在降低文法书写难度等方面有很大改进，不过文法与之前版本的文法并不兼容"。书中从第6章起，内容都是基于ANTLR3的，所以这里尝试将书中使用ANTLR3实现的功能用ANTLR4来重构。书中的代码可以在[这里](https://pragprog.com/titles/tpdsl/source_code)下载。

本文中尝试重构的是"5.3 根据文法自动生成访问器"中的内容。

# 使用ANTLR文法构建AST
书中的4.4节介绍了“使用ANTLR文法构建AST”的方法。ANTLR3内置了一些辅助构建AST的功能, 在options中将output设置为AST后，ANTLR就会给每个规则方法增加返回值tree。启始规则会返回整个树的根节点。下面以5.3节中VecMath文法为例，解释说明这些文法。

```bash
// 文件名: VecMath.g
// START: header
grammar VecMath;
options {output=AST;} // we want to create ASTs
tokens { VEC; }       // define imaginary token for vector literal
// END: header

// START: stat
stat+ ;                         // build list of stat trees
ID '=' expr  -> ^('=' ID expr)  // '=' is operator subtree root
'print' expr -> ^('print' expr) // 'print' is subtree root
    ;
// END: stat
	
// START: expr
multExpr ('+'^ multExpr)* ;     // '+' is root node

multExpr
    :   primary (('*'^|'.'^) primary)*  // '*', '.' are roots
    ;
		    
primary
    :   INT
    |   ID
    |   '[' expr (',' expr)* ']' -> ^(VEC expr+)
    ;
// END: expr

ID  :   'a'..'z'+ ;
INT :   '0'..'9'+ ;
WS  :   (' '|'\r'|'\n')+ {skip();} ;
```

首先必须设置`optoins {output=AST;}`来让每个规则方法增加返回值tree, 

# ANTLR3中的树文法
## 二级目录
### 三级目录
#### 四级目录

# ANTLR4中树文法替代方案


