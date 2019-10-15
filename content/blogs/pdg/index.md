+++
title = "笔记: 编程语言实现模式-树文法"
description = "ANTLR3中的树文法介绍，以及在ANTLR4中的尝试替代方案"
date = 2019-09-10
draft = true
in_search_index = true
[taxonomies]
categories = ["编译"]
+++

The PDG makes explicit both the data and control dependences for each operation in a program.　

Data dependence graphs have provided some optimizing compilers with an explicit representation of the define-use relationships implicitly present in a source program. 

A control flow graph has been the usual representation for the control flow relationships of a program. the control conditions on which an operation depends can be derived from such a graph.

An undesiable property of a control flow graph, howerer, is a fixed sequencing of operations that need not hold.

Our motivation in developing the PDG has been to develop a program representation useful in an optimizing compiler for a vector or parallel machine. Such a compiler must perform both convential optimizations as well as new transformations for the detection of parallelism.
