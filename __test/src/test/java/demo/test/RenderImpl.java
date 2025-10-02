package demo.test;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

@Component("@json")
public class RenderImpl implements Render {
    @Override
    public void render(Object data, Context ctx) throws Throwable {
        //用 json 序列化器生成数据
        String json = Solon.app().serializers().jsonOf().serialize(data);
        String jsonEncoded = "";//加密
        String jsonEigned = ""; //鉴名

        ctx.headerSet("E", jsonEigned);
        ctx.output(jsonEncoded);
    }
}