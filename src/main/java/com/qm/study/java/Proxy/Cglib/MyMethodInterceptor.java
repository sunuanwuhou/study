package com.qm.study.java.Proxy.Cglib;


import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/1/22 16:32
 */
public class MyMethodInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("cglib before");
        Object result = methodProxy.invokeSuper(obj, objects);
        System.out.println("cglib after");
        return result;
    }

    public static void main(String[] args) {

        Enhancer enhancer = new Enhancer();
//设置父类
        enhancer.setSuperclass(Dog.class);
//设置方法拦截处理器
        enhancer.setCallback(new MyMethodInterceptor());
        //创建代理对象
        Dog dog = (Dog) enhancer.create();
        dog.eat();
    }
}
