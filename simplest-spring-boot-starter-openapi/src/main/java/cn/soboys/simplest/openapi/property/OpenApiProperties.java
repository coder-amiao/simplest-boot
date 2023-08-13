package cn.soboys.simplest.openapi.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kenx
 * @version 1.0
 * @date 2023/8/13 12:12
 */
@Configuration
@ConfigurationProperties(prefix = "simplest.openapi")
@Data
public class OpenApiProperties {
    /**
     * 是否开启swagger
     */
    private Boolean enabled = true;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 文档版本，默认使用 2.0
     */
    private String documentationType = "v2.0";

    /**
     * swagger会解析的包路径
     **/
    private String basePackage = "";

    /**
     * swagger会解析的url规则
     **/
    private List<String> basePath = new ArrayList<>();

    /**
     * 在basePath基础上需要排除的url规则
     **/
    private List<String> excludePath = new ArrayList<>();

    /**
     * 标题
     **/
    private String title = "REST FULL";

    /**
     * 描述
     **/
    private String description = "SpringBoot Web Easy RestFull API";

    /**
     * 版本
     **/
    private String version = "1.5.0";

    /**
     * 许可证
     **/
    private String license = "";

    /**
     * 许可证URL
     **/
    private String licenseUrl = "";

    /**
     * 服务条款URL
     **/
    private String termsOfServiceUrl = "";

    /**
     * host信息
     **/
    private String host = "https://rest-api-boot.soboys.cn/doc-rest-api-springboot-starter/";

    /**
     * 联系人信息
     */
    private Contact contact = new Contact();


    @Data
    public static class Contact {

        /**
         * 联系人
         **/
        private String name = "公众号程序员三时";

        /**
         * 联系人url
         **/
        private String url = "https://github.com/coder-amiao/rest-api-spring-boot-starter";

        /**
         * 联系人email
         **/
        private String email = "xymarcus@163.com";

    }
}
