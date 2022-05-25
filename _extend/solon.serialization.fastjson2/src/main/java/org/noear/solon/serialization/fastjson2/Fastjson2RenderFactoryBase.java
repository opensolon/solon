package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.writer.ObjectWriter;
import org.noear.solon.serialization.JsonConverter;
import org.noear.solon.serialization.JsonRenderFactory;


/**
 * Json 渲染器工厂基类
 *
 * @author noear
 * @since 1.5
 */
public abstract class Fastjson2RenderFactoryBase implements JsonRenderFactory {


    public <T> void addEncoder(Class<T> clz, ObjectWriter encoder) {

    }


    @Override
    public <T> void addConvertor(Class<T> clz, JsonConverter<T> converter) {

    }
}
