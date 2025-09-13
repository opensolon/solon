/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo.serialization.fastjson.demo3;

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
        Solon.start(demo.serialization.fastjson.demo2.DemoApp.class, args, app -> {
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
