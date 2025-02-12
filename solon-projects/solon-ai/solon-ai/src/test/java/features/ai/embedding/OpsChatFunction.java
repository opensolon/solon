package features.ai.embedding;

import org.noear.solon.ai.chat.functioncall.ChatFunction;
import org.noear.solon.ai.chat.functioncall.ChatFunctionParam;

import java.util.Map;

/**
 * @author noear 2025/2/12 created
 */
public class OpsChatFunction implements ChatFunction {
    @Override
    public String name() {
        return "";
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public Iterable<ChatFunctionParam> params() {
        return null;
    }

    @Override
    public String handle(Map<String, Object> args) {
        return "";
    }
}
