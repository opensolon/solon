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
package org.noear.solon.data.tran;

import org.noear.solon.core.FactoryManager;
import org.noear.solon.data.tran.impl.DbTran;

/**
 * 事务管理器
 *
 * @author noear
 * @since 1.0
 * */
public final class TranManager {
    private static final ThreadLocal<DbTran> _tl_tran = FactoryManager.getGlobal().newThreadLocal(TranManager.class, false);


    /**
     * 设置当前事务
     *
     * @param tran 事务
     */
    public static void currentSet(DbTran tran) {
        _tl_tran.set(tran);
    }

    /**
     * 获取当前事务
     */
    public static DbTran current() {
        return _tl_tran.get();
    }

    /**
     * 移移当前事务
     */
    public static void currentRemove() {
        _tl_tran.remove();
    }


    /**
     * 尝试挂起
     */
    public static DbTran trySuspend() {
        DbTran tran = current();

        if (tran != null) {
            currentRemove();
        }

        return tran;
    }

    /**
     * 尝试恢复
     */
    public static void tryResume(DbTran tran) {
        if (tran != null) {
            currentSet(tran);
        }
    }
}
