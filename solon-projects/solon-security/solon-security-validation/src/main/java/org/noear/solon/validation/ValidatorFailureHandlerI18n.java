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
package org.noear.solon.validation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.expression.snel.SnelParser;
import org.noear.solon.i18n.I18nUtil;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * 验证失败处理默认实现（支持表达式：message='{aaa.bbb} cc {xxx}'）
 *
 * @author noear
 * @since 3.8
 * */
public class ValidatorFailureHandlerI18n implements ValidatorFailureHandler {
    private final SnelParser SNEL;

    public ValidatorFailureHandlerI18n(int cahceCapacity) {
        SNEL = new SnelParser(cahceCapacity, '#', '{');
    }

    public ValidatorFailureHandlerI18n(SnelParser snelParser) {
        Objects.requireNonNull(snelParser, "snelParser");

        SNEL = snelParser;
    }

    @Override
    public boolean onFailure(Context ctx, Annotation anno, Result rst, String msg) throws Throwable {
        msg = format(anno, rst, msg);

        throw new ValidatorException(rst.getCode(), msg, anno, rst);
    }

    /**
     * 格式化验证消息
     */
    public String format(Annotation anno, Result rst, String msg) {
        if (Utils.isEmpty(msg)) {
            if (anno != null) {
                if (Utils.isEmpty(rst.getDescription())) {
                    msg = new StringBuilder(100)
                            .append("@")
                            .append(anno.annotationType().getSimpleName())
                            .append(" verification failed")
                            .toString();
                } else {
                    msg = new StringBuilder(100)
                            .append("@")
                            .append(anno.annotationType().getSimpleName())
                            .append(" verification failed: ")
                            .append(rst.getDescription())
                            .toString();
                }
            }
        } else if (SNEL.hasMarker(msg)) {
            Context ctx = Context.current();

            if (ctx != null) {
                // for web
                msg = SNEL.forTmpl().parse(msg).eval(key -> I18nUtil.getMessage(ctx, key.toString()));
            }
        }

        return msg;
    }
}