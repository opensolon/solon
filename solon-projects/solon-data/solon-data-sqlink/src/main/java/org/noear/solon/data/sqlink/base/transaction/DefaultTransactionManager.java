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
package org.noear.solon.data.sqlink.base.transaction;

import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;

/**
 * 默认事务控制器管理者
 *
 * @author kiryu1223
 * @since 3.0
 */
public class DefaultTransactionManager implements TransactionManager {
    protected final DataSourceManager dataSourceManager;
    protected final ThreadLocal<Transaction> curTransaction = new ThreadLocal<>();

    public DefaultTransactionManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    @Override
    public Transaction get(Integer isolationLevel) {
        if (currentThreadInTransaction()) {
            throw new RuntimeException("不支持多重事务");
        }
        DefaultTransaction defaultTransaction = new DefaultTransaction(isolationLevel, dataSourceManager.getDataSource(), this);
        curTransaction.set(defaultTransaction);
        return defaultTransaction;
    }

    @Override
    public void remove() {
        curTransaction.remove();
    }

    @Override
    public Transaction getCurTransaction() {
        return curTransaction.get();
    }

    @Override
    public boolean currentThreadInTransaction() {
        return isOpenTransaction();
    }

    @Override
    public boolean isOpenTransaction() {
        return curTransaction.get() != null;
    }
}
