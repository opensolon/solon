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
import org.noear.solon.core.util.CallableTx;
import org.noear.solon.core.util.JavaUtil;
import org.noear.solon.core.util.ScopeLocal;

/**
 * 上下文状态处理工具（独立出来，可为别的业务服务）
 *
 * @see Solon#app()#tryHandle(Context)
 * @author noear
 * @since 1.0
 * @since 3.0
 * */
public class ContextHolder {
    private final static ScopeLocal<Context> SCOPE_LOCAL = FactoryManager.getGlobal().newScopeLocal(ContextHolder.class);


    /**
     * 使用当前域的上下文
     *
     * @since 3.7.4
     */
    public static void currentWith(Context context, Runnable runnable) {
        SCOPE_LOCAL.with(context, runnable);
    }

    /**
     * 使用当前域的上下文
     *
     * @since 3.7.4
     */
    public static <R, X extends Throwable> R currentWith(Context context, CallableTx<R, X> callable) throws X {
        return SCOPE_LOCAL.with(context, callable);
    }


    /**
     * 获取当前线域的上下文
     */
    public static Context current() {
        Context tmp = SCOPE_LOCAL.get();

        if (tmp == null && Solon.appIf(app -> app.cfg().testing())) {
            if (JavaUtil.JAVA_MAJOR_VERSION < 21) {
                tmp = new ContextEmpty();
                SCOPE_LOCAL.set(tmp);
            }
        }

        return tmp;
    }

    /// /////////////////

    /**
     * 设置当前域的上下文
     *
     * @deprecated 3.7.4 {@link #currentWith(Context, Runnable)}
     */
    @Deprecated
    public static void currentSet(Context context) {
        SCOPE_LOCAL.set(context);
    }

    /**
     * 移除当前域的上下文
     *
     * @deprecated 3.7.4 {@link #currentWith(Context, Runnable)}
     */
    @Deprecated
    public static void currentRemove() {
        SCOPE_LOCAL.remove();
    }
}