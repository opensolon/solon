package com.fujieid.jap.ids.solon;


import com.fujieid.jap.ids.config.IdsConfig;
import org.noear.solon.Solon;

/**
 * @author 颖
 * @since 1.6
 */
public class IdsProps {
    public static final String bastPath;
    public static final String wellPath = "/.well-known";
    public static final IdsConfig idsConfig;

    static {
        bastPath = Solon.cfg().get("jap.ids.bastPath", "/oauth");
        idsConfig = Solon.cfg().getBean(
                "jap.ids.config",
                IdsConfig.class
        );
    }
}
