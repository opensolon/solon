package webapp.demo2_mvc;

import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import webapp.dso.AsyncTask;
import webapp.models.RegisterUsername;
import webapp.models.UserD;
import webapp.models.UserModel;
import webapp.models.UserModelEx;

import java.io.IOException;

/**
 * @author noear 2020/12/20 created
 */

@Mapping("/demo2/param4")
@Controller
public class Param4Controller {

    @Inject
    AsyncTask asyncTask;

    @Mapping("json")
    public UserModel test_json(UserModel user) throws IOException {
        asyncTask.test();

        return user;
    }

    @Mapping("param")
    public UserModel test_param(UserModel user) throws IOException {
        asyncTask.test();
        return user;
    }

    @Mapping("param2")
    public UserD test_param(UserD user) throws IOException {
        asyncTask.test();
        return user;
    }

    @Mapping("param3")
    public UserModelEx test_param3(UserModelEx user) throws IOException {
        asyncTask.test();
        return user;
    }

    @Mapping("body")
    public String test_body(@Body String bodyStr) throws IOException {
        asyncTask.test();
        return bodyStr;
    }

    @Mapping("body2")
    public RegisterUsername test_body2(RegisterUsername user) throws IOException {
        asyncTask.test();
        return user;
    }

    @Mapping("test")
    public String test(Context ctx) throws IOException {
        ctx.paramMap();
        return ctx.body();
    }
}
