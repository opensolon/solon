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
     * Add an SSE "id" line.
     */
    public SseEvent id(String id) {
        append("id:").append(id).append("\n");
        return this;
    }

    /**
     * Add an SSE "event" line.
     */
    public SseEvent name(String name) {
        append("event:").append(name).append("\n");
        return this;
    }

    /**
     * Add an SSE "retry" line.
     */
    public SseEvent reconnectTime(long reconnectTimeMillis) {
        append("retry:").append(String.valueOf(reconnectTimeMillis)).append("\n");
        return this;
    }

    /**
     * Add an SSE "data" line.
     */
    public SseEvent data(Object object) {
        append("data:").append(object.toString()).append("\n");
        return this;
    }

    public String build() {
        return append("\n").sb.toString();
    }

    SseEvent append(String text) {
        this.sb.append(text);
        return this;
    }
}