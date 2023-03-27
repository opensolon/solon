package demo;

import com.jfinal.json.JFinalJson;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.handle.RenderManager;

/**
 * @author noear 2023/3/27 created
 */
@Component
public class AppLoadEndEventListener implements EventListener<AppLoadEndEvent> {
    @Override
    public void onEvent(AppLoadEndEvent appLoadEndEvent) throws Throwable {
        //定制 json 序列化输出（使用新的处理接管 "@json" 指令）
        RenderManager.mapping("@json", (data, ctx) -> {
            String json = JFinalJson.getJson().toJson(data);
            ctx.outputAsJson(json);
        });
    }
}
