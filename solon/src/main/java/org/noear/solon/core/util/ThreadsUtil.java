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
    private static Method method_newVirtualThreadFactory;

    public static ExecutorService newVirtualThreadPerTaskExecutor() {
        try {
            if (method_newVirtualThreadPerTaskExecutor == null) {
                method_newVirtualThreadPerTaskExecutor = Executors.class.getDeclaredMethod("newVirtualThreadPerTaskExecutor");
            }

            return (ExecutorService) method_newVirtualThreadPerTaskExecutor.invoke(Executors.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static ThreadFactory newVirtualThreadFactory() {
        try {
            if (method_newVirtualThreadFactory == null) {
                method_newVirtualThreadFactory = Thread.class.getDeclaredMethod("ofVirtual");
            }

            Object ofVirtual = method_newVirtualThreadFactory.invoke(Thread.class);

            return (ThreadFactory) ofVirtual.getClass().getDeclaredMethod("factory").invoke(ofVirtual);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
