# Table of Contents

* [什么是Socket](#什么是socket)



# 什么是Socket



> 一句话就是，TCP的具体实现是Socket

socket则是对TCP/IP协议的封装和应用（程序员层面上）。

也可以说，**TPC/IP协议是传输层协议，主要解决数据如何在网络中传输，而HTTP是应用层协议，主要解决如何包装数据**。

关于TCP/IP和HTTP协议的关系，网络有一段比较容易理解的介绍：

“我们在传输数据时，可以只使用（传输层）TCP/IP协议，但是那样的话，如 果没有应用层，便无法识别数据内容，如果想要使传输的数据有意义，则必须使用到应用层协议，应用层协议有很多，比如HTTP、FTP、TELNET等，也
可以自己定义应用层协议。WEB使用HTTP协议作应用层协议，以封装HTTP文本信息，然后使用TCP/IP做传输层协议将它发到网络上。”

我们平时说的最多的socket是什么呢，实际上socket是对TCP/IP协议的封装，**Socket本身并不是协议，而是一个调用接口 （API），通过Socket，我们才能使用TCP/IP协议。**

**实际上，Socket跟TCP/IP协议没有必然的联系。Socket编程接 口在设计的时候，就希望也能适应其他的网络协议**

所以说，Socket的出现只是使得程序员更方便地使用TCP/IP协议栈而已，是对TCP/IP协议的抽象，从而形成了我们知道的一些最基本的函数接口，比如create、
listen、connect、accept、send、read和write等等。网络有一段关于socket和TCP/IP协议关系的说法比较容易理 解：

“TCP/IP只是一个协议栈，就像操作系统的运行机制一样，必须要具体实现，同时还要提供对外的操作接口。这个就像操作系统会提供标准的编程接口，比如win32编程接口一样，**TCP/IP也要提供可供程序员做网络开发所用的接口，这就是Socket编程接口**。”

实际上，传输层的TCP是基于网络层的IP协议的，而应用层的HTTP协议又是基于传输层的TCP协议的，而Socket本身不算是协议，就像上面所说，它只是提供了一个针对TCP或者UDP编程的接口。socket是对端口通信开发的工具,它要更底层一些.


# 参考链接 


https://mp.weixin.qq.com/s/JlAtbvNSRdXCRsYlkbTlUQ
