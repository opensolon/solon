package org.noear.solon.core.handle;

import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.wrap.MethodWrap;

/**
 * mvc:动作接口
 *
 * @author noear
 * @since 2.7
 * */
public interface Action extends Handler{
    String name();
    String fullName();
    Mapping mapping();
    MethodWrap method();
    BeanWrap controller();
    String produces();
    String consumes();

    void invoke(Context c, Object obj) throws Throwable;
    void render(Object obj, Context c) throws Throwable;

    void before(Handler handler);
    void after(Handler handler);
}
