package cn.soboys.simplest.core;

import cn.soboys.simplest.core.utils.StrUtil;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Locale;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/8/3 21:35
 * @webSite https://github.com/coder-amiao
 */
@SpringBootTest
@Slf4j
@SpringBootConfiguration
public class AppTest {

    @Test
    void test() {
        String a = "user_name";
        String user = StrUtil.toCapitalizeCamelCase(a);
        log.info("{}", user);
    }
}
