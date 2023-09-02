package org.noear.solon.core;

/**
 * 应用上下文（ 为全局对象；热插拨的插件，会产生独立的上下文）
 *
 * 主要实现四个动作：
 * 1.bean 构建
 * 2.bean 注入（字段 或 参数）
 * 3.bean 提取
 * 4.bean 拦截
 *
 * @author noear
 * @since 2.5
 * */
public class AppContext extends AopContext{ //（继承，为兼容性过度）
    public AppContext() {
        super();
    }

    public AppContext(ClassLoader classLoader, Props props) {
        super(classLoader, props);
    }
}
