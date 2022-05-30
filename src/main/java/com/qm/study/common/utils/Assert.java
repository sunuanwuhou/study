package com.qm.study.common.utils;

import com.qm.study.common.CodeEnum.CodeEnum;
import com.qm.study.common.exception.BizException;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/5/30 21:51
 */
public class Assert {

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new BizException(CodeEnum.ERROR.getCode(),message);
        }
    }

}
