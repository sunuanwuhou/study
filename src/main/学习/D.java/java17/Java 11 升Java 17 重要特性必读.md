# Table of Contents

* [Switch模式匹配](#switch模式匹配)
* [参考资料](#参考资料)




# Switch模式匹配

+ JDK11以前：

```java
public static void JDK11_switch() {
    String day = "MONDAY";
    switch (day) {
        case "MONDAY":
            System.out.println(1);
            break;
        case "TUESDAY":
            System.out.println(2);
            break;
        default:
            System.out.println(0);
            break;
    }
}
```

+ JDK13简化了返回值

  ```java
  public static void JDK13_switch() {
      String day = "MONDAY";
      int i = switch (day) {
          case "MONDAY" -> 1;
          case "TUESDAY" -> 2;
          case "WEDNESDAY" -> 3;
          case "THURSDAY" -> 4;
          case "FRIDAY" -> 5;
          case "SATURDAY" -> 6;
          case "SUNDAY" -> 7;
          default -> 0;
      };
      System.out.println(i);
  }
  
  ```

+ 从JDK17以前，switch不支持[instanceof](https://so.csdn.net/so/search?q=instanceof&spm=1001.2101.3001.7020)，如果有多个instanceof只能用if-else来表达：

  ```java
  public static void JDK17_before_instanceof_switch(Object o) {
      //o instanceof Integer i 为JDK16新特性
      if (o instanceof Integer i) {
          System.out.println(i);
      } else if (o instanceof Long l) {
          System.out.println(l);
      } else if (o instanceof Double d) {
          System.out.println(d);
      } else if (o instanceof String s) {
          System.out.println(s);
      } else {
          System.out.println("UNKNOWN");
      }
  }
  public static void JDK17_instanceof_switch(Object o) {
      switch (o) {
          case Integer i -> System.out.println(i);
          case Long l -> System.out.println(l);
          case Double d -> System.out.println(d);
          case String s -> System.out.println(s);
          default -> System.out.println("UNKNOWN");
      }
  }
  ```

  


# 参考资料

https://mp.weixin.qq.com/s/f3KcSHL1raRBJ8r2Y3xJeg
