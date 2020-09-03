package org.noear.solon.extend.data.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.TranManager;
import org.noear.solon.extend.data.TranMeta;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class DbTran extends DbTranNode implements Tran {
    private final TranMeta meta;
    private final Map<DataSource, Connection> conMap = new HashMap<>();

    public Connection getConnection(DataSource ds) throws SQLException {
        if (conMap.containsKey(ds)) {
            return conMap.get(ds);
        } else {
            Connection con = ds.getConnection();
            con.setAutoCommit(false);
            if (meta.isolation().level > 0) {
                con.setTransactionIsolation(meta.isolation().level);
            }

            conMap.putIfAbsent(ds, con);
            return con;
        }
    }

    public DbTran(TranMeta meta) {
        this.meta = meta;
    }

    public void execute(RunnableEx runnable) throws Throwable {
        try {
            //conMap 此时，还是空的
            //
            TranManager.currentSet(this);

            //conMap 会在run时产生
            //
            runnable.run();

            if (parent == null) {
                commit();
            }
        } catch (Throwable ex) {
            if (parent == null) {
                rollback();
            }

            throw ex;
        } finally {
            TranManager.currentRemove();

            if (parent == null) {
                close();
            }
        }
    }

    @Override
    public void commit() throws Throwable{
        super.commit();

        for(Map.Entry<DataSource,Connection> kv : conMap.entrySet()){
            kv.getValue().commit();
        }
    }

    @Override
    public void rollback() throws Throwable{
        super.rollback();
        for(Map.Entry<DataSource,Connection> kv : conMap.entrySet()){
            kv.getValue().rollback();
        }
    }

    @Override
    public void close() throws Throwable{
        super.close();
        for(Map.Entry<DataSource,Connection> kv : conMap.entrySet()){
            kv.getValue().close();
        }
    }
}
