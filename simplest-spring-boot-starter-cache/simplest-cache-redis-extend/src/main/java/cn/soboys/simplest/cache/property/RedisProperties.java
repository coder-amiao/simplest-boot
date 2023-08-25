package cn.soboys.simplest.cache.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author kenx
 * @version 1.0
 * @date 2023/8/9 23:32
 */

@Configuration
@ConfigurationProperties(prefix = "simplest.redis")
@Data
public class RedisProperties {
    /**
     * 全局注册key
     */
    private String keyPrefix;
    /**
     * redis 缓存的默认超时时间(s) 1天超时
     */
    private Long expireTime;
}