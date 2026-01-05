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
package org.noear.solon.core.util;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 线程工具
 *
 * @author noear
 * @since 2.7
 */
public class ThreadsUtil {
    private static Method method_newVirtualThreadPerTaskExecutor;
    private static Method method_newThreadPerTaskExecutor;

    private static Method method_newVirtualOfVirtual;
    private static Method method_newVirtualFactory;
    private static Method method_newVirtualName;

    public static ExecutorService newVirtualThreadPerTaskExecutor() {
        return newVirtualThreadPerTaskExecutor(null);
    }

    public static ExecutorService newVirtualThreadPerTaskExecutor(String prefix) {
        try {
            if (method_newVirtualThreadPerTaskExecutor == null) {
                method_newVirtualThreadPerTaskExecutor = Executors.class.getDeclaredMethod("newVirtualThreadPerTaskExecutor");
                method_newThreadPerTaskExecutor = Executors.class.getDeclaredMethod("newThreadPerTaskExecutor", ThreadFactory.class);

            }

            if (prefix != null) {
                return (ExecutorService) method_newThreadPerTaskExecutor.invoke(Executors.class, newVirtualThreadFactory(prefix));
            } else {
                return (ExecutorService) method_newVirtualThreadPerTaskExecutor.invoke(Executors.class);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static ThreadFactory newVirtualThreadFactory() {
        return newVirtualThreadFactory("solon-");
    }

    public static ThreadFactory newVirtualThreadFactory(String prefix) {
        try {
            if (method_newVirtualOfVirtual == null) {
                method_newVirtualOfVirtual = Thread.class.getDeclaredMethod("ofVirtual");

                Class<?> threadBuilderClass = Class.forName("java.lang.Thread$Builder");
                method_newVirtualFactory = threadBuilderClass.getMethod("factory");
                method_newVirtualName = threadBuilderClass.getMethod("name", String.class, int.class);
            }

            Object ofVirtual = method_newVirtualOfVirtual.invoke(Thread.class);

            if (prefix != null) {
                ofVirtual = method_newVirtualName.invoke(ofVirtual, prefix, 0);
            }

            return (ThreadFactory) method_newVirtualFactory.invoke(ofVirtual);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
