package org.noear.solon.extend.feign;

import feign.Feign;
import feign.Request;
import feign.Retryer;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.*;


public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        //检查是否启用了@FeignClient
        if (app.source().getAnnotation(EnableFeignClients.class) == null) {
            return;
        }

        Aop.factory().beanCreatorAdd(FeignClient.class, (clz, wrap, anno) -> {
            Aop.wrapAndPut(clz, getProxy(clz, anno));
        });

        Aop.factory().beanInjectorAdd(FeignClient.class, (varH, anno) -> {
            varH.setValue(getProxy(varH.getType(), anno));
        });
    }

    private Object getProxy(Class<?> clz, FeignClient anno) {
        //获取配置器
        FeignConfiguration configuration = Aop.get(anno.configuration());

        //生成构建器
        Feign.Builder builder = Feign.builder();

        //初始化构建器
        builder.options(new Request.Options(1000, 3500))
                .retryer(new Retryer.Default(5000, 5000, 3));

        //进行配置
        builder = configuration.config(anno, builder);

        //构建target
        FeignTarget target = null;

        if (XUtil.isEmpty(anno.url())) {
            target = new FeignTarget(clz, anno.name(), anno.path(), XUpstreamFactory.global.create(anno.name()));
        } else {
            target = new FeignTarget(clz, anno.name(), anno.path(), () -> anno.url());
        }

        return builder.target(target);
    }

    @Override
    public void stop() throws Throwable {

    }
}
