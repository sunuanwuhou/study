package com.qm.study.DesignPatterns.creator.factoryMethod;

import cn.hutool.core.util.StrUtil;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2021/10/27 19:40
 */
public class test {
    private static Dialog dialog;

    public static void main(String[] args) {
        configure("2");
        runBusinessLogic();
    }
    static void configure(String str) {
        if (StrUtil.equals("1",str)) {
            dialog = new WindowsDialog();
        } else {
            dialog = new HtmlDialog();
        }
    }

    static void runBusinessLogic() {
        dialog.renderWindow();
    }

}
