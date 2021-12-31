package com.qm.study.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
 
/**
 * https://blog.csdn.net/master336/article/details/104356565
 * @Aspect 表明该类为切面类
 * @Component 声明为Spring组件，由Spring容器统一管理
 * @author master336 2020-02-17
 * 
 */
@Aspect
@Component
public class LogAspect {
    /**
     * 会话ID
     * GUID ： mdc的key
     * REQUEST_GUID ： http/https协议内的guid key
     */
    private static final String KEY = "TRACE_ID";

    private static final String REQUEST_KEY = "REQUEST_TRACE_ID";
    /**
     * GUID ThreadLocal 存储当前线程的guid
     */
    public static final ThreadLocal<String> threadLocal = new NamedThreadLocal<String>("tracehreadLocal");
    /**
     * 通过自动装配 拿到request及response
     * 切点定义的不同，可能导致自动装配失败，这里定义required=false，忽略无法装配的情况
     */
    @Autowired(required=false)
    HttpServletRequest request;
    @Autowired(required=false)
    HttpServletResponse response;
 
    public static void init(){
        String guid = UUID.randomUUID().toString().replace("-", "");
        threadLocal.set(guid);
        MDC.put(KEY, guid);
    }
 
    /*** * 切入规则，拦截*Controller.java下所有方法 */
    @Pointcut("execution(* com..*Controller.*(..))")
    public void beanAspect(){
 
    }
    /**
     * 前置通知 记录开始时间
     * @param joinPoint 切点
     * @throws InterruptedException
     */
    @Before("beanAspect()")
    public void doBefore(JoinPoint joinPoint) throws InterruptedException{
        dealGuid();
    }
    /**
     * 后置通知 返回通知
     * @param res 响应内容
     */
    @AfterReturning(returning = "res", pointcut = "beanAspect()")
    public void doAfterReturning(Object res) throws Throwable {
        // 处理完请求，返回内容
        dealGuid();
    }
    /**
     * 后置通知 记录用户的操作
     * @param joinPoint 切点
     */
    @After("beanAspect()")
    public void doAfter(JoinPoint joinPoint) {
        dealGuid();
    }
 
    /**
     *  异常通知 即使出现错误，也不要丢了guid信息
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "beanAspect()", throwing = "e")
    public  void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        dealGuid();
    }


    public void dealGuid(){
        // 依次从 request threadlocal 里取
        String guid = threadLocal.get();
        if(StringUtils.isEmpty(guid) && request != null) {
            try {
                guid = request.getHeader(REQUEST_KEY);
            }catch (Throwable e){
                // response 不可用 直接忽略
            }
        }
        // 无法读取有效的guid重新生成
        if(StringUtils.isEmpty(guid)){
            guid = UUID.randomUUID().toString().replace("-", "");
        }
        // 设置SessionId
        threadLocal.set(guid);
        if(response != null) {
            try {
                response.setHeader(REQUEST_KEY, guid);
            }catch (Throwable e){
                // response 不可用 直接忽略
            }
        }
        MDC.put(KEY, guid);
    }
}