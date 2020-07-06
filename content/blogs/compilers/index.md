+++
title = "编译原理重修课程"
description = "编译原理相关技术综述"
date = 2020-06-18
draft = false
in_search_index = true
[taxonomies]
categories = ["笔记", "编译"]
+++

# 介绍
俗话说“出来混早晚是要还的”，作为一名学渣，当我在工作时要用到编译原理的时候，我只能哭着说一句: 俗话说的真对。当然作为一名ctrl+c ctrl+v搬砖小工，大部分情况下是用不到编译相关技术的，但当真正掌握一些编译知识的时候，你会发现编译还是挺有用的——最起码能吹牛不是。首先，先吐槽一下大学里这么课程给我留下的惨痛记忆, 过程比较简洁：在经过几节让昏昏欲睡的课程，以及一天一夜的“刻苦学习”之后，终于考试过了，然后就没有然后了。时间回到公元2018年，当我被告知需要写一个程序，把java代码里的安全漏洞检测的出来的时候，我思考了一下，再思考了一下，才意识到，我是不是学习过一门叫做“编译原理”的课程，无可奈何花落去，代码还得码出来啊。但在重新学习编译的之后，发现这东西其实和其他算法并无太大差异，之所以已前觉得难，是被学校里的教材和老师给弄晕了。

所以，在本文中，我会尽量简单的梳理出编译相关的算法与一些示范实例，不求甚解，但求能用。

# 编译技术的应用场景
编译呢，最常见的作用还是将计算机高级语言转换为可被cpu或操作系统直接执行的代码，以及各种个样的虚拟机等(如jvm)。但编译涉及的各个环节的技术其实都是可以单独或组合使用的，也不一定是用来写一门自己的编程语言。举例来说，intellj这种ide的代码提示、各种各样的模板语言(如jinja2)、代码规范检测、安全漏洞检测等。

# 编译原理技术目录
## 词法分析

## 语法分析
## 树型中间表示
## 符合识别与记录
## 语义分析
