package org.apache.ibatis.solon.integration;

import org.apache.ibatis.solon.annotation.Db;
import org.apache.ibatis.solon.aot.MybatisRuntimeNativeRegistrar;
import org.noear.solon.Utils;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.core.*;
import org.apache.ibatis.solon.MybatisAdapter;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ClassUtil;

import javax.sql.DataSource;

public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) {

        context.subWrapsOfType(DataSource.class, bw -> {
            MybatisAdapterManager.register(bw);
        });

        //for new
        context.beanBuilderAdd(Db.class, (clz, wrap, anno) -> {
            builderAddDo(clz, wrap, anno.value());
        });

        context.beanInjectorAdd(Db.class, (varH, anno) -> {
            injectorAddDo(varH, anno.value());
        });

        // aot
        if (NativeDetector.isAotRuntime() && ClassUtil.hasClass(() -> RuntimeNativeRegistrar.class)) {
            context.wrapAndPut(MybatisRuntimeNativeRegistrar.class);
        }

    }

    private void builderAddDo(Class<?> clz, BeanWrap wrap, String annoValue) {
        if (clz.isInterface() == false) {
            return;
        }

        if (Utils.isEmpty(annoValue)) {
            wrap.context().getWrapAsync(DataSource.class, (dsBw) -> {
                create0(clz, dsBw);
            });
        } else {
            wrap.context().getWrapAsync(annoValue, (dsBw) -> {
                if (dsBw.raw() instanceof DataSource) {
                    create0(clz, dsBw);
                }
            });
        }
    }

    private void injectorAddDo(VarHolder varH, String annoValue) {
        if (Utils.isEmpty(annoValue)) {
            varH.context().getWrapAsync(DataSource.class, (dsBw) -> {
                inject0(varH, dsBw);
            });
        } else {
            varH.context().getWrapAsync(annoValue, (dsBw) -> {
                if (dsBw.raw() instanceof DataSource) {
                    inject0(varH, dsBw);
                }
            });
        }
    }


    private void create0(Class<?> clz, BeanWrap dsBw) {
        Object raw = MybatisAdapterManager.get(dsBw).getMapper(clz);
        dsBw.context().wrapAndPut(clz, raw);
    }

    private void inject0(VarHolder varH, BeanWrap dsBw) {
        MybatisAdapter adapter = MybatisAdapterManager.get(dsBw);

        if (adapter != null) {
            adapter.injectTo(varH);
        }
    }
}
