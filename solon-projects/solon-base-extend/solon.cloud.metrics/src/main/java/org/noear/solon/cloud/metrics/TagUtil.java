package org.noear.solon.cloud.metrics;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Context;


/**
 * 标签跑龙套
 *
 * @author bai
 * @since 2.4
 */
public class TagUtil {


    /**
     * 标签
     *
     * @param inv     发票
     * @param type    类型
     * @param AnoTags 另标签
     * @return {@link String[]}
     */
    public static String[] tags(Invocation inv, String type, String[] AnoTags) {
        String[] tags;
        Context ctx = Context.current();
        if (AnoTags.length == 0) {
            tags = new String[]{"path", ctx.path(), "type", type, inv.target().getClass().getTypeName(), inv.method().toString()};
        } else {
            tags = AnoTags;
        }
        return tags;
    }
}
