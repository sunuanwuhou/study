
# 概述
ArrayList实现了List接口，是顺序容器，即元素存放的数据与放进去的顺序相同，允许放入null元素，底层通过数组实现。

除该类未实现同步外，其余跟Vector大致相同。每个ArrayList都有一个容量(capacity)，表示底层数组的实际大小，容器内存储元素的个数不能多于当前容量。

当向容器中添加元素时，如果容量不足，容器会自动增大底层数组的大小。

前面已经提过，Java泛型只是编译器提供的语法糖，所以这里的数组是一个Object数组，以便能够容纳任何类型的对象。






<div align=left>
	<img src=".images/image-20210705140520815.png" width="">
</div>


size(), isEmpty(), get(), set()方法均能在常数时间内完成，

add()方法的时间开销跟插入位置有关，addAll()方法的时间开销跟添加元素的个数成正比。其余方法大都是线性时间。

为追求效率，ArrayList没有实现同步(synchronized)，如果需要多个线程并发访问，用户可以手动同步，也可使用Vector替代。



# 相关问题

## java本身就有数组了，为什么还要用ArrayList？

原生数据有一个特点:你创建元素的时候，必须要创建大小

而`ArrayList`？实现了**动态扩容**！

```java
List<String> list = new ArrayList<>();
list.add()
```

每次add时，都会检查数据够不够空间，不够需要扩容，每次扩容为1.5倍。

```java
  private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
      	//当前数组+当前数组*2=1.5
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
```



## 为什么ArrayList使用的最多？

大多数业务场景是读多写少，符合数组这种结构特点，时间复杂度O(1)

arraycopy优化过？暂未获取相关信息。

## 线程安全怎么解决？

+ Vector:太笨重了 不考虑

+ CopyOnWriteArrayList:COW 写时复制 很多进程FORK都是用到这种思想。

  

```java
//锁住当前数组
//copy一个新数组，并往里面添加元素
//改变执行-》新数组
public boolean add(E e) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            Object[] newElements = Arrays.copyOf(elements, len + 1);
            newElements[len] = e;
            setArray(newElements);
            return true;
        } finally {
            lock.unlock();
        }
    }
```

缺点：

1. 每次都会copy一个数组，是比较费时间的。
2. 数据一致性问题：操作没完成的时候，你获取的是旧数组的信息。

> 跟操作系统的`写时复制`还是不太一样，操作系统级别，在修改元素的时候才会发生复制，之前元素不变的时候都是共享的。

