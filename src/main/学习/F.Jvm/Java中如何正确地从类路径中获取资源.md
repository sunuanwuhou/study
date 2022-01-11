# Table of Contents

* [如何获取Class和ClassLoader](#如何获取class和classloader)
* [Class.getResource 与 ClassLoader.getResource 的区别](#classgetresource-与-classloadergetresource-的区别)
* [ClassLoader.getResources](#classloadergetresources)
* [Spring的ResourceLoader](#spring的resourceloader)


在Java中，通常可以通过以下方式来访问资源：

- `Class`的`getResource`方法;
- `ClassLoader`的`getResource`方法;
- `ClassLoader`的`getResources`方法; //获取批量资源
- `ClassLoader`的`getSystemResource`; //静态方法



# 如何获取Class和ClassLoader

+ Class

  ```java
              Class<?> ClassTest = Class.forName("com.qm.study.Jvm.ClassTest");
              ClassTest classTest = (ClassTest) ClassTest.newInstance();
  
              Class<? extends ClassTest> classTest1 = new ClassTest().getClass();
  
              Class<ClassTest> classTest2 = ClassTest.class;
  ```

  

+ ClassLoader
	
- 调用Class的getClassLoader方法，如：getClass().getClassLoader()
  - 由当前线程获取ClassLoader：Thread.currentThread().getContextClassLoader()
  - 获取系统ClassLoader: ClassLoader.getSystemClassLoader()
  
  
  

# Class.getResource 与 ClassLoader.getResource 的区别

两者最大的区别，是从【**哪里开始**】寻找资源。

- ClassLoader并不关心当前类的包名路径，它永远以classpath为基点来定位资源。需要注意的是在用ClassLoader加载资源时，路径不要以"/"开头，所有以"/"开头的路径都返回null；
- Class.getResource如果资源名是绝对路径(以"/"开头)，那么会以classpath为基准路径去加载资源，如果不以"/"开头，那么以这个类的Class文件所在的路径为基准路径去加载资源。



> Class就是当前类的加载路径
>
> ClassLoader是当前classPath路径


在实际开发过程中建议使用Class.getResource这个方法



# ClassLoader.getResources

使用classLoader的getResources方法可以获得批量资源

```java
ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
Enumeration<URL> resources = classLoader.getResources("META-INF/MANIFEST.MF");
```



# Spring的ResourceLoader

在Spring框架中ResourceLoader和ResourcePatternResolver接口封装了获取资源的方法，我们可以直接拿来使用。ResourceUtils这个类中提供了很多判断资源类型的工具方法，可以直接使用。
