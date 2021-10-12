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
public class RedisSession {
    private static final String SET_SUCCEED = "OK";


    protected RedisSession(Jedis c) {
        client = c;
    }

    //临时寄存器
    public Object value;

    private Jedis client;
    private String _key;
    private long _seconds;

    public RedisSession key(String key) {
        _key = key;
        return this;
    }

    public RedisSession expire(int seconds) {
        _seconds = seconds;
        return this;
    }

    //?表示1+, *表示0+ //可实现分页的效果
    public List<String> scan(String keyPattern, int count) {
        String cursor = ScanParams.SCAN_POINTER_START;

        ScanParams p = new ScanParams();
        p.count(count);
        p.match(keyPattern);

        return client.scan(cursor, p).getResult();
    }

    public boolean match(String keyPattern) {
        List<String> temp = scan(keyPattern, 1);
        return (temp != null && temp.size() > 0);
    }

    public Boolean exists() {
        return client.exists(_key);
    }

    public Boolean delete() {
        return client.del(_key) > 0;
    }

    public void rename(String newKey) {
        client.rename(_key, newKey);
    }

    private boolean _close = false;

    public void close() {
        if (_close) {
            return;
        }

        client.close();
        _close = true;
    }


    private void reset_expire() {
        if (_seconds > 0) {
            client.expire(_key, _seconds);
        }

        if (_seconds == -1L) {
            client.expire(_key, -1L); //马上消失
        }

        if(_seconds == -2L){
            client.persist(_key); //永久有效
        }
    }

    /* 设置时间进行 */
    public void delay() {
        reset_expire();
    }


    //------
    //value::
    public RedisSession valSet(String val) {
        client.set(_key, val);
        reset_expire();

        return this;
    }

    public String val() {
        return client.get(_key);
    }


    public int valAsInt() {
        String temp = val();
        if (Utils.isEmpty(temp)) {
            return 0;
        } else {
            return Integer.parseInt(temp);
        }
    }

    public long valAsLong() {
        String temp = val();
        if (Utils.isEmpty(temp)) {
            return 0L;
        } else {
            return Long.parseLong(temp);
        }
    }

    public float valAsFloat() {
        String temp = val();
        if (Utils.isEmpty(temp)) {
            return 0.0F;
        } else {
            return Float.parseFloat(temp);
        }
    }

    public double valAsDouble() {
        String temp = val();
        if (Utils.isEmpty(temp)) {
            return 0.0D;
        } else {
            return Double.parseDouble(temp);
        }
    }


    //获取多个key的值
    public List<String> valMore(String... keys) {
        return client.mget(keys);
    }


    //或许不开放为好
    public long incr(long num) {
        long val = client.incrBy(_key, num);
        reset_expire();

        return val;
    }

    public boolean lock(String val) {
        /**
         * NX: IF_NOT_EXIST（只在键不存在时，才对键进行设置操作）
         * XX: IF_EXIST（只在键已经存在时，才对键进行设置操作）
         *
         * EX: SET_WITH_EXPIRE_TIME for second
         * PX: SET_WITH_EXPIRE_TIME for millisecond
         * */

        SetParams options = new SetParams().nx().ex(_seconds);
        String rst = client.set(_key, val, options); //设置成功，返回 1 。//设置失败，返回 0 。

        return SET_SUCCEED.equals(rst);//成功获得锁
    }

    public boolean lock() {
        return lock(System.currentTimeMillis() + "");
    }


    //--------
    //hash::

    public Boolean hashHas(String field) {
        return client.hexists(_key, field);
    }

    //?表示1+, *表示0+ //可实现分页的效果
    public List<Map.Entry<String, String>> hashScan(String fieldPattern, int count) {
        String cursor = ScanParams.SCAN_POINTER_START;

        ScanParams p = new ScanParams();
        p.count(count);
        p.match(fieldPattern);

        return client.hscan(_key, cursor, p).getResult();
    }

    public boolean hashMatch(String fieldPattern) {
        List<Map.Entry<String, String>> temp = hashScan(fieldPattern, 1);

        return (temp != null && temp.size() > 0);
    }

    public long hashDel(String... fields) {
        return client.hdel(_key, fields);
    }

    public RedisSession hashSet(String field, String val) {
        client.hset(_key, field, val);
        reset_expire();

        return this;
    }

    public RedisSession hashSet(String field, long val) {
        client.hset(_key, field, val + "");
        reset_expire();

        return this;
    }

    public RedisSession hashSetAll(Map<String, String> map) {
        Pipeline pip = client.pipelined();

        map.forEach((k, v) -> {
            pip.hset(_key, k, v);
        });

        pip.sync();

        reset_expire();

        return this;
    }

    public long hashIncr(String field, long num) {
        long val = client.hincrBy(_key, field, num);
        reset_expire();

        return val;
    }

    public String hashGet(String field) {
        return client.hget(_key, field);
    }

    //获取多个字段的值
    public List<String> hashGetMore(String... fields) {
        return client.hmget(_key, fields);
    }

    public long hashVal(String field) {
        String temp = client.hget(_key, field);

        if (Utils.isEmpty(temp))
            return 0;
        else
            return Long.parseLong(temp);
    }

    public RedisHashMap hashGetAll() {
        return new RedisHashMap(client.hgetAll(_key));
    }

    public long hashLen() {
        return client.hlen(_key);
    }


    //------------------
    //list::

    public RedisSession listAdd(String val) {
        client.lpush(_key, val); //左侧压进
        reset_expire();

        return this;
    }

    /**
     * count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。
     * count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。
     * count = 0 : 移除表中所有与 VALUE 相等的值。
     */
    public RedisSession listDel(String val, int count) {
        client.lrem(_key, count, val); //左侧压进
        reset_expire();

        return this;
    }

    public RedisSession listDel(String val) {
        return listDel(val, 0);
    }

    public RedisSession listAddRange(Collection<String> vals) {
        Pipeline pip = client.pipelined();
        for (String val : vals) {
            pip.lpush(_key, val); //左侧压进
        }
        pip.sync();

        reset_expire();

        return this;
    }

    public String listPop() {
        return client.rpop(_key); //右侧推出
    }

    public String listPeek() {
        return listGet(0); //右侧推出
    }

    /**
     * 先进先出（即从right取）
     */
    public String listGet(int index) {
        return client.lindex(_key, index);
    }

    /**
     * 先进先出（即从right取）
     */
    public List<String> listGet(int start, int end) {
        return client.lrange(_key, start, end);
    }

    public long listLen() {
        return client.llen(_key);
    }

    //------------------
    //Sset::
    public RedisSession setAdd(String val) {
        client.sadd(_key, val); //左侧压进
        reset_expire();

        return this;
    }

    public RedisSession setDel(String val) {
        client.srem(_key, val); //左侧压进
        reset_expire();

        return this;
    }

    public RedisSession setAddRange(Collection<String> vals) {
        Pipeline pip = client.pipelined();
        for (String val : vals) {
            pip.sadd(_key, val); //左侧压进
        }
        pip.sync();

        reset_expire();

        return this;
    }

    public long setLen() {
        return client.scard(this._key);
    }

    public String setPop() {
        return client.spop(_key); //右侧推出
    }

    public List<String> setGet(int count) {
        return client.srandmember(_key, count);
    }

    public List<String> setScan(String valPattern, int count) {
        String cursor = ScanParams.SCAN_POINTER_START;

        ScanParams p = new ScanParams();
        p.count(count);
        p.match(valPattern);

        return client.sscan(_key, cursor, p).getResult();
    }

    public boolean setMatch(String valPattern) {
        List<String> temp = setScan(valPattern, 1);
        return (temp != null && temp.size() > 0);
    }

    //------------------
    //Sort set::
    public RedisSession zsetAdd(double score, String val) {
        client.zadd(_key, score, val);
        reset_expire();

        return this;
    }

    public void zsetDel(String... vals) {
        client.zrem(_key, vals);
    }

    public long zsetLen() {
        return client.zcard(_key);
    }

    public Set<String> zsetGet(long start, long end) {
        return client.zrange(_key, start, end);
    }


    public long zsetIdx(String val) {
        Long tmp = client.zrank(_key, val);
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

        return client.zscan(_key, cursor, p).getResult();
    }

    public boolean zsetMatch(String valPattern) {
        List<Tuple> temp = zsetScan(valPattern, 1);
        return (temp != null && temp.size() > 0);
    }


    public long publish(String channel, String message) {
        return client.publish(channel, message);
    }

    public void subscribe(JedisPubSub jedisPubSub, String... channels){
        client.subscribe(jedisPubSub,channels);
    }
}
