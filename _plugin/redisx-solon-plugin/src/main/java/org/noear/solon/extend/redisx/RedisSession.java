package org.noear.solon.extend.redisx;

import org.noear.solon.Utils;
import redis.clients.jedis.*;
import redis.clients.jedis.params.SetParams;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis 会话
 *
 * @author noear
 * @since 1.5
 */
public class RedisSession implements AutoCloseable {
    private static final String LOCK_SUCCEED = "OK";

    private final Jedis jedis;

    protected RedisSession(Jedis jedis) {
        this.jedis = jedis;
    }

    private boolean _close = false;

    /**
     * 关闭会话
     * */
    @Override
    public void close() throws Exception {
        if (_close) {
            return;
        }

        jedis.close();
        _close = true;
    }

    private String _key;
    private long _seconds;

    /**
     * 主键
     * */
    public RedisSession key(String key) {
        _key = key;
        return this;
    }

    /**
     * 设置失效时间
     *
     * @param seconds 秒数
     * */
    public RedisSession expire(int seconds) {
        _seconds = seconds;
        return this;
    }

    private void expirePush() {
        if (_seconds > 0) {
            jedis.expire(_key, _seconds);
        }

        if (_seconds == -1L) {
            jedis.expire(_key, -1L); //马上消失
        }

        if (_seconds == -2L) {
            jedis.persist(_key); //永久有效
        }
    }



    /**
     * 尝试延期
     * */
    public void delay() {
        expirePush();
    }

    /**
     * 主键扫描
     *
     * @param keyPattern 模式（?表示1+, *表示0+）
     * */
    public List<String> scan(String keyPattern, int count) {
        String cursor = ScanParams.SCAN_POINTER_START;

        ScanParams p = new ScanParams();
        p.count(count);
        p.match(keyPattern);

        return jedis.scan(cursor, p).getResult();
    }

    /**
     * 主键匹配
     *
     * @param keyPattern 模式（?表示1+, *表示0+）
     * */
    public boolean match(String keyPattern) {
        List<String> temp = scan(keyPattern, 1);
        return (temp != null && temp.size() > 0);
    }

    /**
     * 主键是否存在
     * */
    public Boolean exists() {
        return jedis.exists(_key);
    }

    /**
     * 主键删除
     * */
    public Boolean delete() {
        return jedis.del(_key) > 0;
    }

    /**
     * 主键重命名
     * */
    public void rename(String newKey) {
        jedis.rename(_key, newKey);
    }


    //------
    //value::

    /**
     * 设置主键对应的值
     * */
    public RedisSession set(String val) {
        jedis.set(_key, val);
        expirePush();

        return this;
    }

    /**
     * 设置主键对应的值
     * */
    public RedisSession set(long val) {
        return set(String.valueOf(val));
    }

    /**
     * 获取主键对应的值
     * */
    public String get() {
        return jedis.get(_key);
    }

    /**
     * 获取主键对应的值，并转为长整型
     * */
    public long getAsLong() {
        String temp = get();
        if (Utils.isEmpty(temp)) {
            return 0L;
        } else {
            return Long.parseLong(temp);
        }
    }


    /**
     * 获取多个主键值
     * */
    public List<String> getMore(String... keys) {
        return jedis.mget(keys);
    }


    /**
     * 主键对应的值，原子增量
     * */
    public long incr(long num) {
        long val = jedis.incrBy(_key, num);
        expirePush();

        return val;
    }

    public long incr() {
        long val = jedis.incr(_key);
        expirePush();

        return val;
    }

    public long decr() {
        long val = jedis.decr(_key);
        expirePush();

        return val;
    }

    /**
     * 主键尝试锁一个值
     * */
    public boolean lock(String val) {
        /**
         * NX: IF_NOT_EXIST（只在键不存在时，才对键进行设置操作）
         * XX: IF_EXIST（只在键已经存在时，才对键进行设置操作）
         *
         * EX: SET_WITH_EXPIRE_TIME for second
         * PX: SET_WITH_EXPIRE_TIME for millisecond
         * */

        SetParams options = new SetParams().nx().ex(_seconds);
        String rst = jedis.set(_key, val, options); //设置成功，返回 1 。//设置失败，返回 0 。

        return LOCK_SUCCEED.equals(rst);//成功获得锁
    }

    /**
     * 主键尝试锁
     * */
    public boolean lock() {
        return lock(System.currentTimeMillis() + "");
    }


    //--------
    //hash::

    public Boolean hashHas(String field) {
        return jedis.hexists(_key, field);
    }

    /**
     * 哈希字段扫描
     *
     * @param fieldPattern 字段模式（?表示1+, *表示0+）
     * */
    public List<Map.Entry<String, String>> hashScan(String fieldPattern, int count) {
        String cursor = ScanParams.SCAN_POINTER_START;

        ScanParams p = new ScanParams();
        p.count(count);
        p.match(fieldPattern);

        return jedis.hscan(_key, cursor, p).getResult();
    }

    /**
     * 哈希字段匹配
     * */
    public boolean hashMatch(String fieldPattern) {
        List<Map.Entry<String, String>> temp = hashScan(fieldPattern, 1);

        return (temp != null && temp.size() > 0);
    }

    /**
     * 哈希字段删除
     * */
    public long hashDel(String... fields) {
        return jedis.hdel(_key, fields);
    }

    /**
     * 哈希字段设置
     * */
    public RedisSession hashSet(String field, String val) {
        jedis.hset(_key, field, val);
        expirePush();

        return this;
    }

    /**
     * 哈希字段设置
     * */
    public RedisSession hashSet(String field, long val) {
        return hashSet(field, String.valueOf(val));
    }

    /**
     * 哈希字段批量设置（管道模式操作）
     * */
    public RedisSession hashSetAll(Map<String, String> map) {
        Pipeline pip = jedis.pipelined();

        map.forEach((k, v) -> {
            pip.hset(_key, k, v);
        });

        pip.sync();

        expirePush();

        return this;
    }

    /**
     * 哈希字段增量操作
     * */
    public long hashIncr(String field, long num) {
        long val = jedis.hincrBy(_key, field, num);
        expirePush();

        return val;
    }

    /**
     * 哈希字段获取
     * */
    public String hashGet(String field) {
        return jedis.hget(_key, field);
    }

    /**
     * 哈希字段获取并转为长整型
     * */
    public long hashGetAsLong(String field) {
        String temp = hashGet(field);

        if (Utils.isEmpty(temp))
            return 0;
        else
            return Long.parseLong(temp);
    }

    /**
     * 哈希字段多个获取
     * */
    public List<String> hashGetMore(String... fields) {
        return jedis.hmget(_key, fields);
    }

    /**
     * 哈希获取所有字段
     * */
    public Map<String, String> hashGetAll() {
        return jedis.hgetAll(_key);
    }

    /**
     * 哈希长度
     * */
    public long hashLen() {
        return jedis.hlen(_key);
    }


    //------------------
    //list::

    /**
     * 列表添加项
     * */
    public RedisSession listAdd(String item) {
        jedis.lpush(_key, item); //左侧压进
        expirePush();

        return this;
    }

    /**
     * 列表添加项
     * */
    public RedisSession listAdd(long item) {
        return listAdd(String.valueOf(item));
    }

    /**
     * 列表删除项
     *
     * count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。
     * count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。
     * count = 0 : 移除表中所有与 VALUE 相等的值。
     */
    public RedisSession listDel(String item, int count) {
        jedis.lrem(_key, count, item); //左侧压进
        expirePush();

        return this;
    }

    /**
     * 列表删除项
     * */
    public RedisSession listDel(String item) {
        return listDel(item, 0);
    }

    /**
     * 列表批量添加项
     * */
    public RedisSession listAddRange(Collection<String> items) {
        Pipeline pip = jedis.pipelined();
        for (String val : items) {
            pip.lpush(_key, val); //左侧压进
        }
        pip.sync();

        expirePush();

        return this;
    }

    /**
     * 列表冒出
     * */
    public String listPop() {
        return jedis.rpop(_key); //右侧推出
    }

    /**
     * 列表预览
     * */
    public String listPeek() {
        return listGet(0); //右侧推出
    }

    /**
     * 列表获取项（先进先出，从right 取）
     */
    public String listGet(int index) {
        return jedis.lindex(_key, index); //从right取
    }

    /**
     * 列表分页获取项（先进先出，从right取）
     */
    public List<String> listGetRange(int start, int end) {
        return jedis.lrange(_key, start, end);
    }

    /**
     * 列表长度
     * */
    public long listLen() {
        return jedis.llen(_key);
    }

    //------------------
    //Sset::
    public RedisSession setAdd(String val) {
        jedis.sadd(_key, val); //左侧压进
        expirePush();

        return this;
    }

    public RedisSession setDel(String val) {
        jedis.srem(_key, val); //左侧压进
        expirePush();

        return this;
    }

    public RedisSession setAddRange(Collection<String> vals) {
        Pipeline pip = jedis.pipelined();
        for (String val : vals) {
            pip.sadd(_key, val); //左侧压进
        }
        pip.sync();

        expirePush();

        return this;
    }

    public long setLen() {
        return jedis.scard(this._key);
    }

    public String setPop() {
        return jedis.spop(_key); //右侧推出
    }

    public List<String> setGet(int count) {
        return jedis.srandmember(_key, count);
    }

    public List<String> setScan(String valPattern, int count) {
        String cursor = ScanParams.SCAN_POINTER_START;

        ScanParams p = new ScanParams();
        p.count(count);
        p.match(valPattern);

        return jedis.sscan(_key, cursor, p).getResult();
    }

    public boolean setMatch(String valPattern) {
        List<String> temp = setScan(valPattern, 1);
        return (temp != null && temp.size() > 0);
    }

    //------------------
    //Sort set::
    public RedisSession zsetAdd(double score, String val) {
        jedis.zadd(_key, score, val);
        expirePush();

        return this;
    }

    public void zsetDel(String... vals) {
        jedis.zrem(_key, vals);
    }

    public long zsetLen() {
        return jedis.zcard(_key);
    }

    public Set<String> zsetGet(long start, long end) {
        return jedis.zrange(_key, start, end);
    }


    public long zsetIdx(String val) {
        Long tmp = jedis.zrank(_key, val);
        if (tmp == null) {
            return -1;
        } else {
            return tmp;
        }
    }

    public List<Tuple> zsetScan(String valPattern, int count) {
        String cursor = ScanParams.SCAN_POINTER_START;

        ScanParams p = new ScanParams();
        p.count(count);
        p.match(valPattern);

        return jedis.zscan(_key, cursor, p).getResult();
    }

    public boolean zsetMatch(String valPattern) {
        List<Tuple> temp = zsetScan(valPattern, 1);
        return (temp != null && temp.size() > 0);
    }

    //------------------
    //message::


    public long publish(String channel, String message) {
        return jedis.publish(channel, message);
    }

    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        jedis.subscribe(jedisPubSub, channels);
    }
}
