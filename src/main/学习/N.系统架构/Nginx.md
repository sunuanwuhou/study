# Table of Contents

* [NG内置变量](#ng内置变量)
* [root和alias](#root和alias)
* [proxy_pass](#proxy_pass)






# NG内置变量





# root和alias

https://www.jianshu.com/p/44fc4d7771e3



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


对于自定义请求头或者其他的时候，可以采取这种方式进行转发。

这个当时搞了我好久。。。。贼难忘


