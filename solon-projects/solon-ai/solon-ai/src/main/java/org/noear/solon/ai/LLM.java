package org.noear.solon.ai;

/**
 * @author noear 2025/1/27 created
 */
public interface LLM<T extends Message> {
    void prompt(T... messages);
}
