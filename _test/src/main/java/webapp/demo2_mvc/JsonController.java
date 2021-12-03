package webapp.demo2_mvc;

import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;
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
    public Object map(Map<String, UserModel> userMap) {
        if (userMap == null) {
            return null;
        } else {
            return userMap.get("1").id;
        }
    }

    @Mapping("/list")
    public Object list(List<UserModel> userAry) {
        if (userAry == null) {
            return null;
        } else {
            return userAry.get(0).id;
        }
    }
}
