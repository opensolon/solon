package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Singleton;

/**
 * @author noear 2022/12/10 created
 */
@Singleton(false)
@Mapping("/demo2/path")
@Controller
public class PathController {
    @Mapping("/test0**")
    public String test0(){
        return "ok";
    }

    @Mapping("/test1/?")
    public String test1(){
        return "ok";
    }

    @Mapping("/test2/?*")
    public String test2(){
        return "ok";
    }
}
