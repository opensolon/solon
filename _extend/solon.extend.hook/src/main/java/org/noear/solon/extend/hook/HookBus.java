package org.noear.solon.extend.hook;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author noear
 * @since 1.8
 */
public class HookBus {
    static Map<String, Set<HookHandler>> subsList = new HashMap<>();

    public static void onBefore(String name, Map<String, Object> args) {
        Set<HookHandler> subs = subsList.get(name);

        if (subs != null) {
            for (HookHandler handler : subs) {
                handler.onBefore(args);
            }
        }
    }

    public static void onAfter(String name, Map<String, Object> args) {
        Set<HookHandler> subs = subsList.get(name);

        if (subs != null) {
            for (HookHandler handler : subs) {
                handler.onAfter(args);
            }
        }
    }

    public static void subscribe(String name, HookHandler handler) {
        synchronized (name.intern()) {
            Set<HookHandler> subs = subsList.get(name);
            if (subs == null) {
                subs = new HashSet<>();
                subsList.put(name, subs);
            }

            subs.add(handler);
        }
    }
}
