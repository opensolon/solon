package test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;

import java.lang.reflect.Type;

/**
 * @author noear 2023/4/24 created
 */
public class XxxxTest {
    public static void main(String[] args){
        ObjectWriterProvider writerProvider = new ObjectWriterProvider();
        writerProvider.register(Long.class, new ObjectWriter() {
            @Override
            public void write(JSONWriter jsonWriter, Object o, Object o1, Type type, long l) {
                jsonWriter.writeString(String.valueOf(o));
            }
        });

        JSONWriter.Context writeContext = new JSONWriter.Context(writerProvider,
                JSONWriter.Feature.WriteNullNumberAsZero);

        Demo demo = new Demo();
        String tmp = JSON.toJSONString(demo, writeContext);
        System.out.println(tmp);
    }
    public static class Demo{
        public long a;
        public Long b;
        public Long c = 1L;
    }
}
