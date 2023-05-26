package org.noear.solon.serialization.prop;

import org.noear.solon.core.AopContext;

import java.io.Serializable;

/**
 * Json 序列化框架通用配置属性
 *
 * @author noear
 * @since 1.12
 */
public class JsonProps implements Serializable {
    public static JsonProps create(AopContext context) {
        return context.cfg().getBean("solon.serialization.json", JsonProps.class);
    }

    public String dateAsTimeZone;
    public String dateAsFormat;
    public boolean dateAsTicks;

    public boolean longAsString;
    public boolean boolAsInt;

    public boolean nullStringAsEmpty;
    public boolean nullBoolAsFalse;
    public boolean nullNumberAsZero;
    public boolean nullArrayAsEmpty;
    public boolean nullAsWriteable;

    public boolean enumAsName;
}
