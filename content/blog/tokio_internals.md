---
title: "Tokio内部机制: 从底层理解Rust的异步IO框架"
date: 2018-07-20T14:48:55+08:00
tags: [ "翻译", "Tokio", "Rust" ]
draft: true
---

本文译自: [Tokio internals: Understanding Rust's asynchronous I/O framework from the bottom up](https://cafbit.com/post/tokio_internals/), Thanks [David Simmons](https://cafbit.com) for this awesome article!

[Tokio](https://tokio.rs/)是Rust的异步IO框架。 与传统的同步I/O相比, Tokio的事件驱动的方式可以用更少的资源来获取更好的扩展性、性能。不幸的是，Tokio是众所周知的难学, 它太复杂，太抽象了。即使在阅读完官方教程, 我也没能深入了解这些抽象的内部机制, 不知道运行时到底发生了什么。

与从上往下的方式相反，我决定从下往上通过源码来弄懂驱动
