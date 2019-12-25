package webapp.demo2_mvc;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XSingleton;
import org.noear.solon.core.ModelAndView;

import java.time.LocalDate;
import java.util.Date;

@XController
public class ViewController {
    @XMapping("/demo2/view")
    public ModelAndView dock(){
        ModelAndView model = new ModelAndView("dock.ftl");
        model.put("title","dock");
        model.put("msg","你好 world! in XController");

        return model;
    }

    @XMapping("/demo2/json")
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
