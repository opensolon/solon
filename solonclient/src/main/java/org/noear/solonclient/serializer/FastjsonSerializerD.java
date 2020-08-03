package org.noear.solonclient.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.XApp;
import org.noear.solonclient.Enctype;
import org.noear.solonclient.IDeserializer;
import org.noear.solonclient.ISerializer;
import org.noear.solonclient.Result;

public class FastjsonSerializerD implements ISerializer, IDeserializer {
    static {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    public static final FastjsonSerializerD instance = new FastjsonSerializerD(false);
    public static final FastjsonSerializerD instance_type = new FastjsonSerializerD(true);


    private boolean usingType;

    public FastjsonSerializerD(boolean usingType) {
        this.usingType = usingType;
    }

    @Override
    public Enctype enctype() {
        return Enctype.application_json;
    }

    @Override
    public Object serialize(Object obj) {
        if (usingType) {
            return JSON.toJSONString(obj,
                    SerializerFeature.BrowserCompatible,
                    SerializerFeature.WriteClassName,
                    SerializerFeature.DisableCircularReferenceDetect);
        } else {
            return JSON.toJSONString(obj,
                    SerializerFeature.BrowserCompatible,
                    SerializerFeature.DisableCircularReferenceDetect);
        }
    }

    @Override
    public <T> T deserialize(Result rst, Class<T> clz) {
        String str = rst.bodyAsString();

        Object returnVal = null;
        try {
            if (str == null) {
                return (T) str;
            }
            returnVal = JSONObject.parseObject(str, new TypeReference<T>() {
            });

        } catch (Throwable ex) {
            if (XApp.cfg().isDebugMode()) {
                System.err.println("error::" + str);
            }

            returnVal = ex;
        }

        if (returnVal != null && Throwable.class.isAssignableFrom(returnVal.getClass())) {
            if (returnVal instanceof RuntimeException) {
                throw (RuntimeException) returnVal;
            } else {
                throw new RuntimeException((Throwable) returnVal);
            }
        } else {
            return (T) returnVal;
        }
    }
}
