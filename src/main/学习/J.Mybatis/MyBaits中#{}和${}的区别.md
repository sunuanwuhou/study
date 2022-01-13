# Table of Contents

* [${}和#{}的区别](#和的区别)
* [#{} 为什么能防止Sql注入？](#-为什么能防止sql注入)
* [场景使用](#场景使用)
* [参考资料](#参考资料)


# ${}和#{}的区别

+ `#{}`匹配的是一个占位符，相当于JDBC中的一个?，**会对一些敏感的字符进行过滤，编译过后会对传递的值加上双引号**，因此可以防止SQL注入问题。

+ `${}`匹配的是真实传递的值，传递过后，会与sql语句进行字符串拼接。**只是简单的字符串替换**。${}会与其他sql进行字符串拼接，不能预防sql注入问题。


我们看个例子：

```mysql
select * from user where id= #{user_id}，如果传入的值是11,那么解析成sql时的值为where id="11" ，
```

```mysql
select * from user where id= $ {user_id}，如果传入的值是11,那么解析成sql时的值为where id=11
```



# #{} 为什么能防止Sql注入？

打开PreparedStatement类的setString()方法（MyBatis在`#{}`传递参数时，是借助setString()方法来完成，`${}`则不是）



源码部分

```java

                    for(int i = 0; i < stringLength; ++i) {  //遍历字符串，获取到每个字符
                        char c = x.charAt(i);
                        switch(c) {
                        case '\u0000':
                            buf.append('\\');
                            buf.append('0');
                            break;
                        case '\n':
                            buf.append('\\');
                            buf.append('n');
                            break;
                        case '\r':
                            buf.append('\\');
                            buf.append('r');
                            break;
                        case '\u001a':
                            buf.append('\\');
                            buf.append('Z');
                            break;
                        case '"':
                            if (this.usingAnsiMode) {
                                buf.append('\\');
                            }

                            buf.append('"');
                            break;
                        case '\'':
                            buf.append('\\');
                            buf.append('\'');
                            break;
                        case '\\':
                            buf.append('\\');
                            buf.append('\\');
                            break;
                        case '¥':
                        case '₩':
                            if (this.charsetEncoder != null) {
                                CharBuffer cbuf = CharBuffer.allocate(1);
                                ByteBuffer bbuf = ByteBuffer.allocate(1);
                                cbuf.put(c);
                                cbuf.position(0);
                                this.charsetEncoder.encode(cbuf, bbuf, true);
                                if (bbuf.get(0) == 92) {
                                    buf.append('\\');
                                }
                            }

                            buf.append(c);
                            break;
                        default:
                            buf.append(c);
                        }
                    }
```



可以看到此方法会获取传递进来的参数的每个字符，然后进行循环对比，**如果发现有敏感字符（如：单引号、双引号等），则会在前面加上一个'/'代表转义此符号，让其变为一个普通的字符串**，不参与SQL语句的生成，达到防止SQL注入的效果。



# 场景使用

\#{}的应用场景是为给SQL语句的where字句传递条件值，${}的应用场景是为了传递一些需要参与SQL语句语法生成的



# 参考资料

https://blog.csdn.net/Bb15070047748/article/details/107188167
