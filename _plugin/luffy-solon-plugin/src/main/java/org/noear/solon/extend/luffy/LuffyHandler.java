package org.noear.solon.extend.luffy;

import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.TextUtils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.extend.luffy.impl.JtRun;

/**
 * @author noear 2021/3/28 created
 */
public class LuffyHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
        String path = ctx.path();
        boolean debug = ctx.paramAsInt("_debug", 0) == 1;

        if (debug) {
            String name = path.replace("/", "__");
            //AFileUtil.remove(path);
            ExecutorFactory.del(name);

            //RouteHelper.reset();
        }

        do_handle(path, ctx, debug);
    }

    private void do_handle(String path, Context ctx, boolean debug) throws Exception {
        String name = null;

        AFileModel file = JtRun.fileGet(path);

        //文件不存在，则404
        if (file == null || file.file_id == 0) {
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

        //如果有跳转，则跳转
        if (TextUtils.isEmpty(file.link_to) == false) {
            if (file.link_to.startsWith("@")) {
                do_handle(file.link_to.substring(1), ctx, debug);
            } else {
                ctx.redirect(file.link_to);
            }
            return;
        }

        ctx.attrSet("file", file);
        ctx.attrSet("file_tag", file.tag);


        ExecutorFactory.exec(name, file, ctx);
    }
}
