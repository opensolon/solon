package org.noear.solon.data.sqlink.base.transaction;

public interface TransactionManager
{
    Transaction get(Integer isolationLevel);

    void remove();

    Transaction getCurTransaction();

    boolean currentThreadInTransaction();

    boolean isOpenTransaction();
}
