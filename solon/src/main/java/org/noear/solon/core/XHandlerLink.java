package org.noear.solon.core;

/**
 * 处理助手，提供处理链的存储
 *
 * @author noear
 * @since 1.0
 * */
public class XHandlerLink implements XHandler {
    /** 当前节点 */
    public XHandler node;
    /** 下个节点 */
    public XHandler nextNode;

    @Override
    public void handle(XContext context) throws Throwable {
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
