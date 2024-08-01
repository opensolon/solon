/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.view;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;

import java.io.File;
import java.net.URI;
import java.net.URL;

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
            if (ResourceUtil.hasFile(viewPrefix) == false) {
                if (viewPrefix.startsWith("/") == false) {
                    viewPrefix = "/" + viewPrefix;
                }
            }

            if (viewPrefix.endsWith("/") == false) {
                viewPrefix = viewPrefix + "/";
            }
        }
    }

    /**
     * 是否输出元信息
     */
    public static boolean isOutputMeta() {
        return outputMeta;
    }

    /**
     * 获取视图前缀
     */
    public static String getViewPrefix() {
        return viewPrefix;
    }

    /**
     * 获取视图调试位置
     */
    public static File getDebugLocation(ClassLoader classLoader, String viewPrefix) {
        if(ResourceUtil.hasFile(viewPrefix)){
            return null;
        }

        //添加调试模式
        URL rooturi = ResourceUtil.getResource(classLoader, "/");
        if (rooturi == null) {
            return null;
        }

        String rootdir = rooturi.toString().replace("target/classes/", "");
        File dir = null;

        if (rootdir.startsWith("file:")) {
            String dir_str = rootdir + "src/main/resources" + viewPrefix;
            dir = new File(URI.create(dir_str));
            if (!dir.exists()) {
                dir_str = rootdir + "src/main/webapp" + viewPrefix;
                dir = new File(URI.create(dir_str));
            }
        }

        return dir;
    }
}
