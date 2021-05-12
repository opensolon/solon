package org.noear.solon.cache.jedis;

import redis.clients.jedis.*;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

class RedisX {
    private JedisPool _jedisPool;

    public RedisX(String server, String password, int db, int maxTotaol, long maxWaitMillis) {
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

    private RedisUsing doOpen(){
        Jedis jx = _jedisPool.getResource();
        return new RedisUsing(jx);
    }

    public void open0(Consumer<RedisUsing> using){
        RedisUsing ru = doOpen();

        try {
            using.accept(ru);
        }finally {
            ru.close();
        }
    }

    public <T> T open1(Function<RedisUsing,T> using){
        RedisUsing ru = doOpen();

        T temp;
        try {
            temp = using.apply(ru);
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
    }
}
