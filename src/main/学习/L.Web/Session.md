# Table of Contents

* [什么是会话](#什么是会话)
* [为什么会出现session和cookie](#为什么会出现session和cookie)
* [Session 是什么](#session-是什么)
  * [Session 如何判断是否是同一会话](#session-如何判断是否是同一会话)
  * [Session存储](#session存储)
  * [Session 的缺点](#session-的缺点)
* [**Cookies 是什么**](#cookies-是什么)
  * [注意](#注意)
* [Token](#token)
  * [**客户端 token 的存储方式**](#客户端-token-的存储方式)
  * [**防篡改**](#防篡改)
* [**JWT**](#jwt)
* [refresh token(目前没用过)](#refresh-token目前没用过)
* [session 和 token区别](#session-和-token区别)
* [什么是CSRF](#什么是csrf)
  * [Token是如何防止CSRF的](#token是如何防止csrf的)
* [什么是XSS](#什么是xss)
* [什么是跨域？怎么解决](#什么是跨域怎么解决)
* [总结](#总结)
* [参考资料](#参考资料)







# 什么是会话

所谓的会话过程就是指从打开浏览器到关闭浏览器的过程。



# 为什么会出现session和cookie



HTTP 协议是一种<font color=red>无状态协议</font>，即每次服务端接收到客户端的请求时，都是一个全新的请求，服务器并不知道客户端的历史请求记录；Session 和 Cookie 的主要目的就是为了**<font color=red>弥补 HTTP 的无状态特性</font>**。



# Session 是什么

客户端请求服务端，服务端会为这次请求开辟一块<font color=red>内存空间</font>，这个对象便是 Session 对象，存储结构为<font color=red> ConcurrentHashMap</font>。Session 弥补了 HTTP 无状态特性，服务器可以利用 Session 存储客户端在同一个会话期间的一些操作记录。

## Session 如何判断是否是同一会话

- 首先，客户端会发送一个http请求到服务器端。
- 服务器端接受客户端请求后，建立一个session，并发送一个http响应到客户端，这个响应头，其中就包含Set-Cookie头部。该头部包含了sessionId。Set-Cookie格式如下，具体请看Cookie详解 `Set-Cookie: value[; expires=date][; domain=domain][; path=path][; secure]`
- 在客户端发起的第二次请求，假如服务器给了set-Cookie，浏览器会自动在请求头中添加cookie
- 服务器接收请求，分解cookie，验证信息，核对成功后返回response给客户端



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



## 注意

- cookie只是实现session的其中一种方案。**虽然是最常用的，但并不是唯一的方法**。禁用cookie后还有其他方法存储，比如放在url中
- 现在大多都是Session + Cookie，但是只用session不用cookie，或是只用cookie，不用session在理论上都可以保持会话状态。可是实际中因为多种原因，一般不会单独使用
- 用session只需要在客户端保存一个id，实际上大量数据都是保存在服务端。如果全部用cookie，数据量大的时候客户端是没有那么多空间的。



# Token

**session 的维护给服务端造成很大困扰，我们必须找地方存放它，又要考虑分布式的问题，甚至要单独为了它启用一套 Redis 集群**。有没有更好的办法？

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

+ session有状态，存储在服务端。
+ token 无状态.token是开发自定义颁发给客户端使用，不存储



# 什么是CSRF

[](https://tech.meituan.com/2018/10/11/fe-security-csrf.html)

CSRF（Cross-site request forgery）跨站请求伪造：攻击者诱导受害者进入第三方网站，在第三方网站中，向被攻击网站发送跨站请求。利用受害者在被攻击网站已经获取的注册凭证，绕过后台的用户验证，达到冒充用户对被攻击的网站执行某项操作的目的。



## Token是如何防止CSRF的

 CSRF 攻击只是借用了 Cookie，并不能获取 Cookie 中的信息，但是可以利用cookie**发送本不属于自己系统的请求**。

这里其实攻击者是拿得到token的！

+ CSRF自动防御策略：同源检测（Origin 和 Referer 验证）

+ token隐藏到页面。(工作量比较大)

+ 对token加密 ，加解密，比较耗性能。
+ **一次性token**（每次刷新，返回token和随机数hash，后端校验token和随机数,）随机数相同概率基本无






# 什么是跨域？怎么解决



# 总结

1. 因为HTPP是无状态协议，所以出现了Session和Cookie
2. 因为session是有状态的，分布式下负载均衡比较麻烦。不能单独为了登录搭建系统
3. 所以出现了token，token是无状态的，由开发自定义，颁发给客户端。
4. token容易被劫持，所以出现了加密机制。JWT



# 参考资料

https://cloud.tencent.com/developer/article/1704064
