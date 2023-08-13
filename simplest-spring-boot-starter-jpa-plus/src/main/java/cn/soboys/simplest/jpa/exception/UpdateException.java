package cn.soboys.simplest.jpa.exception;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/28 11:37
 * @webSite https://github.com/coder-amiao
 */
public class UpdateException extends RuntimeException {

    public UpdateException(String message) {
        super(message);
    }
}
