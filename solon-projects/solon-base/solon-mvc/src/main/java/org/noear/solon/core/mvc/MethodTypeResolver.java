/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core.mvc;

import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;

import java.util.Set;
import java.util.function.Predicate;

/**
 * MethodType 分析器
 *
 * @author noear
 * @since 2.7
 */
public class MethodTypeResolver {

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

        if (checker.test(Message.class)) {
            list.add(MethodType.MESSAGE);
        }

        return list;
    }
}
