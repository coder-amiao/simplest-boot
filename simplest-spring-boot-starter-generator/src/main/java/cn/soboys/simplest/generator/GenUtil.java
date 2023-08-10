package cn.soboys.simplest.generator;


import org.dromara.hutool.core.io.file.FileUtil;

import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.extra.template.Template;
import org.dromara.hutool.extra.template.TemplateConfig;
import org.dromara.hutool.extra.template.TemplateException;
import org.dromara.hutool.extra.template.TemplateUtil;
import org.dromara.hutool.extra.template.engine.TemplateEngine;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/8/7 22:23
 * @webSite https://github.com/coder-amiao
 */
public class GenUtil {

    public static void generatorCode(GenerateCodeConfig genCodeConfig, String className) {
        /**
         * 代码生成配置
         */
        Map<String, Object> map = new HashMap();
        map.put("package", genCodeConfig.getPackages());
        map.put("author", genCodeConfig.getAuthor());
        map.put("date", LocalDate.now().toString());
        map.put("version", genCodeConfig.getVersion());
        map.put("className", className);
        TemplateEngine engine = TemplateUtil.getEngine().init(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));


        // 生成后端代码
        List<String> templates = getBackTemplateNames();
        for (String templateName : templates) {
            Template template = engine.getTemplate("generator/back/" + templateName + ".ftl");
            String filePath = getBackFilePath(templateName, genCodeConfig, className);

            if (StrUtil.isBlank(filePath)) {
                return;
            }
            File file = new File(filePath);

            // 生成代码
            genFile(file, template, map);
        }

    }


    /**
     * 获取后端代码模板名称
     *
     * @return
     */
    public static List<String> getBackTemplateNames() {
        List<String> templateNames = new ArrayList<>();
        templateNames.add("Repository");
        templateNames.add("Service");
        templateNames.add("ServiceImpl");
        return templateNames;
    }


    /**
     * 定义后端文件路径以及名称
     */
    public static String getBackFilePath(String templateName, GenerateCodeConfig genCodeConfig, String className) {
        String projectPath = genCodeConfig.getProjectPath();
        String packagePath = projectPath + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
        if (!ObjectUtils.isEmpty(genCodeConfig.getPackages())) {
            packagePath += genCodeConfig.getPackages().replace(".", File.separator) + File.separator;
        }


        if ("Service".equals(templateName)) {
            return packagePath + "service" + File.separator + "I" + className + "Service.java";
        }

        if ("ServiceImpl".equals(templateName)) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }


        if ("Repository".equals(templateName)) {
            return packagePath + "repository" + File.separator + className + "Repository.java";
        }

        return null;
    }


    public static void genFile(File file, Template template, Map<String, Object> map) {
        // 生成目标文件
        Writer writer = null;
        try {
            FileUtil.touch(file);
            writer = new FileWriter(file);
            template.render(map, writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
