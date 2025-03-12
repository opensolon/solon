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
package org.noear.solon.data.tran.impl;

import org.noear.solon.core.util.RunnableEx;
import org.noear.solon.data.annotation.Transaction;
import org.noear.solon.data.tran.TranNode;

/**
 * 数据事务实现。支持当前事务，如果没有则创建一个新的（需要入栈）
 *
 * @author noear
 * @since 1.0
 * */
public class TranDbImpl extends DbTran implements TranNode {
    public TranDbImpl(Transaction meta) {
        super(meta);
    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        super.execute(() -> {
            runnable.run();
        });
    }
}
