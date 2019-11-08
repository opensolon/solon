package org.noear.solon.extend.sessionstate.redis;

import org.noear.solon.XUtil;
import redis.clients.jedis.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

class RedisX {
    private JedisPool _jedisPool;
    private static final Object lock = "";

    public RedisX(String server, String password, int db, int maxTotaol) {
        doinit(server, password, db, maxTotaol);
    }

    private void doinit(String server, String password, int db, int maxTotaol) {
        JedisPoolConfig config = new JedisPoolConfig();

        int maxIdle = maxTotaol / 5;
        if (maxIdle < 10) {
            maxIdle = 10;
        }

        config.setMaxTotal(maxTotaol);
        config.setMaxIdle(maxIdle);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);

        String[] ss = server.split(":");

        if ("".equals(password)) {
            password = null;
        }

        _jedisPool = new JedisPool(config, ss[0], Integer.parseInt(ss[1]), 200, password, db);
    }

    private RedisUsing doOpen(){
        Jedis jx = _jedisPool.getResource();
        return new RedisUsing(jx);
    }

    public void open0(Act1<RedisUsing> using){
        RedisUsing ru = doOpen();

        try {
            using.run(ru);
        }finally {
            ru.close();
        }
    }

    public <T> T open1(Fun1<RedisUsing,T> using){
        RedisUsing ru = doOpen();

        T temp;
        try {
            temp = using.run(ru);
        }finally {
            ru.close();
        }

        return temp;
    }


    public class RedisUsing{
        private RedisUsing(Jedis c){
            client = c;
        }

        //临时寄存器
        public Object value;

        private Jedis client;
        private String _key;
        private int _seconds;

        public RedisUsing key(String key){
            _key = key;
            return this;
        }

        public RedisUsing expire(int seconds) {
            _seconds = seconds;
            return this;
        }

        //?表示1+, *表示0+ //可实现分页的效果
        public List<String> scan(String keyPattern, int count){
            String cursor = ScanParams.SCAN_POINTER_START;

            ScanParams p = new ScanParams();
            p.count(count);
            p.match(keyPattern);

            return client.scan(cursor,p).getResult();
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


        private void reset_expire(){
            if(_seconds>0){
                client.expire(_key, _seconds);
            }

            if(_seconds<0){
                client.expire(_key, -1); //马上消失
            }
        }

        /* 设置时间进行 */
        public void delay(){
            reset_expire();
        }


        //------
        //value::

        public RedisUsing set(String val){
            client.set(_key, val);
            reset_expire();

            return this;
        }

        public String get(){
            return client.get(_key);
        }

        //获取多个key的值
        public List<String> getMore(String... keys){
            return client.mget(keys);
        }

        public long val(){
            String temp = client.get(_key);
            if(XUtil.isEmpty(temp)){
                return 0;
            }else{
                return Long.parseLong(temp);
            }
        }

        //或许不开放为好
        public long incr(long num){
            long val = client.incrBy(_key, num);
            reset_expire();

            return val;
        }

        public boolean lock(String val) {
            if(client.exists(_key)){
                return false;
            }else {
                long rst = client.setnx(_key, val);
                reset_expire();

                return rst > 0;//成功获得锁
            }
        }

        public boolean lock() {
            return lock(System.currentTimeMillis() + "");
        }


        //--------
        //hash::

        public Boolean hashHas(String field){
            return client.hexists(_key,field);
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
            List<Map.Entry<String, String>> temp = hashScan(fieldPattern,1);

            return (temp != null && temp.size() > 0);
        }

        public long hashDel(String... fields) {
            return client.hdel(_key, fields);
        }

        public RedisUsing hashSet(String field,String val){
            client.hset(_key,field,val);
            reset_expire();

            return this;
        }

        public RedisUsing hashSet(String field,long val){
            client.hset(_key,field,val+"");
            reset_expire();

            return this;
        }

        public RedisUsing hashSetAll( Map<String,String> map) {
            Pipeline pip = client.pipelined();

            map.forEach((k, v) -> {
                pip.hset(_key, k, v);
            });

            pip.sync();

            reset_expire();

            return this;
        }

        public long hashIncr(String field,long num){
            long val = client.hincrBy(_key,field, num);
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

            if(XUtil.isEmpty(temp))
                return 0;
            else
                return Long.parseLong(temp);
        }

        public Map<String,String> hashGetAll() {
            return client.hgetAll(_key);
        }

        public long hashLen(){
            return client.hlen(_key);
        }


        //------------------
        //list::

        public  RedisUsing listAdd(String val){
            client.lpush(_key, val); //左侧压进
            reset_expire();

            return this;
        }

        public  RedisUsing listAddRange(Collection<String> vals){
            Pipeline pip = client.pipelined();
            for(String  val: vals) {
                pip.lpush(_key, val); //左侧压进
            }
            pip.sync();

            reset_expire();

            return this;
        }

        public String listPop(){
            return client.rpop(_key); //右侧推出
        }

        public String listGet(int index){
            return client.lindex(_key,index);
        }

        public List<String> listGet(int start, int end){
            return client.lrange(_key,start,end);
        }

        public long listLen(){
            return client.llen(_key);
        }

        //------------------
        //Sset::
        public  RedisUsing setAdd(String val){
            client.sadd(_key, val); //左侧压进
            reset_expire();

            return this;
        }

        public  RedisUsing setAddRange(Collection<String>  vals){
            Pipeline pip = client.pipelined();
            for(String  val: vals) {
                pip.sadd(_key, val); //左侧压进
            }
            pip.sync();

            reset_expire();

            return this;
        }

        public long setLen() {
            return client.scard(this._key);
        }

        public String setPop(){
            return client.spop(_key); //右侧推出
        }

        public List<String> setGet(int count){
            return client.srandmember(_key,count);
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
        public  RedisUsing zsetAdd(double score,String val){
            client.zadd(_key, score, val);
            reset_expire();

            return this;
        }

        public long zsetLen(){
            return client.zcard(_key);
        }

        public Set<String> zsetGet(long start, long end){
            return client.zrange(_key,start, end);
        }

        public void zsetDel(String... vals){
            client.zrem(_key,vals);
        }

        public long zsetIdx(String val){
            return client.zrank(_key,val);
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
    }

    public interface Act1<T> {
        void run(T t);
    }

    public interface Fun1<T,R> {
        R run(T t);
    }
}