package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

import java.io.File;

/**
 * @author noear 2023/8/23 created
 */
@Controller
public class DownController {
    @Mapping("/demo2/range/")
    public File down(){
        return new File("/Users/noear/Movies/range_test.mov");
    }
}
