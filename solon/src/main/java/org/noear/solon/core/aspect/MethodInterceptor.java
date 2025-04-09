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
package org.noear.solon.core.aspect;


/**
 * 方法拦截器
 *
 * <pre>{@code
 * @Target({ElementType.METHOD})
 * @Retention(RetentionPolicy.RUNTIME)
 * public @interface Cache {
 *
 * }
 *
 * //或者直接注册
 * context.beanInterceptorAdd(Cache.class, 111, new CacheInterceptor());
 * }</pre>
 *
 * @author noear
 * @since 2.9
 * */
@FunctionalInterface
public interface MethodInterceptor extends Interceptor {

}
