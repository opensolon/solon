package org.noear.nami.coder.gson;

import org.noear.nami.Context;
import org.noear.nami.Encoder;
import org.noear.nami.common.ContentTypes;

import com.google.gson.Gson;

/**
 * 编码器:负责把对象编码为json
 * 
 * @author cqyhm
 * @since 2025年12月30日13:25:26
 */
public class GsonEncoder implements Encoder {
    public static final GsonEncoder instance = new GsonEncoder();

    private Gson gson;
    public GsonEncoder() {
    	this.gson = new Gson();
    }
    @Override
    public String enctype() {
        return ContentTypes.JSON_VALUE;
    }

    @Override
    public byte[] encode(Object obj) {
        return gson.toJson(obj).getBytes();
    }

    @Override
    public void pretreatment(Context ctx) {

    }
}
