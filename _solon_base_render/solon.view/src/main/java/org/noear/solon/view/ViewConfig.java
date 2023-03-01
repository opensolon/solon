package org.noear.solon.view;

import org.noear.solon.Solon;

/**
 * 视图配置
 *
 * @author noear
 * @since 2.2
 */
public class ViewConfig {
    public static final String RES_VIEW_LOCATION = "templates/";
    public static final String RES_WEB_INF_VIEW_LOCATION = "WEB-INF/view/";

    public static final String HEADER_VIEW_META = "Solon-View";

    private static boolean isOutputMeta;

    static {
        isOutputMeta = Solon.cfg().getInt("solon.output.meta", 0) > 0;
    }

    public static boolean isOutputMeta(){
        return isOutputMeta;
    }
}
