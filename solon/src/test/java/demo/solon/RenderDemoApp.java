package demo.solon;

import org.noear.solon.Solon;

/**
 * @author noear 2025/9/22 created
 */
public class RenderDemoApp {
    public static void main(String[] args) throws Throwable {
        Solon.start(RenderDemoApp.class, args, app->{
            //注册一个 "customize" 渲染处理器为

            app.renders().register("customize", (data, ctx) -> {
                //用 json 渲染器生成数据
                String json = app.serializers().<String>get("@json").serialize(data);
                String jsonEncoded = "";//加密
                String jsonEigned = ""; //鉴名

                ctx.headerSet("E", jsonEigned);
                ctx.output(jsonEncoded);
            });

            //通过过滤器，指定渲染处理器为 "customize"
            app.filter(-999, (ctx,chain)->{
                ctx.attrSet("@render", "customize");
            });
        });
    }
}
