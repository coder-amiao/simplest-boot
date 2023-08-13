package cn.soboys.simplest.jpa.config;

import cn.soboys.simplest.jpa.plugin.TenantData;
import cn.soboys.simplest.jpa.plugin.TenantFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/30 15:41
 * @webSite https://github.com/coder-amiao
 */

public class BeanAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TenantProperties tenantProperties() {
        return new TenantProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public TenantFactory tenantFactory() {
        return new TenantData();
    }

}
