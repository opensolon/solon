package org.noear.solon.view;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;

/**
 * 视图配置
 *
 * @author noear
 * @since 2.2
 */
public class ViewConfig {
    public static final String RES_VIEW_LOCATION = "/templates/";
    public static final String RES_WEB_INF_VIEW_LOCATION = "/WEB-INF/view/";

    public static final String HEADER_VIEW_META = "Solon-View";

    private static boolean outputMeta;
    private static String viewBaseUri;

    static {
        outputMeta = Solon.cfg().getInt("solon.output.meta", 0) > 0;
        viewBaseUri = Solon.cfg().get("solon.view.prefix");

        if (Utils.isEmpty(viewBaseUri)) {
            if (ResourceUtil.hasResource(RES_WEB_INF_VIEW_LOCATION)) {
                //第一优化
                viewBaseUri = RES_WEB_INF_VIEW_LOCATION;
            } else if (ResourceUtil.hasResource(RES_VIEW_LOCATION)) {
                //第二优化
                viewBaseUri = RES_VIEW_LOCATION;
            } else {
                //默认
                viewBaseUri = RES_WEB_INF_VIEW_LOCATION;
            }
        }
    }

    public static boolean isOutputMeta() {
        return outputMeta;
    }

    public static String getBaseUri() {
        return viewBaseUri;
    }
}
