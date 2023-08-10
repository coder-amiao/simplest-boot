package cn.soboys.simplest.json.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kenx
 * @version 1.0
 * @date 2023/8/10 16:20
 */
@Configuration
@ConfigurationProperties(prefix = "simplest.json")
@Data
public  class JsonSerializeProperties {
    /**
     * 序列化类型
     */
    private List<String> serializableType = new ArrayList<>();
    /**
     * 时间类型序列化返回 默认时间戳格式
     * yyyy-MM-dd HH:mm:ss.SSS
     */
    private String dateForm = "timestamp";

    /**
     * 浮点数序列化 BigDecimal 保留完整精度 科学计数法
     * 四舍五入
     */
    private String numberForm = ",###.##";


    /**
     * 对空 返回处理
     */
    private NullAble nullAble = new NullAble();


    /**
     * JSON 序列化对空返回处理
     * 空集合 返回[],Double 返回 0.00 Number 返回0 字符串返回""
     */
    @Data
    public static class NullAble{
        /**
         * 是否开启对空值处理
         */
        private Boolean hasNullAble = Boolean.FALSE;

        /**
         * 当 int和long 类型为空默认处理返回0
         *original 不处理| number 0|string ""|
         */
        private String  NumberType="number";

        /**
         * 当 集合类型 空处理默认返会 []
         * original 不处理 |array []
         */
        private String ArrayType="array";

        /**
         * 浮点类型 空处理 默认返回 0.00
         * original 不处理 |double 0.00
         */
        private String DoubleType="double";

        /**
         * 对象 空处理 包括null空字符
         * original 不处理 |string ""
         */
        private String ObjectType="string";



    }
}
