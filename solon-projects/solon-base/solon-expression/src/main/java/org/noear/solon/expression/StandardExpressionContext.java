package org.noear.solon.expression;

import org.noear.solon.core.util.NameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * @author noear
 * @since 3.1
 */
public class StandardExpressionContext implements ExpressionContext {
    private static final Logger log = LoggerFactory.getLogger(StandardExpressionContext.class);
    private Map<String, Object> map = new HashMap<>();

    public void put(String key, Object value) {
        map.put(key, value);
    }

    @Override
    public Object get(String key) {
        Object val;
        int dotIndex = key.indexOf('.');
        if (dotIndex < 0) {
            val = map.get(key);
        } else {
            AtomicReference<Object> valRef = new AtomicReference<>();
            splitTokens(key, token -> {
                if (valRef.get() == null) {
                    valRef.set(map.get(token));
                } else {
                    Object obj = valRef.get();
                    if (obj instanceof Map) {
                        obj = ((Map) obj).get(token);
                        valRef.set(obj);
                    } else {
                        String getName = NameUtil.getPropGetterName(token);
                        try {
                            obj = obj.getClass().getDeclaredMethod(getName).invoke(obj);
                            valRef.set(obj);
                        } catch (Throwable ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }

                return valRef.get() != null;
            });

            val = valRef.get();
        }

        if (val == null) {
            if (log.isDebugEnabled()) {
                log.debug("This context key is null: '{}'", key);
            }
        }

        return val;
    }

    private void splitTokens(String key, Function<String, Boolean> consumer) {
        int start = 0;
        for (int i = 0; i < key.length(); i++) {
            if (key.charAt(i) == '.') {
                String key2 = key.substring(start, i);
                if (consumer.apply(key2) == false) {
                    return;
                }
                start = i + 1;
            }
        }

        consumer.apply(key.substring(start));
    }
}