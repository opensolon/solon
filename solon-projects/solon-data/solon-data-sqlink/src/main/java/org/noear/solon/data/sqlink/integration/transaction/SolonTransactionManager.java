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
package org.noear.solon.data.sqlink.integration.transaction;

import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.transaction.DefaultTransactionManager;
import org.noear.solon.data.sqlink.base.transaction.Transaction;
import org.noear.solon.data.tran.TranUtils;

/**
 * Solon环境下的事务管理器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class SolonTransactionManager extends DefaultTransactionManager {
    public SolonTransactionManager(DataSourceManager dataSourceManager) {
        super(dataSourceManager);
    }

    @Override
    public Transaction get(Integer isolationLevel) {
        if (currentThreadInTransaction()) {
            throw new RuntimeException("不支持多重事务");
        }
        SolonTransaction solonTransaction = new SolonTransaction(isolationLevel, dataSourceManager.getDataSource(), this);
        curTransaction.set(solonTransaction);
        return solonTransaction;
    }

    @Override
    public boolean currentThreadInTransaction() {
        return TranUtils.inTrans() || isOpenTransaction();
    }
}
