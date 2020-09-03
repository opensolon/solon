package org.noear.solon.core;

import org.noear.solon.annotation.XTran;
import org.noear.solon.ext.RunnableEx;

import javax.sql.DataSource;
import java.sql.Connection;

public interface TranExecutor {
    void execute(XTran anno, RunnableEx runnable) throws Throwable;
}
