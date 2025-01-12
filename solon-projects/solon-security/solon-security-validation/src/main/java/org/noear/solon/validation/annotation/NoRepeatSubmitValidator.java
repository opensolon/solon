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
package org.noear.solon.validation.annotation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.validation.Validator;

import java.io.IOException;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class NoRepeatSubmitValidator implements Validator<NoRepeatSubmit> {
    public static final NoRepeatSubmitValidator instance = new NoRepeatSubmitValidator();

    private NoRepeatSubmitChecker checker;

    public void setChecker(NoRepeatSubmitChecker checker) {
        if (checker != null) {
            this.checker = checker;
        }
    }

    @Override
    public String message(NoRepeatSubmit anno) {
        return anno.message();
    }

    @Override
    public Class<?>[] groups(NoRepeatSubmit anno) {
        return anno.groups();
    }

    @Override
    public Result validateOfContext(Context ctx, NoRepeatSubmit anno, String name, StringBuilder tmp) {
        if (checker == null) {
            throw new IllegalArgumentException("Missing NoRepeatSubmitChecker Setting");
        }

        tmp.append(ctx.pathNew()).append("#");

        for (HttpPart part : anno.value()) {
            switch (part) {
                case body: {
                    try {
                        tmp.append("body:");
                        tmp.append(ctx.bodyNew()).append(";");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
                case headers: {
                    tmp.append("headers:");
                    for (KeyValues<String> kv : ctx.headerMap()) {
                        tmp.append(kv.getKey()).append("=").append(kv.getValues()).append(";");
                    }
                    break;
                }
                default: {
                    tmp.append("params:");
                    for (KeyValues<String> kv : ctx.paramMap()) {
                        tmp.append(kv.getKey()).append("=").append(kv.getValues()).append(";");
                    }
                    break;
                }
            }
        }

        if (checker.check(anno, ctx, Utils.md5(tmp.toString()), anno.seconds())) {
            return Result.succeed();
        } else {
            return Result.failure();
        }
    }
}
