package cn.zhxu.bs.solon;

import cn.zhxu.bs.SearchException;

/**
 * 非法配置异常
 */
public class IllegalConfigException extends SearchException {

    public IllegalConfigException(String message) {
        super(message);
    }

    public IllegalConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
