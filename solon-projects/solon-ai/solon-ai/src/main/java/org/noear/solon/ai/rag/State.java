package org.noear.solon.ai.rag;

import java.util.List;

/**
 * @author noear 2025/2/12 created
 */
public class State {
    /**
     * 上下文
     */
    List<Document> context;
    /**
     * 问题
     */
    String question;
    /**
     * 答案
     */
    String answer;
}
