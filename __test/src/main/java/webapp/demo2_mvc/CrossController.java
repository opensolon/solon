package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.web.cors.annotation.CrossOrigin;

/**
 * @author noear 2021/5/22 created
 */
@Mapping("/demo2/cross")
@CrossOrigin(origins = "*")
@Controller
public class CrossController {
    @Mapping("test")
    public void test(){

    }
}
