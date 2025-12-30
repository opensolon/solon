package org.noear.nami.coder.gson;

import java.lang.reflect.Type;

import org.noear.nami.Context;
import org.noear.nami.Decoder;
import org.noear.nami.EncoderTyped;
import org.noear.nami.Result;
import org.noear.nami.common.ContentTypes;
import org.noear.nami.exception.NamiDecodeException;
import org.noear.solon.Utils;

import com.google.gson.Gson;

/**
 * 解码器:负责把json解码为对象
 * 
 * @author cqyhm
 * @since 2025年12月30日13:24:54
 */
public class GsonDecoder implements Decoder {
    public static final GsonDecoder instance = new GsonDecoder();

    private Gson gson;

    public GsonDecoder() {
    	this.gson = new Gson();
    }

    @Override
    public String enctype() {
        return ContentTypes.JSON_VALUE;
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T decode(Result rst, Type type) throws Exception {
        if (rst.body().length == 0) {
            return null;
        }

        String str = rst.bodyAsString();
        if ("null".equals(str)) {
            return null;
        }

        try {
            if (str.contains("\"stackTrace\":[{")) {
                return (T) gson.fromJson(str, RuntimeException.class);
            } else {
            	// 非单双引号开头的字符串直接转换为对象
                if (String.class == type && Utils.isNotEmpty(str)) {
                    if (str.charAt(0) != '\'' && str.charAt(0) != '"') {
                        return (T) str;
                    }
                }

                return (T) gson.fromJson(str, type);
            }
        } catch (Throwable ex) {
            throw new NamiDecodeException("Decoding failure, type: " + type.getTypeName() + ", data: " + str, ex);
        }
    }

    @Override
    public void pretreatment(Context ctx) {
        if (ctx.config.getEncoder() instanceof EncoderTyped) {
            ctx.headers.put(ContentTypes.HEADER_SERIALIZATION, ContentTypes.AT_JSON);
        }

        ctx.headers.put(ContentTypes.HEADER_ACCEPT, ContentTypes.JSON_VALUE);
    }
}
