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
import org.noear.solon.validation.Validator;

/**
 *
 * @author noear
 * @since 1.3
 * */
public class LoginedValidator implements Validator<Logined> {
    public static final LoginedValidator instance = new LoginedValidator();

    private  LoginedChecker checker = new LoginedCheckerImp();

    public  void setChecker(LoginedChecker checker) {
        if (checker != null) {
            this.checker = checker;
        }
    }

    public static String sessionUserKeyName = "user_id";

    @Override
    public String message(Logined anno) {
        return anno.message();
    }

    @Override
    public Class<?>[] groups(Logined anno) {
        return anno.groups();
    }

    @Override
    public Result validateOfContext(Context ctx, Logined anno, String name, StringBuilder tmp) {
        String userKeyName = anno.value();
        if(Utils.isEmpty(userKeyName)){
            userKeyName = LoginedValidator.sessionUserKeyName;
        }

        if(checker.check(anno, ctx, userKeyName)){
            return Result.succeed();
        }else{
            return Result.failure();
        }
    }
}
