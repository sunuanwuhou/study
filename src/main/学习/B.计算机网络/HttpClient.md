# Table of Contents

* [使用代理](#使用代理)


# 使用代理

```java
HttpHost proxy = new HttpHost("proxy.com", 80, "http");
DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
CloseableHttpClient httpclient = HttpClients.custom()
                    .setRoutePlanner(routePlanner)
                    .build();
```

