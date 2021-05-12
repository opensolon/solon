package org.noear.solon.cache.spymemcached;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.cache.CacheService;

import java.io.IOException;
import java.util.Properties;

public class MemCacheService implements CacheService {
    private String _cacheKeyHead;
    private int _defaultSeconds;

    private MemcachedClient _cache = null;

    public MemCacheService(Properties prop) {
        this(prop, prop.getProperty("keyHeader"), 0);
    }

    public MemCacheService(Properties prop, String keyHeader, int defSeconds) {
        String defSeconds_str = prop.getProperty("defSeconds");
        String server = prop.getProperty("server");
        String user = prop.getProperty("user");
        String password = prop.getProperty("password");

        if (defSeconds == 0) {
            if(Utils.isEmpty(defSeconds_str) == false){
                defSeconds = Integer.parseInt(defSeconds_str);
            }
        }

        init0(keyHeader, defSeconds, server, user, password);
    }

    protected void init0(String keyHeader, int defSeconds, String server, String user, String password) {
        _cacheKeyHead = keyHeader;
        _defaultSeconds = defSeconds;

        if (_defaultSeconds < 3) {
            _defaultSeconds = 30;
        }

        if (Utils.isEmpty(_cacheKeyHead)) {
            _cacheKeyHead = Solon.cfg().appName();
        }

        ConnectionFactoryBuilder builder = new ConnectionFactoryBuilder();
        builder.setProtocol(ConnectionFactoryBuilder.Protocol.BINARY);

        try {
            if (Utils.isNotEmpty(user) && Utils.isNotEmpty(password)) {
                AuthDescriptor ad = new AuthDescriptor(new String[]{"PLAIN"},
                        new PlainCallbackHandler(user, password));

                builder.setAuthDescriptor(ad);
            }

            _cache = new MemcachedClient(builder.build(), AddrUtil.getAddresses(server));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        if (_cache != null) {
            String newKey = newKey(key);
            try {
                if(seconds > 0) {
                    _cache.set(newKey, seconds, obj);
                }else{
                    _cache.set(newKey, _defaultSeconds, obj);
                }
            } catch (Exception ex) {
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
