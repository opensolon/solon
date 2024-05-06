package webapp.demo2_mvc;

import org.noear.snack.ONode;
import org.noear.solon.annotation.*;
import webapp.models.UserModel;

import java.util.Map;

/**
 * @author noear 2021/12/3 created
 */
@Mapping("/demo2/props/")
@Controller
public class PropsController {

    @Mapping("/bean")
    public Object bean(UserModel user) {
        return user;
    }

    @Mapping("/bean_map")
    public Object bean_map(@Body Map<String, Object> body) {
        return ONode.stringify(body);
    }
}