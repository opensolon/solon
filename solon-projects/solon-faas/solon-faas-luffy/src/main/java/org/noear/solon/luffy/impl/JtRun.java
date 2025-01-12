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
package org.noear.solon.luffy.impl;

import org.noear.luffy.dso.JtBridge;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ContextHolder;

import java.util.concurrent.CompletableFuture;


/**
 * @author noear
 * @since 1.3
 */
public class JtRun {
    private static CompletableFuture<Integer> initFuture = new CompletableFuture<>();

    private static final JtFunctionLoaderManager resouceLoader = new JtFunctionLoaderManager();
    private static JtExecutorAdapter jtAdapter;

    public static void init() {
        if (jtAdapter == null) {
            resouceLoader.add(0, new JtFunctionLoaderClasspath());

            //添加外部扩展的资源加载器
            Solon.context().subBeansOfType(JtFunctionLoader.class, bean -> {
                resouceLoader.add(0, bean);
            });

            jtAdapter = new JtExecutorAdapter(resouceLoader);
            JtBridge.executorAdapterSet(jtAdapter);
            JtBridge.configAdapterSet(jtAdapter);
        }
    }

    /**
     * 获取资源加载器（可以清空并替换为数据库的）
     */
    public static JtFunctionLoaderManager getResouceLoader() {
        return resouceLoader;
    }

    public static AFileModel fileGet(String path) throws Exception {
        return jtAdapter.fileGet(path);
    }

    /**
     * 删除缓存
     */
    public static void dele(String path) throws Exception {
        //如果有更新，移除缓存
        String name = path.replace("/", "__");
        ExecutorFactory.del(name);
    }

    /**
     * 调用函数
     */
    public static Object call(String path) throws Exception {
        return call(path, ContextEmpty.create());
    }

    /**
     * 调用函数
     */
    public static Object call(String path, Context ctx) throws Exception {
        AFileModel file = JtBridge.fileGet(path);

        return ExecutorFactory.execOnly(file, ctx);
    }

    /**
     * 执行函数
     */
    public static void exec(String path) throws Exception {
        exec(path, ContextEmpty.create());
    }

    /**
     * 执行函数
     */
    public static void exec(String path, Context ctx) throws Exception {
        AFileModel file = JtBridge.fileGet(path);

        ExecutorFactory.execOnly(file, ctx);
    }

    /**
     * 执行代码
     */
    public static void execCode(String code) throws Exception {
        AFileModel file = new AFileModel();
        file.path = Utils.md5(code);
        file.content = code;
        file.edit_mode = "javascript";

        execFile(file);
    }

    /**
     * 执行文件
     */
    public static void execFile(AFileModel file) throws Exception {
        initFuture.get();

        Context ctx = ContextHolder.current();

        if (ctx == null) {
            ctx = ContextEmpty.create();

            ContextHolder.currentSet(ctx);
            ExecutorFactory.execOnly(file, ctx);
            ContextHolder.currentRemove();
        } else {
            ExecutorFactory.execOnly(file, ctx);
        }
    }

    public static void xfunInit() {
        //再等0.5秒 //执行引擎需要加载东西
        try {
            Thread.sleep(500);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        initFuture.complete(1);
    }
}
