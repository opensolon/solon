package org.noear.solon.serialization.snack3;

import org.noear.snack.ONode;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XParamConverter;

public class SnackConverter extends XParamConverter {
    @Override
    public Object getEntity(XContext ctx, String name, Class<?> type) throws Exception {
        String ct = ctx.contentType();

        if (ct != null && ct.indexOf("/json") > 0) {
            if(name == null) {
                return ONode.deserialize(ctx.body(), type);
            }else{
                ONode node = ONode.loadStr(ctx.body());
                if(node.contains(name)){
                    return node.get(name).toObject(type);
                }else{
                    return node.toObject(type);
                }
            }
        } else {
            return super.getEntity(ctx, name, type);
        }
    }
}
