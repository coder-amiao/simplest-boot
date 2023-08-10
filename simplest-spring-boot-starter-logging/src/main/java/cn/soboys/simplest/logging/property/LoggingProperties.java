package cn.soboys.simplest.logging.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author kenx
 * @version 1.0
 * @date 2023/8/10 18:35
 */
@Configuration
@ConfigurationProperties(prefix = "simplest.logging")
@Data
public class LoggingProperties {
    private String path;
    private String maxHistory;
    private String maxFileSize;
    private String maxTotalSizeCap;
    private String levelRoot;
    private String logDataSourceClass = "cn.soboys.restapispringbootstarter.log.LogFileDefaultDataSource";
}
