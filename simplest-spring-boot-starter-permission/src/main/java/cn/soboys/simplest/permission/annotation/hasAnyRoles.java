package cn.soboys.simplest.permission.annotation;

import cn.soboys.restapispringbootstarter.utils.Strings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/16 12:21
 * @webSite https://github.com/coder-amiao
 * 验证用户是否具有以下任意一个角色
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface hasAnyRoles {
    String role() default Strings.EMPTY;
}
