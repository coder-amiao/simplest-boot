package cn.soboys.simplest.jpa.annotation;


import cn.soboys.simplest.jpa.config.BeanAutoConfiguration;
import cn.soboys.simplest.jpa.config.JpaConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/28 17:55
 * @webSite https://github.com/coder-amiao
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({BeanAutoConfiguration.class, JpaConfig.class})
public @interface EnableJPAQuery {

}
