package org.noear.solon.core;

/**
 * 事务工厂
 * */
public interface TranFactory {
    Tran create(TranAnno anno);
}
