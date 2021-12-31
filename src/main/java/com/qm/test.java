package com.qm;

import java.util.Random;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/12/19 22:00
 */
public class test {



    public static void main(String[] args) {
        printName("邱", 2, 800);
    }

    /**
     * 打印名字
     * @param lastName 姓
     * @param digits 名字长度
     * @param count 生成数量
     */
    public static void printName(String lastName, int digits, int count) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i % 10 == 0) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(lastName);
            stringBuilder.append(getRandom(digits));
            stringBuilder.append(" ");
        }
        System.out.println(stringBuilder.toString());
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public static String getRandom(int len) {
        String ret = "";
        for (int i = 0; i < len; i++) {
            String str = null;
            int hightPos, lowPos;
            Random random = new Random();
            hightPos = (176 + Math.abs(random.nextInt(39)));
            lowPos = (161 + Math.abs(random.nextInt(93)));
            byte[] b = new byte[2];
            b[0] = (new Integer(hightPos).byteValue());
            b[1] = (new Integer(lowPos).byteValue());
            try {
                str = new String(b, "GBK");
            } catch (Exception e) {
                System.out.println(e);
            }
            ret += str;
        }
        return ret;
    }

}
