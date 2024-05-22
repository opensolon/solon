package demo.demo3;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.Solon;
import org.noear.solon.serialization.fastjson.FastjsonActionExecutor;
import org.noear.solon.serialization.fastjson.FastjsonRenderFactory;

import java.lang.reflect.Type;

/**
 * @author noear 2022/10/31 created
 */
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(demo.demo2.DemoApp.class, args, app -> {
            app.onEvent(FastjsonRenderFactory.class, e->{
                e.removeFeatures(SerializerFeature.BrowserCompatible);
            });

            app.onEvent(FastjsonActionExecutor.class, executor -> {
                executor.getSerializer().getDeserializeConfig().putDeserializer(String.class, new ObjectDeserializer() {
                    @Override
                    public <T> T deserialze(DefaultJSONParser defaultJSONParser, Type type, Object o) {
                        return null;
                    }

                    @Override
                    public int getFastMatchToken() {
                        return 0;
                    }
                });
            });
        });
    }
}
