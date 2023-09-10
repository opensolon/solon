package demo.mod1;

import org.noear.dami.solon.annotation.Dami;

//保持与 Sender 相同的接口定义
@Dami(topicMapping = "demo.user")
public class UserEventListenerImpl {
    public long created(long userId, String name) {
        System.err.println("created: userId=" + userId + ", name=" + name);
        return userId;
    }
    public void updated(long userId, String name) {
        System.err.println("updated: userId=" + userId + ", name=" + name);
    }
}
