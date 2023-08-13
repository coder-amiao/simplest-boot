package cn.soboys.simplest.jpa.plugin;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/30 22:45
 * @webSite https://github.com/coder-amiao
 */
public abstract class CustomTenant implements TenantFactory {

    /**
     * 用户自定义某个sql不拦截租户 默认不自定走全部拦截
     */
    public static Boolean withoutTenantCondition= Boolean.FALSE;
}
