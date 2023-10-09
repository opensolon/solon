package labs.labs2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;

/**
 * @author noear 2023/4/24 created
 */
public class XxxxTest {
    public static void main(String[] args){
        ObjectWriterProvider writerProvider = new ObjectWriterProvider();
        writerProvider.register(Long.class, (out, obj, fieldName, fieldType, features) -> {
            out.writeString(String.valueOf(obj));
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
