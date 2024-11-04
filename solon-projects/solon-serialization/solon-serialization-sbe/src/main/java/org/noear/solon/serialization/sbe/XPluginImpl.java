package org.noear.solon.serialization.sbe;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.serialize.SerializerNames;

/**
 * @author noear
 * @since 3.0
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        //::render
        SbeRender render = new SbeRender();
        context.wrapAndPut(SbeRender.class, render); //用于扩展
        context.app().renderManager().register(SerializerNames.AT_KRYO,render);
        context.app().serializerManager().register(SerializerNames.AT_KRYO, render.getSerializer());

        //::actionExecutor
        //支持 kryo 内容类型执行
        SbeActionExecutor executor = new SbeActionExecutor();
        context.wrapAndPut(SbeActionExecutor.class, executor); //用于扩展

        context.app().chainManager().addExecuteHandler(executor);
    }
}
