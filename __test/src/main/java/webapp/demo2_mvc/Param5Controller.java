package webapp.demo2_mvc;

import org.noear.solon.annotation.*;
import webapp.models.CatType;
import webapp.models.CatTypeModel;

/**
 * @author noear 2020/12/20 created
 */

@Mapping("/demo2/param5")
@Controller
public class Param5Controller {
    @Mapping("test1")
    public String test1(String a, @Param("params[a]") String a2) {
        return a + ":" + a2;
    }

    @Mapping("test2")
    public String test2(CatType cat) {
        return cat.title;
    }

    @Mapping("test3")
    public String test3(CatTypeModel model) {
        return model.getCat().title;
    }
}