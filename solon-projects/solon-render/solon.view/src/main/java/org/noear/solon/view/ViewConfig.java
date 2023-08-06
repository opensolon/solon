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
    public static final String RES_WEBINF_VIEW_LOCATION = "/WEB-INF/view/";
    public static final String RES_WEBINF_VIEW_LOCATION2 = "/WEB-INF/templates/";

    public static final String HEADER_VIEW_META = "Solon-View";

    private static boolean outputMeta;
    private static String viewPrefix;

    static {
        outputMeta = Solon.cfg().getInt("solon.output.meta", 0) > 0;
        viewPrefix = Solon.cfg().get("solon.view.prefix");

        if (Utils.isEmpty(viewPrefix)) {
            if (ResourceUtil.hasResource(RES_WEBINF_VIEW_LOCATION)) { //兼容旧的
                //第一优化
                viewPrefix = RES_WEBINF_VIEW_LOCATION;
            } else if (ResourceUtil.hasResource(RES_WEBINF_VIEW_LOCATION2)) { //与 RES_VIEW_LOCATION 统一名字
                //第二优化
                viewPrefix = RES_WEBINF_VIEW_LOCATION2;
            } else if (ResourceUtil.hasResource(RES_VIEW_LOCATION)) { //新方案
                //第三优化
                viewPrefix = RES_VIEW_LOCATION;
            } else {
                //默认
                viewPrefix = RES_WEBINF_VIEW_LOCATION;
            }
        } else {
            //自动加 "/"
            if (viewPrefix.startsWith("/") == false) {
                viewPrefix = "/" + viewPrefix;
            }

            if (viewPrefix.endsWith("/") == false) {
                viewPrefix = viewPrefix + "/";
            }
        }
    }

    /**
     * 是否输出元信息
     * */
    public static boolean isOutputMeta() {
        return outputMeta;
    }

    /**
     * 获取视图前缀
     * */
    public static String getViewPrefix() {
        return viewPrefix;
    }
}
