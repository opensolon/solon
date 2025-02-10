package org.noear.solon.ai.chat;

import org.noear.solon.ai.AiUsage;

/**
 * @author noear 2025/2/10 created
 */
public class ChatUsage implements AiUsage {
    //总时长
    public long total_duration;
    //加载时长
    public long load_duration;
    //提示语执行数量
    public long prompt_eval_count;
    //提示语执行时长
    public long prompt_eval_duration;
    //执行数量
    public long eval_count;
    //执行时长
    public long eval_duration;
}
