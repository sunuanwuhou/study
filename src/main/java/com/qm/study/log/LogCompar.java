package com.qm.study.log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
注解
*/
@Retention(RetentionPolicy.RUNTIME)
public @interface LogCompar {
	  /**
	   * 汉字全称
	   * @return
	   */
	  String name();
	  
	  /**
	   * Date 如何格式化，默认可以为空 看业务需求
	   * @return
	   */
	  String dateFormat() default "";


  	/**
	   * 枚举赋值  解决比较过程中 枚举code和desc不对应的情况 业务枚举需要实现BaseEnum
	   * @return
	   */
    Class<? extends BaseEunm> enumClass() default BaseEunm.class;

}

