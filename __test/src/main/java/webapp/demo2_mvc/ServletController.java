package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Singleton;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

/**
 * @author noear 2021/3/30 created
 */
@Mapping("/demo2/servlet")
@Controller
public class ServletController {
//    @Mapping("hello")
//    public String hello(HttpServletRequest request, HttpServletResponse response){
//        if(request == null){
//            return "Err";
//        }else{
//            return "Ok";
//        }
//    }
}
