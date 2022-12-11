package webapp.demo2_mvc;

import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import webapp.dso.AsyncTask;
import webapp.models.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Mapping("json2")
    public List<UserModel> test_json2(List<UserModel> list) throws IOException {
        asyncTask.test();

        return list;
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

    @Mapping("body_map")
    public int test_body_map(@Body Map<String,String> bodyMap) throws IOException {
        asyncTask.test();
        return bodyMap.size();
    }

    @Mapping("body_val")
    public int test_body_val(@Body Map<String,String> name) throws IOException {
        asyncTask.test();
        return name.size();
    }

    @Mapping("val")
    public Object test_val(String name) throws IOException {
        asyncTask.test();
        return name;
    }

    @Mapping("body2")
    public RegisterUsername test_body2(RegisterUsername user) throws IOException {
        asyncTask.test();
        return user;
    }

    @Mapping("body2_t")
    public Object test_body2_t(PageRequest<UserModel> request) throws IOException {
        UserModel userModel = request.getData();
        return request;
    }

    @Mapping("test")
    public String test(Context ctx) throws IOException {
        ctx.paramMap();
        return ctx.body();
    }


    //?id=3&aaa[0]=1&aaa[1]=2
    @Get
    @Mapping("test2")
    public Map test2(Context ctx) throws IOException {
        UserModel user = ctx.paramMap().getBean(UserModel.class);

        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("aaa", user.getAaa());
        return map;
    }
}
