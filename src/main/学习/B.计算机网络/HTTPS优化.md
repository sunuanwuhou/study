# Table of Contents

* [前言](#前言)
* [总结](#总结)


# 前言

由裸数据传输的 HTTP 协议转成加密数据传输的 HTTPS 协议，给应用数据套了个「保护伞」，提高安全性的同时也带来了性能消耗。

因为 HTTPS 相比 HTTP 协议多一个 TLS 协议握手过程，**目的是为了通过非对称加密握手协商或者交换出对称加密密钥**，这个过程最长可以花费掉 **2 RTT**，接着后续传输的应用数据都得使用对称加密密钥来加密/解密。

为了数据的安全性，我们不得不使用 HTTPS 协议，至今大部分网址都已从 HTTP 迁移至 HTTPS 协议，因此针对 HTTPS 的优化是非常重要的。

这次，就从多个角度来优化 HTTPS。









# 总结

1. 对于硬件优化的方向，因为 HTTPS 是属于计算密集型，应该选择计算力更强的 CPU，而且最好选择**支持 AES-NI 特性的 CPU**，这个特性可以在硬件级别优化 AES 对称加密算法，加快应用数据的加解密。

2. 对于软件优化的方向，如果可以，把软件升级成较新的版本，比如将 Linux 内核 2.X 升级成 4.X，将 openssl 1.0.1 升级到 1.1.1，因为新版本的软件不仅会提供新的特性，而且还会修复老版本的问题。

3. 对于协议优化的方向：

- 密钥交换算法应该选择 **ECDHE 算法**，而不用 RSA 算法，因为 ECDHE 算法具备前向安全性，而且客户端可以在第三次握手之后，就发送加密应用数据，节省了 1 RTT。

- 将 TSL1.2 升级 **TSL1.3**，因为 TSL1.3 的握手过程只需要 1 RTT，而且安全性更强。

  > 怎么合并的呢？具体的做法是，客户端在 Client Hello 消息里带上了支持的椭圆曲线，以及这些椭圆曲线对应的公钥。
  >
  > 服务端收到后，选定一个椭圆曲线等参数，然后返回消息时，带上服务端这边的公钥。经过这 1 个 RTT，双方手上已经有生成会话密钥的材料了，于是客户端计算出会话密钥，就可以进行应用数据的加密传输了。

4. 对于证书优化的方向：

- 服务器应该选用 **ECDSA 证书**，而非 RSA 证书，因为在相同安全级别下，ECC 的密钥长度比 RSA 短很多，这样可以提高证书传输的效率；

- 服务器应该开启 **OCSP Stapling** 功能，由服务器预先获得 OCSP 的响应，并把响应结果缓存起来，这样 TLS 握手的时候就不用再访问 CA 服务器，减少了网络通信的开销，提高了证书验证的效率；

  >服务器向 CA 周期性地查询证书状态，获得一个带有时间戳和签名的响应结果并缓存它。
  >
  >当有客户端发起连接请求时，服务器会把这个「响应结果」在 TLS 握手过程中发给客户端。由于有签名的存在，服务器无法篡改，因此客户端就能得知证书是否已被吊销了，这样客户端就不需要再去查询。

5. 对于重连 HTTPS 时，我们可以使用一些技术让客户端和服务端使用上一次 HTTPS 连接使用的会话密钥，直接恢复会话，而不用再重新走完整的 TLS 握手过程。

    常见的**会话重用**技术有 Session ID 和 Session Ticket，

    > 类似服务器的session和token

    用了会话重用技术，当再次重连 HTTPS 时，只需要 1 RTT 就可以恢复会话。对于 TLS1.3 使用 Pre-shared Key 会话重用技术，只需要 0 RTT 就可以恢复会话。

    > 客户端会把 Ticket 和 HTTP 请求一同发送给服务端，这种方式叫 **Pre-shared Key**。

    这些会话重用技术虽然好用，但是存在一定的安全风险，它们不仅不具备前向安全，而且有**重放攻击**的风险，所以应当对会话密钥设定一个合理的过期时间。

    > 什么是重放重放攻击？
    >
    > 假设 Alice 想向 Bob 证明自己的身份。Bob 要求 Alice 的密码作为身份证明，爱丽丝应尽全力提供（可能是在经过如哈希函数的转换之后）。与此同时，Eve 窃听了对话并保留了密码（或哈希）。
    >
    > 交换结束后，Eve（冒充 Alice ）连接到 Bob。当被要求提供身份证明时，Eve 发送从 Bob 接受的最后一个会话中读取的 Alice 的密码（或哈希），从而授予 Eve 访问权限。
    >
    > 重放攻击的危险之处在于，如果中间人截获了某个客户端的 Session ID 或 Session Ticket 以及 POST 报文，而一般 POST 请求会改变数据库的数据，中间人就可以利用此截获的报文，不断向服务器发送该报文，这样就会导致数据库的数据被中间人改变了，而客户是不知情的。
    >
    > 避免重放攻击的方式就是需要**对会话密钥设定一个合理的过期时间**。
