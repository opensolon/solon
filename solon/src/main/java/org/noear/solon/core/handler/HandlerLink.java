package org.noear.solon.core.handler;

/**
 * 处理助手，提供处理链的存储
 *
 * @author noear
 * @since 1.0
 * */
public class HandlerLink implements Handler {
    /** 当前节点 */
    public Handler node;
    /** 下个节点 */
    public Handler nextNode;

    @Override
    public void handle(Context context) throws Throwable {
        if(node == null || context.getHandled()){
            return;
        }

        node.handle(context);


        if(nextNode == null || context.getHandled()){
            return;
        }

        nextNode.handle(context);
    }
}
