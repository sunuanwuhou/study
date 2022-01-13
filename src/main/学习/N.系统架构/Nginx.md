# Table of Contents

* [NG内置变量](#ng内置变量)
* [root和alias](#root和alias)
* [proxy_pass](#proxy_pass)
* [Nginx转发Host问题](#nginx转发host问题)
* [rewrite](#rewrite)






# NG内置变量





# root和alias

https://www.jianshu.com/p/44fc4d7771e3




root与alias主要区别在于nginx如何解释location后面的uri，这会使两者分别以不同的方式将请求映射到服务器文件上。

+ root的处理结果是：`root路径＋location路径`
+ alias的处理结果是：使用alias路径`替换`location路径

```xml
server {
    listen 80;
    server_name test.html.com;
 
    location ^~ /test/html/ {
        root   /workspace/www;
	    alias   /workspace/1;
    }
}
```



# proxy_pass

![image-20220108141329957](.images/image-20220108141329957.png)


对于自定义请求头，【**使用变量**】的时候，可以采取这种方式进行转发。

这个当时搞了我好久。。。。贼难忘



# Nginx转发Host问题

NG转发的时候，要注意host

参考地址： https://www.cnblogs.com/operationhome/p/14232793.html


# rewrite

+ 原始地址：api/test
+ 配置： rewrite "^/api/(.*)$" /$1 break;
+ 真实访问：/test
