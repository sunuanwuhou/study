# Table of Contents

* [传统做法-if-else分支](#传统做法-if-else分支)
* [策略模式+MAP字典](#策略模式map字典)
* [Map+lambda（一般的推荐使用这个）](#maplambda一般的推荐使用这个)
* [责任链](#责任链)
* [策略模式和责任链区别](#策略模式和责任链区别)
* [参考资料](#参考资料)




#  传统做法-if-else分支

```java
for (Receipt receipt : receiptList) {
    if (StringUtils.equals("MT2101",receipt.getType())) {
        System.out.println("接收到MT2101回执");
        System.out.println("解析回执内容");
        System.out.println("执行业务逻辑");
    } else if (StringUtils.equals("MT1101",receipt.getType())) {
        System.out.println("接收到MT1101回执");
        System.out.println("解析回执内容");
        System.out.println("执行业务逻辑");
    } else if (StringUtils.equals("MT8104",receipt.getType())) {
        System.out.println("接收到MT8104回执");
        System.out.println("解析回执内容");
        System.out.println("执行业务逻辑");
    } else if (StringUtils.equals("MT9999",receipt.getType())) {
        System.out.println("接收到MT9999回执");
        System.out.println("解析回执内容");
        System.out.println("执行业务逻辑");
        System.out.println("推送邮件");
    }
    // ......未来可能还有好多个else if
}
```



# 策略模式+MAP字典



我们知道， 策略模式的目的是封装一系列的算法，它们具有共性，可以相互替换，也就是说让算法独立于使用它的客户端而独立变化，客户端仅仅依赖于策略接口 。

在上述场景中，我们可以把if-else分支的业务逻辑抽取为各种策略，**但是不可避免的是依然需要客户端写一些if-else进行策略选择的逻辑，我们可以将这段逻辑抽取到工厂类中去，这就是策略模式+简单工厂**，代码如下

+ 策略接口

  ```java
  public interface IReceiptHandleStrategy {
      void handleReceipt(Receipt receipt);
  }
  ```

+ 策略接口实现类：其实就是原来逻辑中具体实现ifelse的。

  ```java
  public class Mt2101ReceiptHandleStrategy implements IReceiptHandleStrategy {
  
      @Override
      public void handleReceipt(Receipt receipt) {
          System.out.println("解析报文MT2101:" + receipt.getMessage());
      }
  
  }
  
  public class Mt1101ReceiptHandleStrategy implements IReceiptHandleStrategy {
  
      @Override
      public void handleReceipt(Receipt receipt) {
          System.out.println("解析报文MT1101:" + receipt.getMessage());
      }
  
  }
  --- 
  ```
  
 + 策略上下文(策略接口的持有者)

   ```java
   public class ReceiptStrategyContext {
   
       private IReceiptHandleStrategy receiptHandleStrategy;
   
       /**
        * 设置策略接口
        * @param receiptHandleStrategy
        */
       public void setReceiptHandleStrategy(IReceiptHandleStrategy receiptHandleStrategy) {
           this.receiptHandleStrategy = receiptHandleStrategy;
       }
   
       public void handleReceipt(Receipt receipt){
           if (receiptHandleStrategy != null) {
                receiptHandleStrategy.handleReceipt(receipt);
           }
       }
   }
   ```

+ 策略工厂

  ```java
  public class ReceiptHandleStrategyFactory {
  
      private static Map<String,IReceiptHandleStrategy> receiptHandleStrategyMap;
  
      private ReceiptHandleStrategyFactory(){
          this.receiptHandleStrategyMap = new HashMap<>();
          this.receiptHandleStrategyMap.put("MT2101",new Mt2101ReceiptHandleStrategy());
          this.receiptHandleStrategyMap.put("MT8104",new Mt8104ReceiptHandleStrategy());
      }
  
      public static IReceiptHandleStrategy getReceiptHandleStrategy(String receiptType){
          return receiptHandleStrategyMap.get(receiptType);
      }
  }
  ```

+ 使用代码

  ```java
    //模拟回执
          List<Receipt> receiptList = ReceiptBuilder.generateReceiptList();
          //策略上下文
          ReceiptStrategyContext receiptStrategyContext = new ReceiptStrategyContext();
          for (Receipt receipt : receiptList) {
              //获取并设置策略
              IReceiptHandleStrategy receiptHandleStrategy = ReceiptHandleStrategyFactory.getReceiptHandleStrategy(receipt.getType());
              receiptStrategyContext.setReceiptHandleStrategy(receiptHandleStrategy);
              //执行策略
              receiptStrategyContext.handleReceipt(receipt);
          }
      }
  ```

  

+ 流程图

![](.images/未命名文件.png)



# Map+lambda（一般的推荐使用这个）

> 参考资料：https://www.cnblogs.com/hollischuang/p/13186766.html

策略模式带来的缺点：

1、策略类会增多

2、业务逻辑分散到各个实现类中，而且没有一个地方可以俯视整个业务逻辑

针对传统策略模式的缺点，在这分享一个实现思路，这个思路已经帮我们团队解决了多个复杂if else的业务场景，理解上比较容易，代码上需要用到Java8的特性——利用Map与函数式接口来实现。


```java
package com.qm.study.DesignPatterns.behavior.strategy;

import org.springframework.stereotype.Service;

/**
 * 提供业务逻辑单元
 */
@Service
public class BizUnitService {

    public String bizOne(String order) {
        return order + "各种花式操作1";
    }
    public String bizTwo(String order) {
        return order + "各种花式操作2";
    }
    public String bizThree(String order) {
        return order + "各种花式操作3";
    }
}
```



```java
package com.qm.study.DesignPatterns.behavior.strategy.maplambda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 某个业务服务类
 */
@Service
public class BizService {
    @Autowired
    private BizUnitService bizUnitService;

    /**
     * 传统的 if else 解决方法
     * 当每个业务逻辑有 3 4 行时，用传统的策略模式不值得，直接的if else又显得不易读
     */
    public  String getCheckResult(String order) {
        if ("校验1".equals(order)) {
            return "执行业务逻辑1";
        } else if ("校验2".equals(order)) {
            return "执行业务逻辑2";
        }else if ("校验3".equals(order)) {
            return "执行业务逻辑3";
        }else if ("校验4".equals(order)) {
            return "执行业务逻辑4";
        }else if ("校验5".equals(order)) {
            return "执行业务逻辑5";
        }else if ("校验6".equals(order)) {
            return "执行业务逻辑6";
        }else if ("校验7".equals(order)) {
            return "执行业务逻辑7";
        }else if ("校验8".equals(order)) {
            return "执行业务逻辑8";
        }else if ("校验9".equals(order)) {
            return "执行业务逻辑9";
        }
        return "不在处理的逻辑中返回业务错误";
    }

    /**
     * 业务逻辑分派Map
     * Function为函数式接口，下面代码中 Function<String, String> 的含义是接收一个Stirng类型的变量，返回一个String类型的结果
     */
    private   Map<String, Function<String, String>> checkResultDispatcher = new HashMap<>();

    /**
     * 初始化 业务逻辑分派Map 其中value 存放的是 lambda表达式
     */
    @PostConstruct
    public  void checkResultDispatcherInit() {
        checkResultDispatcher.put("校验1", order -> bizUnitService.bizOne(order));
        checkResultDispatcher.put("校验2", order -> String.format("对%s执行业务逻辑2", order));
        checkResultDispatcher.put("校验3", order -> String.format("对%s执行业务逻辑3", order));
        checkResultDispatcher.put("校验4", order -> String.format("对%s执行业务逻辑4", order));
        checkResultDispatcher.put("校验5", order -> String.format("对%s执行业务逻辑5", order));
        checkResultDispatcher.put("校验6", order -> String.format("对%s执行业务逻辑6", order));
        checkResultDispatcher.put("校验7", order -> String.format("对%s执行业务逻辑7", order));
        checkResultDispatcher.put("校验8", order -> String.format("对%s执行业务逻辑8", order));
        checkResultDispatcher.put("校验9", order -> String.format("对%s执行业务逻辑9", order));
    }

    public  String getCheckResultSuper(String order) {
        //从逻辑分派Dispatcher中获得业务逻辑代码，result变量是一段lambda表达式
        Function<String, String> result = checkResultDispatcher.get(order);
        if (result != null) {
            //执行这段表达式获得String类型的结果
            return result.apply(order);
        }
        return "不在处理的逻辑中返回业务错误";
    }

    public static void main(String[] args) {
        BizService bizService = new BizService();
        System.out.println(bizService.getCheckResult("校验1"));
        bizService.checkResultDispatcherInit();
        System.out.println(bizService.getCheckResultSuper("校验1"));

    }
}

```







# 责任链

责任链模式是一种对象的行为模式。在责任链模式里，很多对象由每一个对象对其下家的引用而连接起来形成一条链。请求在这个链上传递，直到链上的某一个对象决定处理此请求。

发出这个请求的客户端并不知道链上的哪一个对象最终处理这个请求，这使得系统可以在不影响客户端的情况下动态地重新组织和分配责任



+ 处理者接口

  ```java
  public interface IReceiptHandler {
      //注意这里 是要透传 责任链接口  这里是可以优化下 将 receipt 和 handleChain 封装为一个 ProcessContext 上下文对象
      void handleReceipt(Receipt receipt,IReceiptHandleChain handleChain);
  }
  ```
  
+ 责任链接口:

  ```java
  public interface IReceiptHandleChain {
  
      void handleReceipt(Receipt receipt);
  }
  ```

+ 责任链具体实现

  > 这里是跟策略模式不一样的，注册所有的具体实现，然后具体实现判断是否需要拦截，是拦截 否则 下一个拦截连

  ```java
  public class ReceiptHandleChain implements IReceiptHandleChain {
      //记录当前处理者位置
      private int index = 0;
      //处理者集合
      private static List<IReceiptHandler> receiptHandlerList;
  
      static {
          //从容器中获取处理器对象
          receiptHandlerList = ReceiptHandlerContainer.getReceiptHandlerList();
      }
  
      @Override
      public void handleReceipt(Receipt receipt) {
          if (receiptHandlerList !=null && receiptHandlerList.size() > 0) {
              if (index != receiptHandlerList.size()) {
                  IReceiptHandler receiptHandler = receiptHandlerList.get(index++);
                  receiptHandler.handleReceipt(receipt,this);
              }
          }
      }
  }
  ```

  

+ 处理者具体实现

  ```java
  public class Mt2101ReceiptHandler implements IReceiptHandler {
  
      @Override
      public void handleReceipt(Receipt receipt, IReceiptHandleChain handleChain) {
          if (StringUtils.equals("MT2101",receipt.getType())) {
              System.out.println("解析报文MT2101:" + receipt.getMessage());
          }
          else {
                //处理不了该回执就往下传递
              handleChain.handleReceipt(receipt);
          }
      }
  }
  
  public class Mt8104ReceiptHandler implements IReceiptHandler {
  
      @Override
      public void handleReceipt(Receipt receipt, IReceiptHandleChain handleChain) {
          if (StringUtils.equals("MT8104",receipt.getType())) {
              System.out.println("解析报文MT8104:" + receipt.getMessage());
          }
          else {
                //处理不了该回执就往下传递
              handleChain.handleReceipt(receipt);
          }
      }
  }
  ```

  

+ 责任链处理者容器(如果采用spring,则可以通过依赖注入的方式获取到IReceiptHandler的子类对象)

```java
public class ReceiptHandlerContainer {

    private ReceiptHandlerContainer(){}

    public static List<IReceiptHandler> getReceiptHandlerList(){
        List<IReceiptHandler> receiptHandlerList = new ArrayList<>();
        receiptHandlerList.add(new Mt2101ReceiptHandler());
        receiptHandlerList.add(new Mt8104ReceiptHandler());
        return receiptHandlerList;
    }

}
```

+ 客户端

```java
public class Client {

    public static void main(String[] args) {
        //模拟回执
        List<Receipt> receiptList = ReceiptBuilder.generateReceiptList();
        for (Receipt receipt : receiptList) {
            //回执处理链对象
            ReceiptHandleChain receiptHandleChain = new ReceiptHandleChain();
            receiptHandleChain.handleReceipt(receipt);
        }
    }
}
```









# 策略模式和责任链区别

个人理解：

+ 策略模式：是准确找到对应策略，然后执行
+ 责任链：一层一层透传





# 参考资料

https://mp.weixin.qq.com/s/jp6pycU0mhFVppAsr0ftQg
