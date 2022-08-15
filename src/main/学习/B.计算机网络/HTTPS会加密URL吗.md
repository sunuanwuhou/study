# Table of Contents

* [**HTTPS 会加密 URL 吗？**](#https-会加密-url-吗)
* [**HTTPS 可以看到域名吗？**](#https-可以看到域名吗)




# **HTTPS 会加密 URL 吗？**

**答案是，会加密的。**

因为 URL 的信息都是保存在 HTTP Header 中的，而 HTTPS 是会对 **HTTP Header + HTTP Body** 整个加密的，所以 URL 自然是会被加密的。



# **HTTPS 可以看到域名吗？**

域名是在head里面的，所以看不到的。

但是我们**可以在 TLS 握手过程中看到域名信息**。
