package org.noear.solon.serialization.jackson;

import org.noear.solon.core.XActionConverter;
import org.noear.solon.core.XContext;

public class JacksonConverter extends XActionConverter {
    @Override
    protected boolean matched(XContext ctx, String contextType) {
        if (contextType != null && contextType.contains("/json")) {
            return true;
        }else{
            return false;
        }
    }
}
