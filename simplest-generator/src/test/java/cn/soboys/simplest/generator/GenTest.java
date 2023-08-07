package cn.soboys.simplest.generator;

import cn.soboys.simplest.generator.GenerateCodeConfig;
import cn.soboys.simplest.generator.service.IGeneratorService;
import cn.soboys.simplest.generator.service.impl.GeneratorServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/8/7 22:52
 * @webSite https://github.com/coder-amiao
 */
@SpringBootTest
@Slf4j
@SpringBootConfiguration
public class GenTest {



    @Test
    void  gen(){
        IGeneratorService generatorService=new GeneratorServiceImpl();
        GenerateCodeConfig generateCodeConfig=new GenerateCodeConfig();
         generateCodeConfig.setClassName("Category");
         generateCodeConfig.setPackages("cn.soboys.simplestjpa");
         generateCodeConfig.setProjectPath("/Users/xiangyong/selfProject/project/simplest-jpa");
        generatorService.generator(generateCodeConfig);
    }


    @Test
    void g(){
        System.out.println(1);
    }
}
