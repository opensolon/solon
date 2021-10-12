package org.noear.solon.extend.redisx;

import org.noear.solon.Utils;
import org.noear.solon.extend.redisx.utils.RedisId;
import org.noear.solon.extend.redisx.utils.RedisLock;
import org.noear.solon.extend.redisx.utils.RedisQueue;
import org.noear.solon.extend.redisx.utils.RedisTopic;
import redis.clients.jedis.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Redis 会话管理类
 *
 * @author noear
 * @since 1.5
 */
 public class RedisClient {
    /**
     * NX: IF_NOT_EXIST（只在键不存在时，才对键进行设置操作）
     * XX: IF_EXIST（只在键已经存在时，才对键进行设置操作）
     * <p>
     * EX: SET_WITH_EXPIRE_TIME for second
     * PX: SET_WITH_EXPIRE_TIME for millisecond
     */
    private JedisPool _jedisPool;

    public RedisClient(Properties prop) {
        String db = prop.getProperty("db");

        if (Utils.isEmpty(db)) {
            throw new RuntimeException("RedisX:Properties lacks the db parameter!");
        }

        initDo(prop, Integer.parseInt(db), 0);
    }

    public RedisClient(Properties prop, int db) {
        initDo(prop, db, 0);
    }

    public RedisClient(Properties prop, int db, int maxTotaol) {
        initDo(prop, db, maxTotaol);
    }

    public RedisClient(String server, String user, String password, int db, int maxTotaol) {
        initDo(server, user, password, db, maxTotaol, 0L);
    }

    public RedisClient(String server, String user, String password, int db, int maxTotaol, long maxWaitMillis) {
        initDo(server, user, password, db, maxTotaol, maxWaitMillis);
    }

    private void initDo(Properties prop, int db, int maxTotaol) {
        String server = prop.getProperty("server");
        String user = prop.getProperty("user");
        String password = prop.getProperty("password");
        String maxWaitMillis = prop.getProperty("maxWaitMillis");
        String maxTotaolStr = prop.getProperty("maxTotaol");

        if (maxTotaol > 0) {
            initDo(server,
                    user,
                    password,
                    db,
                    maxTotaol,
                    (Utils.isEmpty(maxWaitMillis) ? 0L : Long.parseLong(maxWaitMillis))
            );
        } else {
            initDo(server,
                    user,
                    password,
                    db,
                    (Utils.isEmpty(maxTotaolStr) ? 0 : Integer.parseInt(maxTotaolStr)),
                    (Utils.isEmpty(maxWaitMillis) ? 0L : Long.parseLong(maxWaitMillis))
            );
        }
    }

    private void initDo(String server, String user, String password, int db, int maxTotaol, long maxWaitMillis) {
        JedisPoolConfig config = new JedisPoolConfig();

        if (db < 0) {
            db = 0;
        }

        if (maxTotaol < 20) {
            maxTotaol = 200;
        }

        int maxIdle = maxTotaol / 100;
        if (maxIdle < 5) {
            maxIdle = 5;
        }

        if(maxWaitMillis < 3000){
            maxWaitMillis = 3000;
        }

        config.setMaxTotal(maxTotaol);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);

        String[] ss = server.split(":");

        if ("".equals(password)) {
            password = null;
        }

        _jedisPool = new JedisPool(config, ss[0], Integer.parseInt(ss[1]), 3000, password, db);
    }

    private RedisSession doOpen() {
        Jedis jx = _jedisPool.getResource();
        return new RedisSession(jx);
    }

    public void open0(Consumer<RedisSession> using) {
        RedisSession session = doOpen();

        try {
            using.accept(session);
        } finally {
            session.close();
        }
    }

    public <T> T open1(Function<RedisSession, T> using) {
        RedisSession session = doOpen();

        T temp;
        try {
            temp = using.apply(session);
        } finally {
            session.close();
        }

        return temp;
    }

    ////////////////////
    public RedisLock getLock(String lockName){
        return new RedisLock(this, lockName);
    }

    public RedisQueue getQueue(String queueName){
        return new RedisQueue(this, queueName);
    }

    public RedisId getId(String idName){
        return new RedisId(this, idName);
    }

    public RedisTopic getTopic(String topicName){
        return new RedisTopic(this, topicName);
    }
}