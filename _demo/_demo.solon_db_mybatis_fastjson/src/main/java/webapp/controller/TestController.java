package webapp.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import webapp.dso.db.AppxMapper;
import webapp.dso.db.AppxMapper2;
import webapp.model.AppxModel;

@Mapping("/test")
@Controller
public class TestController {

    @Inject
    AppxMapper appxMapper;

    @Inject
    AppxMapper2 appxMapper2;

    @Mapping("demo1")
    public Object test1() throws Exception {

        AppxModel tmp = appxMapper.appx_get();

        return tmp;
    }

    @Mapping("demo2")
    public Object test2() throws Exception {

        AppxModel tmp = appxMapper2.appx_get2(48);

        return tmp;
    }


    @Mapping("demo11")
    public Object test11() throws Exception {
        AppxModel tmp = appxMapper.appx_get();

        return tmp;
    }

    @Mapping("demo12")
    public Object test12() throws Exception {
        AppxModel tmp = appxMapper2.appx_get2(48);

        return tmp;
    }

}
