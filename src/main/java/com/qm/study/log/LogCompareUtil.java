package com.qm.study.log;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/10/20 21:43
 */
@Slf4j
public class LogCompareUtil {

    /**
     * @param oldObj 原数据对象
     * @param newObj 修改后数据对象
     */
    public static String addRecord(Object oldObj, Object newObj) {
        try {
            // 得到类对象
            Class<? extends Object> class1 = oldObj.getClass();
            Class<? extends Object> class2 = newObj.getClass();
            if (!class1.equals(class2)) {
                throw new RuntimeException("请传入两个相同的实体类对象");
            }
            // 得到属性集合
            Field[] fields1 = class1.getDeclaredFields();
            StringBuffer info = new StringBuffer();
            for (Field field1 : fields1) {
                LogCompar pn = field1.getAnnotation(LogCompar.class);
                if (pn == null) {
                    continue;
                }
                field1.setAccessible(true);
                Object oldValue = field1.get(oldObj);
                Object newValue = field1.get(newObj);
                if (oldValue.equals(newValue)) {
                    continue;
                }
                //这里可以处理 枚举 以及日期格式 只需要添加注解不同的属性值即可
                Class<? extends BaseEunm> enumClass = pn.enumClass();
                if (!enumClass.equals(BaseEunm.class)) {
                    BaseEunm[] enumConstants = enumClass.getEnumConstants();
                    Map<Integer, BaseEunm> baseEunmMap = Arrays.stream(enumConstants).collect(Collectors.toMap(BaseEunm::getCode, Function.identity()));
                    info.append(pn.name()).append("由:").append(baseEunmMap.get(oldValue).getDesc()).append("改成").append(baseEunmMap.get(newValue).getDesc()).append(";");

                } else {
                    info.append(pn.name()).append("由:").append(oldValue).append("改成").append(newValue).append(";");
                }

            }

            if (info.length() != 0) {
                return info.substring(0, info.length() - 1);
            }
        } catch (RuntimeException e) {
            // log.error(e.getMessage());
        } catch (Exception e) {
            // log.error("属性内容更改前后验证错误,日志无法被记录！");
        }
        return null;
    }
}
