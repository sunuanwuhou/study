# Table of Contents

* [NG内置变量](#ng内置变量)
* [监听域名](#监听域名)
* [监听端口](#监听端口)
* [监听域名和端口](#监听域名和端口)
* [负载均衡](#负载均衡)
* [静态资源](#静态资源)
* [root和alias](#root和alias)
* [proxy_pass](#proxy_pass)
* [Nginx转发Host问题](#nginx转发host问题)
* [rewrite](#rewrite)
* [Nginx配置限流](#nginx配置限流)
  * [速率](#速率)
  * [并发数](#并发数)






# NG内置变量



https://blog.51cto.com/u_15274085/2919075





# 监听域名



```nginx
server {
    # Listen to yourdomain.com
    server_name yourdomain.com;
    # Listen to multiple domains  server_name yourdomain.com www.yourdomain.com;
    # Listen to all domains
    server_name *.yourdomain.com;
    # Listen to all top-level domains
    server_name yourdomain.*;
    # Listen to unspecified Hostnames (Listens to IP address itself)
    server_name "";
}
```



# 监听端口

```nginx
server {
    # Standard HTTP Protocol
    listen 80;
    # Standard HTTPS Protocol
    listen 443 ssl;
    # For http2
    listen 443 ssl http2;
    # Listen on 80 using IPv6
    listen [::]:80;
    # Listen only on using IPv6
    listen [::]:80 ipv6only=on;
}
```



# 监听域名和端口

```nginx
server {
        listen       80; #监听端口
        server_name  manage.enjoyment.com;# 监听域名

		# 头信息
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Server $host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

		# location请求映射规则，/ 代表一切请求路径
        location / {
			proxy_pass http://127.0.0.1:9001; # 代理转发，所有manage.enjoyment.com和80访问的请求，都会被转发到http://127.0.0.1:9001处理
			proxy_connect_timeout 600;
			proxy_read_timeout 600;
        }
    }
```



# 负载均衡

```nginx
upstream node_js {
    server 0.0.0.0:3000;
    server 0.0.0.0:4000;
    server 123.131.121.122;
}

server {
    listen 80;
    server_name yourdomain.com;
    location / {
        proxy_pass http://node_js;
    }
}
```





# 静态资源

```nginx
server {
    listen 80;
    server_name yourdomain.com;
    location / {
        root /path/to/website;  # 代理转发，所有yourdomain.com和80访问的请求，都会被转发到/path/to/website路径下
    }
}
```







# root和alias

https://cloud.tencent.com/developer/article/1945979




root与alias主要区别在于nginx如何解释location后面的uri，这会使两者分别以不同的方式将请求映射到服务器文件上。

+ root的处理结果是：`root路径＋location路径`
+ alias的处理结果是：使用alias路径`替换`location路径

```nginx
server {
    listen 80;
    server_name test.html.com;
 
     location /img/ {
        root /var/www/image
    }
	# 若按照上述配置的话，访问/img目录里面的文件时, nginx会自动去/var/www/image/img去找  【root+location】

	location /img/ {
 		alias /var/www/image/
	}
	# 若按照上述配置的话，访问/img目录里面的文件时, nginx会自动去/var/www/image目录找文件  【alias路径替换location】
}
```



# proxy_pass

![image-20220108141329957](.images/image-20220108141329957.png)

对于自定义请求头，【**使用变量**】的时候，可以采取这种方式进行转发。

如果实在链接需要带/ 

可以使用rewrite 重写规则

这个当时搞了我好久。。。。贼难忘



# Nginx转发Host问题

NG转发的时候，要注意host

参考地址： https://www.cnblogs.com/operationhome/p/14232793.html



# rewrite

+ 原始地址：api/test
+ 配置： rewrite "^/api/(.*)$" /$$1 break;
+ 真实访问：/test

> ```javascript
> (1)"^/api/(.)$"：匹配路径的正则表达式，用了分组语法就是*(.)**，把/api/以后的所有部分当做1组；
> （2）/$1：重写的目标路径，这里用$1引用前面正则表达式匹配到的分组（组编号从1开始，也就是api），即/api/后面的所有。这样新的路径就是除去/api/以外的所有，就达到了去除/api前缀的目的
> ```




# Nginx配置限流

Nginx 提供了两种限流手段：一是控制速率，二是控制并发连接数。



## 速率

```xml
limit_req_zone $binary_remote_addr zone=mylimit:10m rate=2r/s;
server { 
    location / { 
        limit_req zone=mylimit burst=4;
    }
}
```

burst=4 表示每个 IP 最多允许4个突发请求

限制每个 IP 访问的速度为 2r/s，因为 Nginx 的限流统计是基于毫秒的，我们设置的速度是 2r/s，转换一下就是 500ms 内单个 IP 只允许通过 1 个请求，从 501ms 开始才允许通过第 2 个请求。

如果单个 IP 在 10ms 内发送 6 次请求的结果如下


从以上结果可以看出，有 1 个请求被立即处理了，4 个请求被放到 burst 队列里排队执行了，另外 1 个请求被拒绝了。

## 并发数

利用 `limit_conn_zone` 和 `limit_conn` 两个指令即可控制并发数，示例配置如下：

```text
limit_conn_zone $binary_remote_addr zone=perip:10m;
limit_conn_zone $server_name zone=perserver:10m;
server {
    ...
    limit_conn perip 10;
    limit_conn perserver 100;
}
```

其中 limit_conn perip 10 表示限制单个 IP 同时最多能持有 10 个连接；limit_conn perserver 100 表示 server 同时能处理并发连接的总数为 100 个。

> 小贴士：只有当 request header 被后端处理后，这个连接才进行计数。
