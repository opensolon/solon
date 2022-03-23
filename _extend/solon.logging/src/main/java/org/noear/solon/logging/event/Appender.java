package org.noear.solon.logging.event;


/**
 * 日志添加器
 *
 * @author noear
 * @since 1.0
 */
public interface Appender{

    /**
     * 开始生命周期（一般由 AppenderHolder 控制 ）
     *
     * @see org.noear.solon.logging.AppenderHolder
     * */
    default void start(){}

    /**
     * 停止生命周期
     * */
    default void stop(){}

    /**
     * 获取名称
     * */
    String getName();
    /**
     * 设置名称
     *
     * @param name 名称
     * */
    void setName(String name);

    /**
     * 添加日志事件
     *
     * @param logEvent 日志事件
     * */
    void append(LogEvent logEvent);
}
