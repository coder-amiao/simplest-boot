package cn.soboys.simplest.ip2region;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author kenx
 * @version 1.0
 * @date 2023/8/10 22:34
 */
@Configuration
@ConfigurationProperties(prefix = "simplest.ip2region")
@Data
public class Ip2regionProperties {
    /**
     * 是否使用外部的IP数据文件.
     */
    private boolean external = false;
    /**
     * ip2region.db 文件路径，默认： classpath:ip2region/ip2region.db
     */
    private String location = "classpath:ip2region/ip2region.xdb";

}
