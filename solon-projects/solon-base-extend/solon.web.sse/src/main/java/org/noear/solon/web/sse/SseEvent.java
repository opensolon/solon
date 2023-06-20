package org.noear.solon.web.sse;

/**
 * Sse 事件
 *
 * @author kongweiguang
 * @since 2.3
 */
public class SseEvent {

    private final StringBuilder sb = new StringBuilder();

    /**
     * 添加 SSE "id" 行.
     */
    public SseEvent id(String id) {
        append("id:").append(id).append("\n");
        return this;
    }

    /**
     * 添加 SSE "event" 行.
     */
    public SseEvent name(String name) {
        append("event:").append(name).append("\n");
        return this;
    }

    /**
     * 添加 SSE "retry" 行.
     */
    public SseEvent reconnectTime(long reconnectTimeMillis) {
        append("retry:").append(String.valueOf(reconnectTimeMillis)).append("\n");
        return this;
    }

    /**
     * 添加 SSE "data" 行.
     */
    public SseEvent data(Object object) {
        append("data:").append(object.toString()).append("\n");
        return this;
    }

    /**
     * 构建为事件文本
     * */
    public String build() {
        return append("\n").sb.toString();
    }

    SseEvent append(String text) {
        this.sb.append(text);
        return this;
    }
}