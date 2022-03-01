package webapp.demo2_mvc;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Header;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import webapp.models.UserModel;

import java.util.List;
import java.util.Map;

/**
 * @author noear 2021/12/3 created
 */
@Mapping("/demo2/json/")
@Remoting
public class JsonController {
    @Mapping("/map")
    public Object map(Map<String, UserModel> userMap, ModelAndView mv) {
        if (userMap == null) {
            return null;
        } else {
            return userMap.get("1").id;
        }
    }

    @Mapping("/list")
    public Object list(List<UserModel> userAry, ModelAndView mv) {
        if (userAry == null) {
            return null;
        } else {
            return userAry.get(0).id;
        }
    }

    @Mapping("/body")
    public Integer body(@Body String body) {
        if (Utils.isEmpty(body)) {
            return 0;
        } else {
            return body.length();
        }
    }

    @Mapping("/form")
    public Integer form(String p) {
        if (Utils.isEmpty(p)) {
            return 0;
        } else {
            return p.length();
        }
    }

    @Mapping("/header")
    public Integer header(@Header("p") String p) {
        if (Utils.isEmpty(p)) {
            return 0;
        } else {
            return p.length();
        }
    }

    @Mapping("/cookie")
    public Integer cookie(Context ctx) {
        String p = ctx.cookie("p");

        if (Utils.isEmpty(p)) {
            return 0;
        } else {
            return p.length();
        }
    }
}
