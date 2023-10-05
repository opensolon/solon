package feign.solon.integration;

import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.solon.EnableFeignClient;
import feign.solon.FeignClient;
import feign.solon.FeignConfiguration;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.*;

import java.util.function.Consumer;


public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        //检查是否启用了@FeignClient
        if (Solon.app().source().getAnnotation(EnableFeignClient.class) == null) {
            return;
        }

        context.beanBuilderAdd(FeignClient.class, (clz, wrap, anno) -> {
            getProxy(wrap.context(), clz, anno, obj -> wrap.context().wrapAndPut(clz, obj));
        });

        context.beanInjectorAdd(FeignClient.class, (varH, anno) -> {
            getProxy(varH.context(), varH.getType(), anno, obj -> varH.setValue(obj));
        });
    }

    private void getProxy(AppContext ctx, Class<?> clz, FeignClient anno, Consumer consumer) {
        //获取配置器
        FeignConfiguration configuration = ctx.wrapAndPut(anno.configuration()).get();

        //生成构建器
        Feign.Builder builder0 = Feign.builder();

        //初始化构建器
        builder0.options(new Request.Options(1000, 3500))
                .retryer(new Retryer.Default(5000, 5000, 3));

        //进行配置
        builder0 = configuration.config(anno, builder0);

        Feign.Builder builder = builder0;

        //构建target
        if (Utils.isEmpty(anno.url())) {
            LoadBalance upstream = getUpstream(anno);
            if (upstream != null) {
                FeignTarget target = new FeignTarget(clz, anno.name(), anno.path(), upstream);
                consumer.accept(builder.target(target));
            } else {
                ctx.getWrapAsync(anno.name(), (bw) -> {
                    LoadBalance tmp = bw.raw();
                    FeignTarget target = new FeignTarget(clz, anno.name(), anno.path(), tmp);
                    consumer.accept(builder.target(target));
                });
            }
        } else {
            FeignTarget target = new FeignTarget(clz, anno.name(), anno.path(), () -> anno.url());
            consumer.accept(builder.target(target));
        }
    }

    private LoadBalance getUpstream(FeignClient anno) {
        return LoadBalance.get(anno.group(), anno.name());
    }

    @Override
    public void stop() throws Throwable {

    }
}
