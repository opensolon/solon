package org.noear.solon.extend.beetlsql;

import org.beetl.sql.core.SQLManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQLManager包装
 *
 * @author noear
 * */
public class SQLManagerHolder {
    public final SQLConnectionSource connectionSource;
    public final SQLManager sqlManager;

    public SQLManagerHolder(DataSource dataSource){
        connectionSource = new SQLConnectionSource(dataSource,null);
        sqlManager = SQLManager.newBuilder(connectionSource).build();
    }

    public <T> T getMapper(Class<T> mapperInterface){
        return sqlManager.getMapper(mapperInterface);
    }


    private final static Map<DataSource, SQLManagerHolder> cached = new ConcurrentHashMap<>();

    /**
     * 获取会话容器
     */
    public static SQLManagerHolder get(DataSource source) {
        SQLManagerHolder holder = cached.get(source);
        if (holder == null) {
            synchronized (cached) {
                holder = cached.get(source);
                if (holder == null) {
                    holder = new SQLManagerHolder(source);
                    cached.put(source, holder);
                }
            }
        }

        return holder;
    }
}
