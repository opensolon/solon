package org.noear.solon.serialization.avro;

import org.noear.solon.core.handle.Context;
import org.noear.solon.serialization.StringSerializerRender;

public class AvroStringRender extends StringSerializerRender {
    public AvroStringRender(){
        super(false, new AvroSerializer());
    }

    @Override
    protected void output(Context ctx, Object obj, String txt) {
        ctx.output(txt);
    }
}
