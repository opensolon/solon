package org.noear.solon.data.tran.impl;

import org.noear.solon.data.tran.TranNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据事务节点（用于生成事务树）
 *
 * @author noear
 * @since 1.0
 * */
public abstract class DbTranNode implements TranNode {
    static final Logger log = LoggerFactory.getLogger(DbTranNode.class);

    /**
     * 父节点
     * */
    protected DbTranNode parent;
    /**
     * 孩子节点
     * */
    protected List<DbTranNode> children = new ArrayList<>();

    /**
     * 添加孩子节点
     * */
    @Override
    public void add(TranNode slave) {
        if (slave instanceof DbTranNode) {
            DbTranNode node = (DbTranNode) slave;

            node.parent = this;
            this.children.add(node);
        }
    }

    /**
     * 提交
     * */
    public void commit() throws Throwable {
        for (DbTranNode n1 : children) {
            n1.commit();
        }
    }

    /**
     * 回滚
     * */
    public void rollback() throws Throwable {
        //确保每个子处事，都有机会回滚
        //
        for (DbTranNode n1 : children) {
            try {
                n1.rollback();
            } catch (Throwable e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * 关闭
     * */
    public void close() throws Throwable {
        //确保每个子处事，都有机会关闭
        //
        for (DbTranNode n1 : children) {
            try {
                n1.close();
            } catch (Throwable e) {
                log.warn(e.getMessage(), e);
            }
        }
    }
}
