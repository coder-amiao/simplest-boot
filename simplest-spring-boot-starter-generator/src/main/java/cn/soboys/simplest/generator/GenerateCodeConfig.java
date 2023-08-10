package cn.soboys.simplest.generator;

import lombok.Data;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/5 00:05
 * @webSite https://github.com/coder-amiao
 */
@Data
public class GenerateCodeConfig {

    /**
     * 作者
     **/
    private String author;


    private String version="1.0.0";

    /**
     * 生成代码 保存路径。默认当前项目下。
     * 如需修改，使用绝对得路径
     */
    private String projectPath;
    /**
     * 代码生成包位置
     */
    private String packages;

    /**
     * JPA 实体类名
     * (模型驱动数据库。所以不需要实体类生成。而是由实体类生成通用service，repository)
     */
    private String className;

}
