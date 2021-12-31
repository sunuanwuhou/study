# Table of Contents

* [传统做法-if-else分支](#传统做法-if-else分支)
* [策略模式+MAP字典](#策略模式map字典)
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
