package com.fujieid.jap.ids.solon;

import com.fujieid.jap.ids.config.IdsConfig;
import org.noear.solon.Solon;

/**
 * @author 颖
 * @since 1.6
 */
public class IdsProps {

    public static final String BAST_PATH = Solon.cfg().get("jap.ids.basePath", "/oauth");
    public static final String WELL_PATH = "/.well-known";
    public static final IdsConfig IDS_CONFIG = Solon.cfg().getBean("jap.ids.config", IdsConfig.class);

}
