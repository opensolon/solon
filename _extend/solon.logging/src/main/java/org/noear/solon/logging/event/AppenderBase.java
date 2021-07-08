package org.noear.solon.logging.event;

/**
 * 日志添加器基类
 *
 * @author noear
 * @since 1.3
 */
public abstract class AppenderBase implements Appender{
    /**
     * 名称
     * */
    private String name;

    /**
     * 获取名称
     * */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     * */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 开始生命周期（一般由 AppenderHolder 控制 ）
     *
     * @see org.noear.solon.logging.AppenderHolder
     * */
    @Override
    public void start() {

    }
}
