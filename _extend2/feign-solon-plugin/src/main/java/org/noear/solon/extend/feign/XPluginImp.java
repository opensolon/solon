package org.noear.solon.extend.feign;

import feign.Feign;
import feign.Request;
import feign.Retryer;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;
import org.noear.solon.core.XUpstreamFactory;


public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Aop.factory().beanCreatorAdd(FeignClient.class, (clz, wrap, anno) -> {
            //获取配置器
            FeignConfiguration configuration = Aop.get(anno.configuration());

            //生成构建器
            Feign.Builder builder = Feign.builder();

            //初始化构建器
            builder.options(new Request.Options(1000, 3500))
                    .retryer(new Retryer.Default(5000, 5000, 3));

            //进行配置
            configuration.config(anno, builder);

            //构建target
            FeignTarget target = null;

            if (XUtil.isEmpty(anno.url())) {
                target = new FeignTarget(clz, anno.name(), XUpstreamFactory.global.create(anno.name()));
            } else {
                target = new FeignTarget(clz, anno.name(), () -> anno.url());
            }

            Object proxy = builder.target(target);

            Aop.wrapAndPut(clz,proxy);
        });
    }

    @Override
    public void stop() throws Throwable {

    }
}
