package org.noear.dami.solon;

/**
 * 监听器记录
 *
 * @author noear
 * @since 1.0
 */
public class ListenerRecord {
    private final String topicMapping;
    private final Object listenerObj;

    public ListenerRecord(String topicMapping, Object listenerObj) {
        this.topicMapping = topicMapping;
        this.listenerObj = listenerObj;
    }

    public String getTopicMapping() {
        return topicMapping;
    }

    public Object getListenerObj() {
        return listenerObj;
    }
}
