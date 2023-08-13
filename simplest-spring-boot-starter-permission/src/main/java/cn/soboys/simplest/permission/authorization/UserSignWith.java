package cn.soboys.simplest.permission.authorization;

import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/13 22:01
 * @webSite https://github.com/coder-amiao
 */
public class UserSignWith implements UserSign {

    @Override
    public SignatureAlgorithm sign() {
        return SignatureAlgorithm.HS256;
    }

    //获取密钥,可以动态配置
    @Override
    public String AuthKey() {
        return null;
    }


}
