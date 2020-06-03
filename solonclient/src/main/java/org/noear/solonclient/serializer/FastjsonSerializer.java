package org.noear.solonclient.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solonclient.ISerializer;

public class FastjsonSerializer implements ISerializer {
    static {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    public static final FastjsonSerializer instance = new FastjsonSerializer();


    @Override
    public String stringify(Object obj) {
        return JSON.toJSONString(obj,
                SerializerFeature.BrowserCompatible,
                SerializerFeature.DisableCircularReferenceDetect);
    }

    @Override
    public String serialize(Object obj) {
        return JSON.toJSONString(obj,
                SerializerFeature.BrowserCompatible,
                SerializerFeature.WriteClassName,
                SerializerFeature.DisableCircularReferenceDetect);
    }

    @Override
    public <T> T deserialize(String str, Class<T> clz) {
        Object returnVal = null;
        try {
            if (str == null) {
                return (T)str;
            }
            returnVal = JSONObject.parseObject(str, new TypeReference<T>(){});

        }catch (Throwable ex){
            System.err.println("error::" + str);
            returnVal = ex;
        }

        if (returnVal != null && Throwable.class.isAssignableFrom(returnVal.getClass())) {
            throw new RuntimeException((Throwable)returnVal);
        } else {
            return (T)returnVal;
        }
    }
}
