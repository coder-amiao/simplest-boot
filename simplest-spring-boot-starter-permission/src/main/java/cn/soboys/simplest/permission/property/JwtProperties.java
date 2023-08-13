package cn.soboys.simplest.permission.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author kenx
 * @version 1.0
 * @date 2023/8/13 11:33
 */
@Configuration
@ConfigurationProperties(prefix = "simplest.jwt")
@Data
public class JwtProperties {

    /**
     * 过期时间秒1天后过期=86400  (单位秒)
     */
    private Long expiration = 86400l;

    /**
     * 记住我过期时间 7天后过期=604800（单位秒）
     */
    private Long rememberMeExpiration = 604800l;

    /**
     * 配置用户自定义签名
     */
    private Boolean userSign = Boolean.FALSE;
    /**
     * Header Key
     */
    private String tokenHeader = "Token";

    /**
     * # 密匙KEY
     */
    private String secret = "2af57b969bac152d";


    private Authorization authorization = new Authorization();


    @Data
    public static class Authorization {

        private Boolean hasAuthorization = Boolean.FALSE;

        /**
         * 需要认证的url
         */
        private String includesUrl;

        /**
         * 不需要认证的url
         */
        private String excludesUrl;
    }
}
