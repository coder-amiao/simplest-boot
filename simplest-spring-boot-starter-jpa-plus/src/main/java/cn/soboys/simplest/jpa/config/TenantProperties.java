package cn.soboys.simplest.jpa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/30 14:43
 * @webSite https://github.com/coder-amiao
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "tenant")
public class TenantProperties {

    /**
     * 需要进行租户解析的租户表
     */
    private List<String> tables=new ArrayList<>();

    /**
     * 需进行租户解析的租户字段名
     */
    private String tenantIdColumn="tenant_id";


    /**
     * 是否开启租户拦截
     */
    private Boolean enableTenant=Boolean.FALSE;

    /**
     * 自定义忽略某个方法执行租户拦截
     */
    private Boolean customTenant=Boolean.FALSE;
}
