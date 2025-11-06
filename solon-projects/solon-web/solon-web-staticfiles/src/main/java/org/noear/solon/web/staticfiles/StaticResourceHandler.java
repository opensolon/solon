/*
 * Copyright 2017-2025 noear.org and authors
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
package org.noear.solon.web.staticfiles;

import org.noear.solon.Utils;
import org.noear.solon.server.prop.GzipProps;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.server.util.OutputUtils;
import org.noear.solon.core.util.DateUtil;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * 静态文件资源处理
 *
 * @author noear
 * @since 1.0
 */
public class StaticResourceHandler implements Handler {
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String LAST_MODIFIED = "Last-Modified";
    private static final Date modified_time = new Date();


    @Override
    public void handle(Context ctx) throws Exception {
        if (ctx.getHandled()) {
            return;
        }

        if (MethodType.GET.name.equals(ctx.method()) == false) {
            return;
        }

        String path = ctx.pathNew();

        //找后缀名
        String suffix = findByExtName(path);
        if (Utils.isEmpty(suffix)) {
            return;
        }

        //找内容类型(先用配置的，再用jdk的)
        String conentType = StaticMimes.findByExt(suffix);

        if (Utils.isEmpty(conentType)) {
            conentType = Utils.mime(suffix);
        }

        //说明没有支持的mime
        if (Utils.isEmpty(conentType)) {
            return;
        }

        //找资源
        URL resUri = null;
        String resUriZiped = null;

        String acceptEncoding = ctx.headerOrDefault("Accept-Encoding", "");

        if (GzipProps.hasMime(conentType)) {
            //如果有支持压缩的类型
            if (acceptEncoding.contains("gzip")) {
                resUri = StaticMappings.find(path + ".gz");
                if (resUri != null) {
                    resUriZiped = "gzip";
                }
            }

            if (resUri == null && acceptEncoding.contains("br")) {
                resUri = StaticMappings.find(path + ".br");
                if (resUri != null) {
                    resUriZiped = "br";
                }
            }
        }

        if (resUri == null) {
            //如果还没有资源
            resUri = StaticMappings.find(path);
        }

        if (resUri != null) {
            ctx.setHandled(true);

            String modified_since = ctx.header("If-Modified-Since");
            String modified_now = DateUtil.toGmtString(modified_time);

            if(ctx.headerValuesOfResponse(CACHE_CONTROL)== null){
                // 用户设置优先,用户不设置时才会使用配置文件或默认配置
                ctx.headerSet(CACHE_CONTROL, "max-age=" + StaticConfig.getCacheMaxAge());//单位秒
            }

            ctx.headerSet(LAST_MODIFIED, modified_now);
            if (modified_since != null) {
                if (modified_since.equals(modified_now)) {
                    ctx.status(304);
                    return;
                }
            }


            //开始输出：
            if ("br".equals(resUriZiped) && acceptEncoding.contains("br")) {
                //如果支持 br
                try (InputStream stream = resUri.openStream()) {
                    ctx.contentType(conentType);
                    ctx.headerSet("Vary", "Accept-Encoding");
                    ctx.headerSet("Content-Encoding", "br");
                    OutputUtils.global().outputStreamAsRange(ctx, stream, stream.available());
                }
                return;
            }

            if ("gzip".equals(resUriZiped) && acceptEncoding.contains("gzip")) {
                //如果支持 gzip
                try (InputStream stream = resUri.openStream()) {
                    ctx.contentType(conentType);
                    ctx.headerSet("Vary", "Accept-Encoding");
                    ctx.headerSet("Content-Encoding", "gzip");
                    OutputUtils.global().outputStreamAsRange(ctx, stream, stream.available());
                }
                return;
            }


            OutputUtils.global().outputFile(ctx, resUri, conentType, StaticConfig.getCacheMaxAge() >= 0);
        }
    }


    /**
     * 尝试查找路径的后缀名
     */
    private String findByExtName(String path) {
        int pos = path.lastIndexOf(35); //'#'
        if (pos > 0) {
            path = path.substring(0, pos - 1);
        }

        pos = path.lastIndexOf(46); //'.'
        pos = Math.max(pos, path.lastIndexOf(47)); //'/'
        pos = Math.max(pos, path.lastIndexOf(63)); //'?'

        if (pos != -1 && path.charAt(pos) == '.') {
            return path.substring(pos).toLowerCase();
        } else {
            return null;
        }
    }
}