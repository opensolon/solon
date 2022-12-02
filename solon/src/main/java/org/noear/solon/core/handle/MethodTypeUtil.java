package org.noear.solon.core.handle;

import org.noear.solon.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author noear
 * @since 1.3
 */
public class MethodTypeUtil {
    static final Map<String, MethodType> enumMap = new HashMap<>();

    static {
        MethodType[] enumOrdinal = MethodType.class.getEnumConstants();

        for (int i = 0; i < enumOrdinal.length; ++i) {
            MethodType e = enumOrdinal[i];

            enumMap.put(e.name(), e);
        }
    }

    public static MethodType valueOf(String name) {
        MethodType tmp = enumMap.get(name);

        if (tmp == null) {
            return MethodType.UNKNOWN;
        } else {
            return tmp;
        }
    }

    public static Set<MethodType> findAndFill(Set<MethodType> list, Predicate<Class> checker) {
        if (checker.test(Get.class)) {
            list.add(MethodType.GET);
        }

        if (checker.test(Post.class)) {
            list.add(MethodType.POST);
        }

        if (checker.test(Put.class)) {
            list.add(MethodType.PUT);
        }

        if (checker.test(Patch.class)) {
            list.add(MethodType.PATCH);
        }

        if (checker.test(Delete.class)) {
            list.add(MethodType.DELETE);
        }

        if (checker.test(Head.class)) {
            list.add(MethodType.HEAD);
        }

        if (checker.test(Options.class)) {
            list.add(MethodType.OPTIONS);
        }

        if (checker.test(Http.class)) {
            list.add(MethodType.HTTP);
        }

        if (checker.test(Socket.class)) {
            list.add(MethodType.SOCKET);
        }

        if (checker.test(WebSocket.class)) {
            list.add(MethodType.WEBSOCKET);
        }

        return list;
    }
}
