package webapp.demo2_mvc;

import org.noear.solon.annotation.*;
import webapp.dso.AsyncTask;
import webapp.models.UserD;
import webapp.models.UserModel;

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

    @Mapping("body")
    public String test_body(@Body String bodyStr) throws IOException {
        asyncTask.test();
        return bodyStr;
    }
}
