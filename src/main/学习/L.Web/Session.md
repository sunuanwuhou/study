# Table of Contents

* [Session 是什么](#session-是什么)
  * [Session 如何判断是否是同一会话](#session-如何判断是否是同一会话)
  * [Session存储](#session存储)
  * [Session 的缺点](#session-的缺点)
* [**Cookies 是什么**](#cookies-是什么)
* [Token](#token)
  * [**客户端 token 的存储方式**](#客户端-token-的存储方式)
  * [**防篡改**](#防篡改)
* [**JWT**](#jwt)
* [refresh token(目前没用过)](#refresh-token目前没用过)
* [session 和 token区别](#session-和-token区别)
* [Token是如何防止CSRF的](#token是如何防止csrf的)
* [什么是跨域？怎么解决](#什么是跨域怎么解决)



https://cloud.tencent.com/developer/article/1704064


HTTP 协议是一种<font color=red>无状态协议</font>，即每次服务端接收到客户端的请求时，都是一个全新的请求，服务器并不知道客户端的历史请求记录；Session 和 Cookie 的主要目的就是为了<font color=red>弥补 HTTP 的无状态特性</font>。

# Session 是什么

客户端请求服务端，服务端会为这次请求开辟一块<font color=red>内存空间</font>，这个对象便是 Session 对象，存储结构为<font color=red> ConcurrentHashMap</font>。Session 弥补了 HTTP 无状态特性，服务器可以利用 Session 存储客户端在同一个会话期间的一些操作记录。

## Session 如何判断是否是同一会话

服务器第一次接收到请求时，开辟了一块 Session 空间（创建了Session对象），同时生成一个 <font color=red>sessionId</font> ，并通过响应头的 **Set-Cookie：JSESSIONID=XXXXXXX **命令，向客户端发送要求设置 Cookie 的响应； 客户端收到响应后，在本机客户端设置了一个 **JSESSIONID=XXXXXXX **的 Cookie 信息，该 Cookie 的过期时间为浏览器会话结束；

接下来客户端每次向同一个网站发送请求时，请求头都会带上该 Cookie信息（包含 sessionId ）， 然后，服务器通过读取请求头中的 Cookie 信息，获取名称为 JSESSIONID 的值，得到此次请求的 sessionId。



![](.images/v2-18bd4826103a35c3ef4d99742ad1bd12_720w.jpg)



## Session存储

- Redis（推荐）：内存型数据库，redis中文官方网站。以 key-value 的形式存，正合 sessionId-sessionData 的场景；且访问快。
- 内存：直接放到变量里。一旦服务重启就没了
- 数据库：普通数据库。性能不高。

## Session 的缺点

Session 机制有个缺点，比如 A 服务器存储了 Session，就是做了[负载均衡]后，假如一段时间内 A 的访问量激增，会转发到 B 进行访问，但是 B 服务器并没有存储 A 的 Session，会导致 Session 的失效。->可以用redis做session共享



# **Cookies 是什么**



HTTP 协议中的 Cookie 包括 `Web Cookie` 和`浏览器 Cookie`，它是服务器发送到 Web 浏览器的一小块数据。服务器发送到浏览器的 Cookie，浏览器会进行存储，并与下一个请求一起发送到服务器。通常，它用于判断两个请求是否来自于同一个浏览器，例如用户保持登录状态。



> Domain属性指定浏览器发出 HTTP 请求时，哪些域名要附带这个 Cookie。如果没有指定该属性，浏览器会默认将其设为当前 URL 的一级域名，比如 [www.example.com](http://www.example.com/) 会设为 [example.com](http://example.com/)，而且以后如果访问[example.com](http://example.com/)的任何子域名，HTTP 请求也会带上这个 Cookie。如果服务器在Set-Cookie字段指定的域名，不属于当前域名，浏览器会拒绝这个 Cookie。
> Path属性指定浏览器发出 HTTP 请求时，哪些路径要附带这个 Cookie。只要浏览器发现，Path属性是 HTTP 请求路径的开头一部分，就会在头信息里面带上这个 Cookie。比如，PATH属性是/，那么请求/docs路径也会包含该 Cookie。当然，前提是域名必须一致。
> —— Cookie — JavaScript 标准参考教程（alpha）



> Secure属性指定浏览器只有在加密协议 HTTPS 下，才能将这个 Cookie 发送到服务器。另一方面，如果当前协议是 HTTP，浏览器会自动忽略服务器发来的Secure属性。该属性只是一个开关，不需要指定值。如果通信是 HTTPS 协议，该开关自动打开。
> HttpOnly属性指定该 Cookie 无法通过 JavaScript 脚本拿到，主要是Document.cookie属性、XMLHttpRequest对象和 Request API 都拿不到该属性。这样就防止了该 Cookie 被脚本读到，只有浏览器发出 HTTP 请求时，才会带上该 Cookie。
> —— Cookie — JavaScript 标准参考教程（alpha）

# Token

session 的维护给服务端造成很大困扰，我们必须找地方存放它，又要考虑分布式的问题，甚至要单独为了它启用一套 Redis 集群。有没有更好的办法？

一个登录场景，也不必往 session 存太多东西，那为什么不直接打包到 cookie 中呢？这样服务端不用存了，每次只要核验 cookie 带的「证件」有效性就可以了，也可以携带一些轻量的信息。

这种方式通常被叫做 token。


![](.images/v2-45c53c27a611d78c489a25f3496c7625_720w.jpg)

token 的流程是这样的：

- 用户登录，服务端校验账号密码，获得用户信息
- 把用户信息、token 配置编码成 token，通过 cookie set 到浏览器
- 此后用户请求业务接口，通过 cookie 携带 token
- 接口校验 token 有效性，进行正常业务接口处理



## **客户端 token 的存储方式**


在前面 cookie 说过，cookie 并不是客户端存储凭证的唯一方式。

token 因为它的「无状态性」，有效期、使用限制都包在 token 内容里，对 cookie 的管理能力依赖较小，客户端存起来就显得更自由。

但 web 应用的主流方式仍是放在 cookie 里，毕竟少操心。



##  **防篡改**

> 那问题来了，如果用户 cdd 拿`{"userid":"abb”}`转了个 base64，再手动修改了自己的 token 为 `eyJ1c2VyaWQiOiJhIn0=`，是不是就能直接访问到 abb 的数据了？

是的。所以看情况，如果 token 涉及到敏感权限，就要想办法避免 token 被篡改。



# **JWT**

>JSON Web Token (JWT) 是一个开放标准，定义了一种传递 JSON 信息的方式。这些信息通过数字签名确保可信。


 JWT token例子：

```text
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyaWQiOiJhIiwiaWF0IjoxNTUxOTUxOTk4fQ.2jf3kl_uKWRkwjOP6uQRJFqMlwSABcgqqcJofFH5XCo
```



![](.images/v2-355749afd88316167254eb40b0cb0739_720w.jpg)



可以将JWT放入Redis的同时，一起把过期时间放入进去



# refresh token(目前没用过)

业务接口用来鉴权的 token，我们称之为 access token。越是权限敏感的业务，我们越希望 access token 有效期足够短，以避免被盗用。但过短的有效期会造成 access token 经常过期，过期后怎么办呢？

一种办法是，让用户重新登录获取新 token，显然不够友好，要知道有的 access token 过期时间可能只有几分钟。

另外一种办法是，再来一个 token，一个专门生成 access token 的 token，我们称为 refresh token。

- access token 用来访问业务接口，由于有效期足够短，盗用风险小，也可以使请求方式更宽松灵活
- refresh token 用来获取 access token，有效期可以长一些，通过独立服务和严格的请求方式增加安全性；由于不常验证，也可以如前面的 session 一样处理


![](.images/v2-8305486dc3674c6ff3e48bbac52194aa_720w.jpg)


# session 和 token区别


 session 是「客户端在 cookie 上、数据存在服务端」的认证方案，token 是「客户端存哪都行、数据存在 token 里」的认证方案。



# Token是如何防止CSRF的

 CSRF 攻击只是借用了 Cookie，并不能获取 Cookie 中的信息，但是可以利用cookie发送本不属于自己系统的请求。

这里其实攻击者是拿得到token的！

+ 对token加密 ，加解密，比较耗性能。
+ **一次性token**（每次刷新，返回token和随机数hash，后端校验token和随机数）随机数相同概率基本无





# 什么是跨域？怎么解决

