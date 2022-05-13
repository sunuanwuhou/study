package com.qm.study.java.Thread.CompletableFuture;

import com.qm.study.java.Tuply.TwoTuple;

import java.util.concurrent.CompletableFuture;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/5/8 20:33
 */
public class demo1 {


    public static void main(String[] args) {
        System.out.println("主进程开始");
        sendMsg();
        System.out.println("主进程结束");
    }


    public static void sendMsg() {
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("发送消息");
            //发送http信息
            Request request = new Request();
            Response response = send(request);
            return new TwoTuple<>(request, response);//注意这里 泛型
        }).whenComplete((twoTuple, throwable) -> {
            System.out.println("记录日志");
            if (null != throwable) {
                //记录错误日志信息
            }
            insertLog(twoTuple.first,twoTuple.second);//注意这里 泛型
        });
    }


    public static Response send(Request request) {
        Response response = new Response();
        response.setErrorMsg("111");
        return response;
    }


    public static void insertLog(Request request, Response response) {
        System.out.println(request + "-" + response);
    }

    public static class Request {

    }

    public static class Response {

        private String errorMsg;

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }


}
