package org.noear.solon.serialization.avro;

import org.noear.solon.core.handle.Context;
import org.noear.solon.serialization.ContextSerializer;
import org.noear.solon.serialization.StringSerializerRender;

public class AvroStringRender extends StringSerializerRender {

    public AvroStringRender(boolean typed, ContextSerializer<String> serializer) {
        super(typed, serializer);
    }

    @Override
    protected void output(Context ctx, Object obj, String txt) {
        ctx.output(txt);
    }
}
