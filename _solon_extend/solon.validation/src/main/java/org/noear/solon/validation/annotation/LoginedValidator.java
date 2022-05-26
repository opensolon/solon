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
