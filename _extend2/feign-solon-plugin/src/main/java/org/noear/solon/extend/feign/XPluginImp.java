package org.noear.solon.extend.feign;

import feign.Feign;
import feign.Request;
import feign.Retryer;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.*;

import java.util.function.Consumer;


public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        //检查是否启用了@FeignClient
        if (app.source().getAnnotation(EnableFeignClient.class) == null) {
            return;
        }

        Aop.context().beanBuilderAdd(FeignClient.class, (clz, wrap, anno) -> {
            getProxy(clz, anno, obj -> Aop.wrapAndPut(clz, obj));
        });

        Aop.context().beanInjectorAdd(FeignClient.class, (varH, anno) -> {
            getProxy(varH.getType(), anno, obj -> varH.setValue(obj));
        });
    }

    private void getProxy(Class<?> clz, FeignClient anno, Consumer consumer) {
        //获取配置器
        FeignConfiguration configuration = Aop.get(anno.configuration());

        //生成构建器
        Feign.Builder builder0 = Feign.builder();

        //初始化构建器
        builder0.options(new Request.Options(1000, 3500))
                .retryer(new Retryer.Default(5000, 5000, 3));

        //进行配置
        builder0 = configuration.config(anno, builder0);

        Feign.Builder builder = builder0;

        //构建target
        if (XUtil.isEmpty(anno.url())) {
            XUpstream upstream = getUpstream(anno);
            if (upstream != null) {
                FeignTarget target = new FeignTarget(clz, anno.name(), anno.path(), upstream);
                consumer.accept(builder.target(target));
            } else {
                Aop.getAsyn(anno.name(), (bw) -> {
                    XUpstream tmp = bw.raw();
                    FeignTarget target = new FeignTarget(clz, anno.name(), anno.path(), tmp);
                    consumer.accept(builder.target(target));
                });
            }
        } else {
            FeignTarget target = new FeignTarget(clz, anno.name(), anno.path(), () -> anno.url());
            consumer.accept(builder.target(target));
        }
    }

    private XUpstream getUpstream(FeignClient anno){
        if(XBridge.upstreamFactory() == null){
            return null;
        }

        return XBridge.upstreamFactory().create(anno.name());
    }

    @Override
    public void stop() throws Throwable {

    }
}
