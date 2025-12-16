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
package org.noear.solon.rx.handle;

import org.noear.solon.core.handle.Entity;
import org.noear.solon.core.handle.StatusCodes;
import reactor.core.publisher.Mono;

/**
 * 响应式处理实体
 *
 * @author noear
 * @since 3.7.4
 */
public class RxEntity {
    private final Entity entity;

    private RxEntity(int code) {
        this.entity = new Entity().status(code);
    }

    public RxEntity body(Object body) {
        entity.body(body);
        return this;
    }

    public RxEntity headerAdd(String name, String value) {
        entity.headerAdd(name, value);
        return this;
    }

    public RxEntity headerRemove(String name) {
        entity.headerRemove(name);
        return this;
    }

    public RxEntity headerSet(String name, String value) {
        entity.headerSet(name, value);
        return this;
    }

    public RxEntity contentType(String contentType) {
        entity.contentType(contentType);
        return this;
    }

    public Mono<Entity> build() {
        return Mono.just(entity);
    }

    /// ///////////

    public static RxEntity ok() {
        return new RxEntity(StatusCodes.CODE_OK);
    }

    public static RxEntity badRequest() {
        return new RxEntity(StatusCodes.CODE_BAD_REQUEST);
    }

    public static RxEntity notFound() {
        return new RxEntity(StatusCodes.CODE_NOT_FOUND);
    }

    public static RxEntity accepted() {
        return new RxEntity(StatusCodes.CODE_ACCEPTED);
    }

    public static RxEntity status(int code) {
        return new RxEntity(code);
    }
}