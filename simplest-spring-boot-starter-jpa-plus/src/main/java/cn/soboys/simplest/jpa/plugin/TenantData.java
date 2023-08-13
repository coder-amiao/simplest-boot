package cn.soboys.simplest.jpa.plugin;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/30 15:35
 * @webSite https://github.com/coder-amiao
 */
public class TenantData implements TenantFactory {

    @Override
    public String getTenantId() {
        return "";
    }
}
