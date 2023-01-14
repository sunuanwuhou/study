# Table of Contents

* [InheritableThreadLocal类](#inheritablethreadlocal类)
* [线程间传值实现原理](#线程间传值实现原理)
  * [用户创建线程](#用户创建线程)
  * [ThreadLocal.createInheritedMap](#threadlocalcreateinheritedmap)
* [总结](#总结)
* [线程池间传递](#线程池间传递)
* [参考资料](#参考资料)


ThreadLocal设计之初就是为了绑定当前线程，如果希望当前线程的ThreadLocal能够被子线程使用，实现方式就会相当困难（需要用户自己在代码中传递）。在此背景下，InheritableThreadLocal应运而生。



# InheritableThreadLocal类

InheritableThreadLocal类重写了ThreadLocal的3个函数：



```cpp
    /**
     * 该函数在父线程创建子线程，向子线程复制InheritableThreadLocal变量时使用
     */
    protected T childValue(T parentValue) {
        return parentValue;
    }
```



```dart
    /**
     * 由于重写了getMap，操作InheritableThreadLocal时，
     * 将只影响Thread类中的inheritableThreadLocals变量，
     * 与threadLocals变量不再有关系
     */
    ThreadLocalMap getMap(Thread t) {
       return t.inheritableThreadLocals;
    }
```



```dart
    /**
     * 类似于getMap，操作InheritableThreadLocal时，
     * 将只影响Thread类中的inheritableThreadLocals变量，
     * 与threadLocals变量不再有关系
     */
    void createMap(Thread t, T firstValue) {
        t.inheritableThreadLocals = new ThreadLocalMap(this, firstValue);
    }
```



# 线程间传值实现原理

说到InheritableThreadLocal，还要从Thread类说起：



```java
public class Thread implements Runnable {
   ......(其他源码)
    /* 
     * 当前线程的ThreadLocalMap，主要存储该线程自身的ThreadLocal
     */
    ThreadLocal.ThreadLocalMap threadLocals = null;

    /*
     * InheritableThreadLocal，自父线程集成而来的ThreadLocalMap，
     * 主要用于父子线程间ThreadLocal变量的传递
     * 本文主要讨论的就是这个ThreadLocalMap
     */
    ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;
    ......(其他源码)
}
```

Thread类中包含 *threadLocals* 和 *inheritableThreadLocals* 两个变量，其中 **inheritableThreadLocals** 即主要存储可自动向子线程中传递的ThreadLocal.ThreadLocalMap。
接下来看一下父线程创建子线程的流程，我们从最简单的方式说起：

## 用户创建线程

```java
Thread thread = new Thread();

 public Thread() {
        init(null, null, "Thread-" + nextThreadNum(), 0);
 }

/**
     * 默认情况下，设置inheritThreadLocals可传递
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize) {
        init(g, target, name, stackSize, null, true);
    }


    /**
     * 初始化一个线程.
     * 此函数有两处调用，
     * 1、上面的 init()，不传AccessControlContext，inheritThreadLocals=true
     * 2、传递AccessControlContext，inheritThreadLocals=false
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc,
                      boolean inheritThreadLocals) {
        ......（其他代码）

        if (inheritThreadLocals && parent.inheritableThreadLocals != null)
            this.inheritableThreadLocals =
                ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);

        ......（其他代码）
    }

可以看到，采用默认方式产生子线程时，inheritThreadLocals=true；若此时父线程inheritableThreadLocals不为空，则将父线程inheritableThreadLocals传递至子线程。

```

## ThreadLocal.createInheritedMap

```java
    static ThreadLocalMap createInheritedMap(ThreadLocalMap parentMap) {
        return new ThreadLocalMap(parentMap);
    }
```

```java
        /**
         * 构建一个包含所有parentMap中Inheritable ThreadLocals的ThreadLocalMap
         * 该函数只被 createInheritedMap() 调用.
         */
        private ThreadLocalMap(ThreadLocalMap parentMap) {
            Entry[] parentTable = parentMap.table;
            int len = parentTable.length;
            setThreshold(len);
            // ThreadLocalMap 使用 Entry[] table 存储ThreadLocal
            table = new Entry[len];

            // 逐一复制 parentMap 的记录
            for (int j = 0; j < len; j++) {
                Entry e = parentTable[j];
                if (e != null) {
                    @SuppressWarnings("unchecked")
                    ThreadLocal<Object> key = (ThreadLocal<Object>) e.get();
                    if (key != null) {
                        // 可能会有同学好奇此处为何使用childValue，而不是直接赋值，
                        // 毕竟childValue内部也是直接将e.value返回；
                        // 个人理解，主要为了减轻阅读代码的难度
                        Object value = key.childValue(e.value);
                        Entry c = new Entry(key, value);
                        int h = key.threadLocalHashCode & (len - 1);
                        while (table[h] != null)
                            h = nextIndex(h, len);
                        table[h] = c;
                        size++;
                    }
                }
            }
        }
```

从ThreadLocalMap可知，子线程将parentMap中的所有记录逐一复制至自身线程。

# 总结

InheritableThreadLocal主要用于子线程创建时，需要自动继承父线程的ThreadLocal变量，方便必要信息的进一步传递。



# 线程池间传递

会使用多线程编程，为了保证调用链ID能够自然的在多线程间传递，需要考虑ThreadLocal传递问题（大多数系统会使用线程池技术，这已经不仅仅是InheritableThreadLocal能够解决的了，我会在另外一篇文章中介绍相关技术实现）。

[线程池之间变量传递](线程池之间变量传递.md)

# 参考资料

[InheritableThreadLocal详解 - 简书 (jianshu.com)](https://www.jianshu.com/p/94ba4a918ff5)
