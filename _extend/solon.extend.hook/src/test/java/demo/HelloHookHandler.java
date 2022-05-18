package demo;

import org.noear.solon.extend.hook.HookHandler;
import org.noear.solon.extend.hook.annotation.HookAction;

import java.util.Map;

/**
 * @author noear 2022/5/18 created
 */
@HookAction("hello")
public class HelloHookHandler implements HookHandler {

    @Override
    public void handle(Map<String, Object> args) {
        System.out.println(args);
    }
}