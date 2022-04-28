# Table of Contents



- Cache-Control是HTTP/1.1的头字段，用来区分对缓存机制的支持情况，请求头和响应头都支持这个属性。通过它提供的不同的值来定义缓存策略。主要有`public、private、no-cache`等值。
- expires是http1.0的头字段，过期时间，如果设置了时间，则浏览器会在设置的时间内直接读取缓存，不再请求。
