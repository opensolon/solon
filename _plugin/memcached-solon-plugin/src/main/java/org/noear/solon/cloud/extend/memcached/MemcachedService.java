package org.noear.solon.cloud.extend.memcached;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.cache.CacheService;

import java.util.Properties;

public class MemcachedService implements CacheService {
    private String _cacheKeyHead;
    private int _defaultSeconds;

    private MemcachedClient _cache = null;

    public MemcachedService(Properties prop) {
        this(prop, prop.getProperty("keyHeader"), 0);
    }

    public MemcachedService(Properties prop, String keyHeader, int defSeconds) {
        String defSeconds_str = prop.getProperty("defSeconds");
        String server = prop.getProperty("server");
        String user = prop.getProperty("user");
        String password = prop.getProperty("password");

        if (defSeconds == 0) {
            defSeconds = (defSeconds_str == null ? 60 * 1 : Integer.parseInt(defSeconds_str));
        }

        init0(keyHeader, defSeconds, server, user, password);
    }

    public MemcachedService(String keyHeader, int defSeconds, String server, String user, String password) {
        init0(keyHeader, defSeconds, server, user, password);
    }

    private void init0(String keyHeader, int defSeconds, String server, String user, String password) {
        _cacheKeyHead = keyHeader;
        _defaultSeconds = defSeconds;

        if (_defaultSeconds < 10) {
            _defaultSeconds = 60;
        }

        if(Utils.isEmpty(_cacheKeyHead)){
            _cacheKeyHead = Solon.cfg().appName();
        }

        try {
            if (Utils.isEmpty(user) || Utils.isEmpty(password)) {

                _cache = new MemcachedClient(new ConnectionFactoryBuilder()
                        .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY).build(),
                        AddrUtil.getAddresses(server));
            }else{
                AuthDescriptor ad = new AuthDescriptor(new String[]{"PLAIN"},
                        new PlainCallbackHandler(user, password));

                _cache = new MemcachedClient(new ConnectionFactoryBuilder()
                        .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                        .setAuthDescriptor(ad).build(),
                        AddrUtil.getAddresses(server));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        if (_cache != null) {
            if(seconds == 0){
                seconds = _defaultSeconds;
            }

            String newKey = newKey(key);
            try {
                _cache.set(newKey, seconds, obj);
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public Object get(String key) {
        if (_cache != null) {
            String newKey = newKey(key);
            try {
                return _cache.get(newKey);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void remove(String key) {
        if (_cache != null) {
            String newKey = newKey(key);
            _cache.delete(newKey);
        }
    }


    private String newKey(String key) {
        return _cacheKeyHead + "$" + Utils.md5(key);
    }
}
