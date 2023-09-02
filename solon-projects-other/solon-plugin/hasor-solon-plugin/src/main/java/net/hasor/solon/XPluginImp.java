package net.hasor.solon;

import net.hasor.core.DimModule;
import net.hasor.core.Module;
import net.hasor.solon.boot.BuildConfig;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * Solon 插件（获取 start 入口）
 *
 * @author noear
 * @since 2020.10.10
 * */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        //
        //注册bean构建器
        //
        context.beanBuilderAdd(DimModule.class, (type, bw, anno) -> {
            // 把Solon 中所有标记了 @DimModule 的 Module，捞进来。
            //
            if (Module.class.isAssignableFrom(type)) {
                BuildConfig.getInstance().addModules(bw.raw());
            }
        });
    }
}
