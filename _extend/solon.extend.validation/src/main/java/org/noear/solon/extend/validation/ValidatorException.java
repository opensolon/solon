package org.noear.solon.extend.validation;

/**
 * 验证器异常
 *
 * @author noear
 * @since 1.4
 */
public class ValidatorException extends RuntimeException{
    public ValidatorException(String message){
        super(message);
    }
}
