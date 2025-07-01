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
package org.noear.solon.luffy;

import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.TextUtils;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.luffy.impl.JtRun;

/**
 * @author noear
 * @since 1.3
 */
public class LuffyHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
        String path = ctx.pathNew();

        if (Solon.cfg().isDebugMode()) {
            String name = path.replace("/", "__");
            ExecutorFactory.del(name);
        }

        handleDo(path, ctx, Solon.cfg().isDebugMode());
    }

    private void handleDo(String path, Context ctx, boolean debug) throws Exception {

        AFileModel file = JtRun.fileGet(path);

        //文件不存在，则404
        if (file == null || file.path == null) {
            ctx.status(404);
            return;
        }

        if (file.is_disabled && debug == false) {
            ctx.status(403);
            return;
        }

        if (file.content_type != null && file.content_type.startsWith("code/")) {
            ctx.status(403);
            return;
        }

        if (Utils.isNotEmpty(file.method)) {
            if(file.method.contains(ctx.method()) == false) {
                ctx.status(405);
                return;
            }
        }

        //如果有跳转，则跳转
        if (TextUtils.isEmpty(file.link_to) == false) {
            if (file.link_to.startsWith("@")) {
                handleDo(file.link_to.substring(1), ctx, debug);
            } else {
                ctx.redirect(file.link_to);
            }
            return;
        }

        ctx.attrSet("file", file);


        String name = path.replace("/", "__");
        ExecutorFactory.exec(name, file, ctx);
    }
}
