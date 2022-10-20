package com.qm.study.log;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/10/20 22:29
 */
@Setter
@Getter
public class LogCompareTest {

    @LogCompar(name = "1")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        LogCompareTest logCompareTest = new LogCompareTest();
        logCompareTest.setName("1");

        LogCompareTest logCompareTest2 = new LogCompareTest();
        logCompareTest2.setName("2");

        System.out.println(LogCompareUtil.addRecord(logCompareTest,logCompareTest2));;

    }
}
