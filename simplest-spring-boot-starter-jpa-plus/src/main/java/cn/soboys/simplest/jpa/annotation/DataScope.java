package cn.soboys.simplest.jpa.annotation;

import org.apache.logging.log4j.util.Strings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/30 22:24
 * @webSite https://github.com/coder-amiao
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {
    /**
     * 租户隔离
     * @return
     */
    boolean hasTenant() default true;

    /**
     * 数据权限
     * @return
     */
    String scope() default Strings.EMPTY;
}

