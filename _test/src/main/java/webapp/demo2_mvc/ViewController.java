package webapp.demo2_mvc;

import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Controller;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.i18n.annotation.I18n;

import java.time.LocalDate;
import java.util.Date;

@Controller
public class ViewController {
    @Mapping("/demo2/view")
    public ModelAndView dock(){
        ModelAndView model = new ModelAndView("dock.ftl");
        model.put("title","dock");
        model.put("msg","你好 world! in XController");

        return model;
    }

    @Mapping("/demo2/json")
    public ModelAndView dock2(){
        ModelAndView model = new ModelAndView();
        model.put("title","dock");
        model.put("msg","你好 world! in XController");

        model.put("bool",true);
        model.put("int",12);
        model.put("long",12l);
        model.put("double",12.12d);
        model.put("date",new Date());
        model.put("local_date", LocalDate.now());

        return model;
    }
}
