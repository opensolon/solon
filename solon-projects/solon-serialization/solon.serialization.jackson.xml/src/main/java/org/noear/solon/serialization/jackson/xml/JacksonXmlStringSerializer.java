package org.noear.solon.serialization.jackson.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.serialization.ActionSerializer;

import java.io.IOException;

/**
 * Jackson xml序列化
 * @author painter
 * @since 1.5
 * @since 2.8
 */
public class JacksonXmlStringSerializer implements ActionSerializer<String> {
    private XmlMapper config;

    public XmlMapper getConfig() {
        if (config == null) {
            config = new XmlMapper();
        }

        return config;
    }

    public void setConfig(XmlMapper config) {
        if (config != null) {
            this.config = config;
        }
    }

    @Override
    public String name() {
        return "jackson-xml";
    }

    @Override
    public String serialize(Object obj) throws IOException {
        return getConfig().writeValueAsString(obj);
    }

    @Override
    public Object deserialize(String data, Class<?> clz) throws IOException {
        if (clz == null) {
            return getConfig().readTree(data);
        } else {
            return getConfig().readValue(data, clz);
        }
    }

    @Override
    public Object deserializeBody(Context ctx) throws IOException {
        String data = ctx.bodyNew();

        if (Utils.isNotEmpty(data)) {
            return getConfig().readTree(data);
        } else {
            return null;
        }
    }
}