package com.qm.study.utils;

import org.springframework.util.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * 处理异常工具类
 *
 * @version 1.0
 */
public class ExceptionUtil {

    // 异常堆栈信息长度
    private static final int length = 2000;

    /**
     * 获取异常堆栈信息
     *
     * @param e      异常信息
     * @param length 长度
     * @return 异常堆栈信息
     */
    public static String getExpMsg(Exception e, int length) {
        String msg = null;
        //这个可以修改为自定义异常
        if (e instanceof Exception) {
            msg = e.getMessage();
        } else {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(output));
            msg = output.toString();
        }
        if (!ObjectUtils.isEmpty(msg) && msg.length() > length) {
            msg = msg.substring(0, length);
        }
        return msg;
    }

    public static void main(String[] args) {

        try {
            int b = 1;
            int a = 0;
            System.out.println(b / a);
        } catch (Exception e) {
            System.out.println(getExpMsg(e, 100));
        }
    }

}
