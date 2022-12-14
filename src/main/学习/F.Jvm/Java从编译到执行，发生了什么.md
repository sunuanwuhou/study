# Table of Contents

* [java从编译到执行的过程，发生了什么？](#java从编译到执行的过程发生了什么)
* [编译](#编译)
* [加载](#加载)
* [解释](#解释)
* [执行](#执行)








# java从编译到执行的过程，发生了什么？

+ 编译
+ 加载
+ 解释
+ 运行



# 编译

将源码文件编译成JVM可以解释的class文件

编译过程会对源代码程序做【语法分析】【语义分析】【注解处理】最后生成字节码文件。



> lombok



# 加载



+ [类加载机制](类加载机制.md)



# 解释

> 把字节码转换为操作系统识别的指令



java文件通过javac编译成class文件，这种中间码被称为字节码。然后由JVM加载字节码，

+ 运行时解释器将字节码解释为一行行机器码来执行。

+ 在程序运行期间，即时编译器会争对**热点代码**(当虚拟机发现某个方法或代码的运行特别频繁时，就会把它们认定为“热点代码”)，将该部分字节码编译成机器码，获得更高的运行效率。

在这个运行时，解释器和即时编译器(**JIT编译**)的相互配合，使java程序几乎能达到和编译型语言一样的执行效率。

- 热点代码探测

  - 基于采样的热点探测

    采用这种方法的虚拟机会周期性地检查各个线程的栈顶，如果发现某些方法经常出现在栈顶，那这个方法就是“热点方法”。这种探测方法的好处是实现**简单高效**，还可以很容易地获取方法调用关系（将调用堆栈展开即可），缺点是很难精确地确认一个方法的热度，容易因为受到线程阻塞或别的外界因素的影响而扰乱热点探测。

- 基于计数器的热点探测

  采用这种方法的虚拟机会为每个方法（甚至是代码块）建立计数器，统计方法的执行次数，如果执行次数超过一定的阀值，就认为它是“热点方法”。这种统计方法**实现复杂**一些，需要为每个方法建立并维护计数器，而且不能直接获取到方法的调用关系，但是它的统计结果相对更加**精确严谨**。

- HotSpot虚拟机中使用的是哪种热点检测方式呢？
  基于计数器的热点探测方法，因此它为每个方法准备了两个计数器：***方法调用计数器***和***回边计数器***。在确定虚拟机运行参数的前提下，这两个计数器都有一个确定的阈值，当2个计数器超过设定阈值溢出了，就会触发JIT编译。

  - 方法调用计数器
  - 回边计数器
    统计一个方法中统计一个方法中**循环体**代码执行的次数，在字节码中遇到控制流向后跳转的指令称为“回边”**代码执行的次数，在字节码中遇到控制流向后跳转的指令称为“回边”

- server和Client对

  - Client Compiler(C1)来说，它是一个简单快速的编译器，主要关注点在于**局部优化**，而放弃许多耗时较长的全局优化手段。
  - 而Server Compiler(C2)则是专门面向服务器端的，并为服务端的性能配置特别调整过的编译器，是一个**充分优化**过的高级编译器



# 执行

操作系统把解释器解析出来的指令码，调用系统硬件执行最终的程序指令。
