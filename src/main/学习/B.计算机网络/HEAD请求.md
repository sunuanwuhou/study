# Table of Contents

* [什么是HEAD请求](#什么是head请求)
* [应用场景](#应用场景)




# 什么是HEAD请求



**HEAD方法跟GET方法相同，只不过服务器响应时不会返回消息体**。一个HEAD请求的响应中，HTTP头中包含的元信息应该和一个GET请求的响应消息相同。这种方法可以用来获取请求中隐含的元信息，而不用传输实体本身。也经常用来测试超链接的有效性、可用性和最近的修改。



# 应用场景

+ 获取文件大小，根据文件链接

  ```java
  import java.io.IOException;
  import java.net.HttpURLConnection;
  import java.net.URL;
  
  public class FileSizeChecker {
  
      public static long getFileSize(String url) throws IOException {
          HttpURLConnection connection = null;
          try {
              connection = (HttpURLConnection) new URL(url).openConnection();
              connection.setRequestMethod("HEAD");
              return connection.getContentLengthLong();
          } finally {
              if (connection != null) {
                  connection.disconnect();
              }
          }
      }
  
  }
  ```

  

