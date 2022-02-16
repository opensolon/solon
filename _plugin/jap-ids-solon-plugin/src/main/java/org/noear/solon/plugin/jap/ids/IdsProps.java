package org.noear.solon.plugin.jap.ids;


import com.fujieid.jap.ids.config.IdsConfig;
import org.noear.solon.Solon;

/**
 * @author 颖
 */
public class IdsProps {

    public static String bastPath = null;
    public static IdsConfig idsConfig = null;

    public static void initialize() {
        bastPath = Solon.cfg().get("jap.ids.bastPath", "/oauth");
        bastPath = bastPath.startsWith("/") ? bastPath : "/" + bastPath;
        idsConfig = Solon.cfg().getBean(
                "jap.ids.config",
                IdsConfig.class
        );
    }

}
