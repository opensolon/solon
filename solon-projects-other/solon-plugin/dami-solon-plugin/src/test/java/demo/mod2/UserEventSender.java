package demo.mod2;

import org.noear.dami.solon.annotation.Dami;

/**
 * 提示：需要要支持类隔离的环境，请使用基本类型做为参数与返回
 * */
@Dami(topicMapping = "demo.user")
public interface UserEventSender {
    long created(long userId, String name);
    void updated(long userId, String name);
}
