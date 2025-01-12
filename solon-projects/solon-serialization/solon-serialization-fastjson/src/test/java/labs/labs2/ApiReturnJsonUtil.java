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
package labs.labs2;

/**
 * @author noear 2023/2/17 created
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;

/**
 * 接口返回 Json 格式化工具
 */
public class ApiReturnJsonUtil {
    static final SerializeConfig serializeConfig = new SerializeConfig();
    static final SerializerFeature[] serializeFeatures;

    static {
        //添加特性
        HashSet<SerializerFeature> tmp = new HashSet<>();
        tmp.add(SerializerFeature.WriteNullListAsEmpty);
        tmp.add(SerializerFeature.WriteMapNullValue);
        tmp.add(SerializerFeature.WriteNullNumberAsZero);
        tmp.add(SerializerFeature.WriteNullBooleanAsFalse);
        tmp.add(SerializerFeature.WriteNullStringAsEmpty);
        serializeFeatures = tmp.toArray(new SerializerFeature[tmp.size()]);

        //添加编码器
        serializeConfig.put(Long.class, new LongAsStringJsonSerializer());
        serializeConfig.put(Boolean.class, new BoolAsNumberJsonSerializer());
    }

    /**
     * 转为特定要求的 Json 字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return "";
        }

        return JSON.toJSONString(obj, serializeConfig, serializeFeatures);
    }

    public static class BoolAsNumberJsonSerializer implements ObjectSerializer {

        @Override
        public void write(JSONSerializer ser, Object o, Object o1, Type type, int i) throws IOException {
            SerializeWriter out = ser.getWriter();
            if (o == null) {
                out.writeInt(0);
            } else {
                out.writeInt(((Boolean) o) ? 1 : 0);
            }
        }
    }

    public static class LongAsStringJsonSerializer implements ObjectSerializer {
        @Override
        public void write(JSONSerializer ser, Object o, Object o1, Type type, int i) throws IOException {
            SerializeWriter out = ser.getWriter();
            if (o == null) {
                out.writeString("0");
            } else {
                out.writeString(((Number) o).toString());
            }
        }
    }
}