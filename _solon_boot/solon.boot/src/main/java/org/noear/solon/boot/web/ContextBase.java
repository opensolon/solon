package org.noear.solon.boot.web;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Note;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.handle.Context;

/**
 * @author noear
 * @since 1.11
 */
public abstract class ContextBase extends Context {
    private String contentCharset;
    @Override
    public String contentCharset() {
        if (contentCharset == null) {
            contentCharset = HeaderUtils.extractQuotedValueFromHeader(contentCharset(), "charset");

            if (Utils.isEmpty(contentCharset)) {
                contentCharset = ServerProps.request_encoding;
            }

            if (Utils.isEmpty(contentCharset)) {
                contentCharset = Solon.encoding();
            }
        }

        return contentCharset;
    }


    /**
     * 获取 sessionId
     */
    public final String sessionId() {
        return sessionState().sessionId();
    }

    /**
     * 获取 session 状态
     *
     * @param name 状态名
     */
    public final Object session(String name) {
        return sessionState().sessionGet(name);
    }

    /**
     * 获取 session 状态（类型转换，存在风险）
     *
     * @param name 状态名
     */
    @Note("泛型转换，存在转换风险")
    public final <T> T session(String name, T def) {
        Object tmp = session(name);
        if (tmp == null) {
            return def;
        } else {
            return (T) tmp;
        }
    }

    /**
     * 获取 session 状态，并以 int 型输出
     *
     * @since 1.6
     * @param name 状态名
     */
    public final int sessionAsInt(String name){
        return sessionAsInt(name, 0);
    }

    /**
     * 获取 session 状态，并以 int 型输出
     *
     * @since 1.6
     * @param name 状态名
     */
    public final int sessionAsInt(String name, int def) {
        Object tmp = session(name);
        if (tmp == null) {
            return def;
        } else {
            if (tmp instanceof Number) {
                return ((Number) tmp).intValue();
            } else if (tmp instanceof String) {
                String str = (String) tmp;
                if (str.length() > 0) {
                    return Integer.parseInt(str);
                }
            }

            return def;
        }
    }

    /**
     * 获取 session 状态，并以 long 型输出
     *
     * @since 1.6
     * @param name 状态名
     */
    public final long sessionAsLong(String name){
        return sessionAsLong(name, 0L);
    }

    /**
     * 获取 session 状态，并以 long 型输出
     *
     * @since 1.6
     * @param name 状态名
     */
    public final long sessionAsLong(String name, long def) {
        Object tmp = session(name);
        if (tmp == null) {
            return def;
        } else {
            if (tmp instanceof Number) {
                return ((Number) tmp).longValue();
            } else if (tmp instanceof String) {
                String str = (String) tmp;
                if (str.length() > 0) {
                    return Long.parseLong(str);
                }
            }

            return def;
        }
    }

    /**
     * 获取 session 状态，并以 double 型输出
     *
     * @since 1.6
     * @param name 状态名
     */
    public final double sessionAsDouble(String name) {
        return sessionAsDouble(name, 0.0D);
    }

    /**
     * 获取 session 状态，并以 double 型输出
     *
     * @since 1.6
     * @param name 状态名
     */
    public final double sessionAsDouble(String name, double def) {
        Object tmp = session(name);
        if (tmp == null) {
            return def;
        } else {
            if (tmp instanceof Number) {
                return ((Number) tmp).doubleValue();
            } else if (tmp instanceof String) {
                String str = (String) tmp;
                if (str.length() > 0) {
                    return Double.parseDouble(str);
                }
            }

            return def;
        }
    }

    /**
     * 设置 session 状态
     *
     * @param name 状态名
     * @param val 值
     */
    public final void sessionSet(String name, Object val) {
        sessionState().sessionSet(name, val);
    }

    /**
     * 移除 session 状态
     *
     * @param name 状态名
     * */
    public final void  sessionRemove(String name){
        sessionState().sessionRemove(name);
    }

    /**
     * 清空 session 状态
     * */
    public final void sessionClear() {
        sessionState().sessionClear();
    }
}
