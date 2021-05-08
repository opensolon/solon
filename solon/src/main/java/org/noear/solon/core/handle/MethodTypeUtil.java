package org.noear.solon.core.handle;

import org.noear.solon.annotation.*;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author noear
 * @since 1.3
 */
public class MethodTypeUtil {
    public static List<MethodType> findAndFill(List<MethodType> list, Predicate<Class> checker) {
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

        return list;
    }
}
