package org.noear.solon.core;

import org.noear.solon.annotation.XTran;
import org.noear.solon.ext.RunnableEx;

public interface TranExecutor {
    void execute(XTran anno, RunnableEx runnable) throws Throwable;
}
