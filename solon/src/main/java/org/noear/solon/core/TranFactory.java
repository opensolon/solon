package org.noear.solon.core;

import org.noear.solon.annotation.XTran;

/**
 * 事务工厂
 * */
public interface TranFactory {
    Tran create(XTran anno);
}
