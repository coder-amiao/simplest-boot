package cn.soboys.simplest.generator.service;

import cn.soboys.simplest.generator.GenerateCodeConfig;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/8/7 21:43
 * @webSite https://github.com/coder-amiao
 */
public interface IGeneratorService {
    /**
     * 生成代码
     * @param genCodeConfig
     */
    void generator(GenerateCodeConfig genCodeConfig);
}
