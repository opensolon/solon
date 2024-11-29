package features.sessionstate.local;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2024/11/29 created
 */
@Controller
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
    }

    @Mapping
    public Object home(Context ctx) {
        SessionState state = ctx.sessionState();

        Map<String, Object> map = new HashMap<>();

        map.put("id", state.sessionId());
        map.put("t1", state.creationTime());
        map.put("t2", state.lastAccessTime());

        return map;
    }
}
