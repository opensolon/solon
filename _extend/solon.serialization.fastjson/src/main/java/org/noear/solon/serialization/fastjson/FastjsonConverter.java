package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XConverter;

public class FastjsonConverter extends XConverter {
    @Override
    public Object convert(XContext ctx, String name, Class<?> type) throws Exception {
        String ct = ctx.contentType();

        if (ct != null && ct.indexOf("/json") > 0) {
            if(name == null) {
                return JSON.parseObject(ctx.body(), type);
            }else{
                JSONObject node = JSON.parseObject(ctx.body());

                if(node.containsKey(name)){
                    return node.getObject(name,type);
                }else {
                    return node.toJavaObject(type);
                }
            }
        } else {
            return super.convert(ctx, name, type);
        }
    }
}
