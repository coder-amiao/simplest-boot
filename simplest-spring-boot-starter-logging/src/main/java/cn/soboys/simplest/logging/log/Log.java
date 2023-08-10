package cn.soboys.simplest.logging.log;


import cn.soboys.simplest.logging.enums.LogApiTypeEnum;
import cn.soboys.simplest.logging.enums.LogCURDTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version: 2.2
 * @className: Log.java
 * @author:  kenx
 * @description: 日志注解
*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
	String value() default "";

	Class[] logExpandHandles() default {LogFileDefaultDataSource.class};

	LogApiTypeEnum apiType() default LogApiTypeEnum.USER;

	LogCURDTypeEnum CURDType() default LogCURDTypeEnum.RETRIEVE;

	/**
	 * 记录ip对应城市
	 * @return
	 */
	boolean ipCity()  default true;

	/**
	 * 是否记录接口整个返回。
	 * @return
	 */
	boolean apiResult() default false;
}
