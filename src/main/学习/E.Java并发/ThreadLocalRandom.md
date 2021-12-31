# Table of Contents

* [Random](#random)
* [ThreadLocalRandom](#threadlocalrandom)
* [参考资料](#参考资料)




# Random



```java
   protected int next(int bits) {
        long oldseed, nextseed;
        AtomicLong seed = this.seed;
        do {
        	// 6 
            oldseed = seed.get();
            // 7 
            nextseed = (oldseed * multiplier + addend) & mask;
            // 8
        } while (!seed.compareAndSet(oldseed, nextseed));
        // 9 
        return (int)(nextseed >>> (48 - bits));
    }
```


总结：每个Random实例里面都有一个原子性的种子变量用来记录当前的种子值，当要生成新的随机数时需要根据当前种子计算新的种子并更新回原子变量。**在多线程下使用单个Random实例生成随机数时，当多个线程同时计算随机数来计算新的种子时，多个线程会竞争同一个原子变量的更新操作，由于原子变量的更新是CAS操作，同时只有一个线程会成功，所以会造成大量线程进行自旋重试，这会降低并发性能，所以ThreadLocalRandom应运而生**。



# ThreadLocalRandom



从名字上看它会让我们联想到ThreadLocal：ThreadLocal通过让每一个线程复制一份变量，使得在每个线程对变量进行操作时实际是操作自己本地内存里面的副本，从而避免了对共享变量进行同步。

实际上ThreadLocalRandom的实现也是这个原理，**Random的缺点是多个线程会使用同一个原子性种子变量，从而导致对原子变量更新的竞争**.



那么，**如果每个线程都维护一个种子变量**，则每个线程生成随机数时都根据自己老的种子计算新的种子，并使用新种子更新老的种子，再根据新种子计算随机数，就不会存在竞争问题了，这会大大提高并发性能。

> 这里会不会有一个问题？
>
> 每个线程维护一个自己的种子变量，那么有可能2个线程的随机数是一致的!








# 参考资料

【Java Review - 并发编程_ThreadLocalRandom实现原理&源码分析】https://cloud.tencent.com/developer/article/1907374
