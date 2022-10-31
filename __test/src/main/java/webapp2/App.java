package webapp2;

import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.event.PluginLoadEndEvent;
import org.noear.solon.core.handle.Context;
import org.noear.solon.serialization.snack3.SnackJsonActionExecutor;

/**
 * @author noear 2022/7/11 created
 */
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            app.onEvent(PluginLoadEndEvent.class, e->{
                Bridge.actionExecutorRemove(SnackJsonActionExecutor.class);
                Bridge.actionExecutorAdd(new SnackJsonActionExecutorNew());
            });
        });
    }

    public static class SnackJsonActionExecutorNew extends SnackJsonActionExecutor {
        Options options = Options.def();
        public SnackJsonActionExecutorNew() {
            options.addDecoder(String.class, (node, type) -> {
                if (Utils.isEmpty(node.getString())) {
                    return null;
                } else {
                    return node.getString();
                }
            });
        }

        @Override
        protected Object changeBody(Context ctx) throws Exception {
            String json = ctx.bodyNew();

            if (Utils.isNotEmpty(json)) {
                return ONode.loadStr(json, options);
            } else {
                return null;
            }
        }
    }
}