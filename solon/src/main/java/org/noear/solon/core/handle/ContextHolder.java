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
package org.noear.solon.core.handle;


import org.noear.solon.Solon;
import org.noear.solon.core.FactoryManager;

/**
 * 上下文状态处理工具（独立出来，可为别的业务服务）
 *
 * @see Solon#app()#tryHandle(Context)
 * @author noear
 * @since 1.0
 * @since 3.0
 * */
public class ContextHolder {
    private final static ThreadLocal<Context> threadLocal = FactoryManager.getGlobal().newThreadLocal(ContextHolder.class, false);

    /**
     * 设置当前线程的上下文
     * */
    public static void currentSet(Context context){
        threadLocal.set(context);
    }

    /**
     * 移除当前线程的上下文
     * */
    public static void currentRemove(){
        threadLocal.remove();
    }

    /**
     * 获取当前线程的上下文
     * */
    public static Context current(){
        Context tmp = threadLocal.get();

        if (tmp == null && Solon.cfg().testing()) {
            tmp = new ContextEmpty();
            threadLocal.set(tmp);
        }

        return tmp;
    }
}
