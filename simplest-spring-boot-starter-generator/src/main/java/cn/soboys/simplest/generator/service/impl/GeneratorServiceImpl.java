package cn.soboys.simplest.generator.service.impl;

import cn.soboys.simplest.generator.GenUtil;
import cn.soboys.simplest.generator.GenerateCodeConfig;
import cn.soboys.simplest.generator.service.IGeneratorService;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/8/7 21:44
 * @webSite https://github.com/coder-amiao
 */
@Service
public class GeneratorServiceImpl implements IGeneratorService {

    @Override
    public void generator(GenerateCodeConfig genCodeConfig) {

        Assert.isTrue(genCodeConfig.getPackages() != null, "代码生成包位置不能为空");
        if (genCodeConfig.getProjectPath() == null) {
            genCodeConfig.setProjectPath(System.getProperty("user.dir"));
        }
        if (StrUtil.isEmpty(genCodeConfig.getAuthor())) {
            genCodeConfig.setAuthor("公众号 程序员三时");
        }
        if (StrUtil.isEmpty(genCodeConfig.getVersion())) {
            genCodeConfig.setAuthor("1.0");
        }
        try {
            for(String className : genCodeConfig.getClassName().split(",")){
                GenUtil.generatorCode(genCodeConfig,className);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
