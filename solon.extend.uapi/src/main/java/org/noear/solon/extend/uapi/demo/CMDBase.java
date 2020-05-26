package org.noear.solon.extend.uapi.demo;


import org.noear.solon.core.Result;
import org.noear.solon.core.XContext;
import org.noear.solon.extend.uapi.UApi;

public class CMDBase implements UApi {
    @Override
    public String name() {
        return "B.0.1";
    }

    @Override
    public Result call(XContext cxt) {
        Result<UserModel> result = new Result<>();
        result.data = new UserModel();

        return result;
    }

}
