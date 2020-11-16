package org.noear.solon.serialization.avro;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.noear.solon.core.handler.Context;
import org.noear.solon.core.handler.Render;

import java.io.ByteArrayOutputStream;

public class AvroRender implements Render {
    @Override
    public void render(Object obj, Context ctx) throws Throwable {


        //非序列化处理
        //
        if (obj == null) {
            return;
        }

        if (obj instanceof Throwable) {
            throw (Throwable) obj;
        }

        if (obj instanceof String) {
            ctx.output((String) obj); //不能做为json输出
            return;
        }

        DatumWriter datumWriter = new SpecificDatumWriter(obj.getClass());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out , null);

        datumWriter.write(obj , encoder);

        String txt = out.toString();


        if (XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "AvroRender");
        }

        ctx.attrSet("output", txt);
        ctx.outputAsJson(txt);
    }
}
