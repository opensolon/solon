package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import webapp.models.UserModel;

/**
 * @author noear 2023/10/10 created
 */
@Mapping("/demo2/ext/")
@Controller
public class ExtController extends ExtControllerBase<UserModel>{
}
