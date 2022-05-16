package org.noear.solon.plugind;

import org.noear.solon.core.Aop;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;

/**
 * 外接小程序（由独立 ClassLoader 加载的，可动态管理的 Plugin）
 *
 * @author noear
 * @since 1.7
 */
public abstract class PluginPlus implements Plugin {
    private AopContext context;
    private Props props;

    public PluginPlus(){
        this(null);
    }
    public PluginPlus(Props props){
        this.props = props;
    }

    /**
     * 上下文
     * */
    public AopContext context() {
        if(context == null){
            context = Aop.context().copy(props);
        }
        return context;
    }
}
