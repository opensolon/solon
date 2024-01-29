package org.noear.solon.core.handle;

import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.lang.Nullable;

/**
 * mvc:动作接口
 *
 * @author noear
 * @since 2.7
 * */
public interface Action extends Handler {
    /**
     * 名字
     */
    String name();

    /**
     * 全名
     */
    String fullName();

    /**
     * 映射
     */
    @Nullable
    Mapping mapping();

    /**
     * 执行方法
     */
    MethodWrap method();

    /**
     * 控制器包装
     */
    BeanWrap controller();

    /**
     * 生产格式
     */
    String produces();

    /**
     * 消费格式
     */
    String consumes();

    /**
     * 调用
     */
    void invoke(Context c, Object obj) throws Throwable;

    /*
     * 渲染
     * */
    void render(Object obj, Context c) throws Throwable;

    /**
     * 添加前置处理
     */
    void before(Handler handler);

    /**
     * 添加后置处理
     */
    void after(Handler handler);
}
