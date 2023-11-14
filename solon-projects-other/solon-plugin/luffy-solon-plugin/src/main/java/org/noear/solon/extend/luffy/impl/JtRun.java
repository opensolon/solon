package org.noear.solon.extend.luffy.impl;

import org.noear.luffy.dso.JtBridge;
import org.noear.luffy.dso.JtFun;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ContextUtil;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;


/**
 * @author noear
 * @since 1.3
 */
public class JtRun {
    private static CompletableFuture<Integer> initFuture = new CompletableFuture<>();

    private static final JtResouceLoaderManager resouceLoader = new JtResouceLoaderManager();
    private static JtExecutorAdapter jtAdapter;

    public static void init() {
        if (jtAdapter == null) {
            resouceLoader.add(0, new JtResouceLoaderClass());

            //添加外部扩展的资源加载器
            Solon.context().subBeansOfType(JtResouceLoader.class, bean -> {
                resouceLoader.add(0, bean);
            });

            jtAdapter = new JtExecutorAdapter(resouceLoader);
            JtBridge.executorAdapterSet(jtAdapter);
            JtBridge.configAdapterSet(jtAdapter);
        }
    }

    /**
     * 获取资源加载器（可以清空并替换为数据库的）
     * */
    public static JtResouceLoaderManager getResouceLoader() {
        return resouceLoader;
    }

    public static Object call(String path, Context ctx) throws Exception {
        AFileModel file = JtBridge.fileGet(path);

        return ExecutorFactory.execOnly(file, ctx);
    }

    public static void exec(String path, Context ctx) throws Exception {
        AFileModel file = JtBridge.fileGet(path);

        ExecutorFactory.execOnly(file, ctx);
    }

    public static void exec(String code) throws Exception {
        AFileModel file = new AFileModel();
        file.path = Utils.md5(code);
        file.content = code;
        file.edit_mode = "javascript";

        exec(file);
    }

    public static AFileModel fileGet(String path) throws Exception{
        return jtAdapter.fileGet(path);
    }

    public static void exec(AFileModel file) throws Exception {
        initFuture.get();

        Context ctx = ContextEmpty.create();

        ContextUtil.currentSet(ctx);
        ExecutorFactory.execOnly(file, ctx);
        ContextUtil.currentRemove();
    }

    public static void xfunInit() {
        JtFun.g.set("afile_get_paths", (map) -> {
            String tag = (String) map.get("tag");
            String label = (String) map.get("label");
            Boolean useCache = (Boolean) map.get("useCache");
            return Collections.emptyList(); //return DbPaaSApi.fileGetPaths(tag, label, useCache);//List<AFileModel>
        });

        JtFun.g.set("afile_get", (map) -> {
            String path = (String) map.get("path");
            return jtAdapter.fileGet(path); //return DbPaaSApi.fileGet(path);//AFileModel
        });

        //CallUtil.callLabel(null, "hook.start", false, Collections.EMPTY_MAP);

        //再等0.5秒
        try {
            Thread.sleep(500);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        initFuture.complete(1);
    }
}
