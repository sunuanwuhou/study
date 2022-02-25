# Table of Contents

* [WATCH 机制原理](#watch-机制原理)






# WATCH 机制原理

WATCH 机制：**使用 WATCH 监视一个或多个 key , 跟踪 key 的 value 修改情况，如果有key 的 value 值在事务 EXEC 执行之前被修改了，整个事务被取消。EXEC 返回提示信息，表示事务已经失败**。

WATCH 机制使的事务 EXEC 变的有条件，事务只有在被 WATCH 的 key 没有修改的前提下才能执行。不满足条件，事务被取消。使用 WATCH 监视了一个带过期时间的键，那么即使这个键过期了，事务仍然可以正常执行.
大多数情况下，不同的客户端会访问不同的键，相互同时竞争同一 key 的情况一般都很少，watch 能很好解决数据冲突的问题。





