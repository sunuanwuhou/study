# Table of Contents

* [什么是建造者模式](#什么是建造者模式)
* [@Builder使用](#builder使用)
  * [@Builder中使用 @Singular 注释集合](#builder中使用-singular-注释集合)
  * [@Builder.Default 的使用](#builderdefault-的使用)
  * [缺点](#缺点)


# 什么是建造者模式



建造者（Builder）模式的定义：指将一个复杂对象的构造与它的表示分离，使同样的构建过程可以创建不同的表示，这样的[设计模式](http://c.biancheng.net/design_pattern/)被称为建造者模式。它是将一个复杂的对象分解为多个简单的对象，然后一步一步构建而成。它将变与不变相分离，即产品的组成部分是不变的，但每一部分是可以灵活选择的。

该模式的主要优点如下：

1. 封装性好，构建和表示分离。
2. 扩展性好，各个具体的建造者相互独立，有利于系统的解耦。
3. 客户端不必知道产品内部组成的细节，建造者可以对创建过程逐步细化，而不对其它模块产生任何影响，便于控制细节风险。



其缺点如下：

1. 产品的组成部分必须相同，这限制了其使用范围。
2. 如果产品的内部变化复杂，如果产品内部发生变化，则建造者也要同步修改，后期维护成本较大。





# @Builder使用

```java
@Builder
public class User {
    private final Integer code = 200;
    private String username;
    private String password;
}

// 编译后：
public class User {
    private String username;
    private String password;
    User(String username, String password) {
        this.username = username; this.password = password;
    }
    public static User.UserBuilder builder() {
        return new User.UserBuilder();
    }

    public static class UserBuilder {
        private String username;
        private String password;
        UserBuilder() {}

        public User.UserBuilder username(String username) {
            this.username = username;
            return this;
        }
        public User.UserBuilder password(String password) {
            this.password = password;
            return this;
        }
        public User build() {
            return new User(this.username, this.password);
        }
        public String toString() {
            return "User.UserBuilder(username=" + this.username + ", password=" + this.password + ")";
        }
    }
}
```



使用

```java
Student.builder()
               .sno( "001" )
               .sname( "admin" )
               .sage( 18 )
               .sphone( "110" )
               .build();
```



## @Builder中使用 @Singular 注释集合

```java
 Student.builder()
                .sno( "001" )
                .sname( "admin" )
                .sage( 18 )
                .sphone( "110" ).sphone( "112" )
                .build();
```



编译后代码

```java
@Builder
public class User {
    private final Integer id;
    private final String zipCode = "123456";
    private String username;
    private String password;
    @Singular
    private List<String> hobbies;
}

// 编译后：
public class User {
    private final Integer id;
    private final String zipCode = "123456";
    private String username;
    private String password;
    private List<String> hobbies;
    User(Integer id, String username, String password, List<String> hobbies) {
        this.id = id; this.username = username;
        this.password = password; this.hobbies = hobbies;
    }

    public static User.UserBuilder builder() {return new User.UserBuilder();}

    public static class UserBuilder {
        private Integer id;
        private String username;
        private String password;
        private ArrayList<String> hobbies;
        UserBuilder() {}
        public User.UserBuilder id(Integer id) { this.id = id; return this; }
        public User.UserBuilder username(String username) { this.username = username; return this; }
        public User.UserBuilder password(String password) { this.password = password; return this; }

        public User.UserBuilder hobby(String hobby) {
            if (this.hobbies == null) {
                this.hobbies = new ArrayList();
            }
            this.hobbies.add(hobby);
            return this;
        }

        public User.UserBuilder hobbies(Collection<? extends String> hobbies) {
            if (this.hobbies == null) {
                this.hobbies = new ArrayList();
            }
            this.hobbies.addAll(hobbies);
            return this;
        }

        public User.UserBuilder clearHobbies() {
            if (this.hobbies != null) {
                this.hobbies.clear();
            }
            return this;
        }

        public User build() {
            List hobbies;
            switch(this.hobbies == null ? 0 : this.hobbies.size()) {
            case 0:
                hobbies = Collections.emptyList();
                break;
            case 1:
                hobbies = Collections.singletonList(this.hobbies.get(0));
                break;
            default:
                hobbies = Collections.unmodifiableList(new ArrayList(this.hobbies));
            }
            return new User(this.id, this.username, this.password, hobbies);
        }
        public String toString() {
            return "User.UserBuilder(id=" + this.id + ", username=" + this.username + ", password=" + this.password + ", hobbies=" + this.hobbies + ")";
        }
    }
}
```

## @Builder.Default 的使用

```java
@Builder
@ToString
public class User {
    @Builder.Default
    private final String id = UUID.randomUUID().toString();
    private String username;
    private String password;
    @Builder.Default
    private long insertTime = System.currentTimeMillis();
}
```

在类中我在`id`和`insertTime`上都添加注解`@Builder.Default`，当我在使用这个实体对象时，我就不需要在为这两个字段进行初始化值，如下面这样：

```java
public class BuilderTest {
    public static void main(String[] args) {
        User user = User.builder()
                .password("admin")
                .username("admin")
                .build();
        System.out.println(user);
    }
}

// 输出内容：
User(id=416219e1-bc64-43fd-b2c3-9f8dc109c2e8, username=admin, password=admin, insertTime=1546869309868)
```

当然，你如果再对这两个字段进行设值的话，那么默认定义的值将会被覆盖掉，如下面这样：

```java
public class BuilderTest {
    public static void main(String[] args) {
        User user = User.builder()
                .id("admin")
                .password("admin")
                .username("admin")
                .build();
        System.out.println(user);
    }
}
// 输出内容
User(id=admin, username=admin, password=admin, insertTime=1546869642151)
```





## 缺点

Entity上加上@Builder，会默认为类加上全参构造函数，且提供以建造器模式构造对象的方法。但此时因为显示声明了构造器，默认的无参构造器就失效了，就不能通过new Obj()的方式创建对象。这是自然想到加@NoArgsConstructor注解生成无参构造函数以便使用new Obj()方式创建对象，很多框架中都需要反射调用无参构造函数。但是如果显式声明了@NoArgsConstructor，lombok就不会生成全参构造函数，而@Builder中会用到全参构造函数，所以冲突。

解决方案：

@AllArgsConstructor可解决。
@Builder
