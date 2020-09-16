package webapp.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XInject;
import org.noear.solon.annotation.XMapping;
import webapp.dso.db.AppxMapper;
import webapp.dso.db.AppxMapper2;
import webapp.model.AppxModel;

@XMapping("/test")
@XController
public class TestController {

    @XInject
    AppxMapper appxMapper;

    @XInject
    AppxMapper2 appxMapper2;

    @XMapping("demo1")
    public Object test1() throws Exception {

        AppxModel tmp = appxMapper.appx_get();

        return tmp;
    }

    @XMapping("demo2")
    public Object test2() throws Exception {

        AppxModel tmp = appxMapper2.appx_get2(48);

        return tmp;
    }


    @XMapping("demo11")
    public Object test11() throws Exception {
        AppxModel tmp = appxMapper.appx_get();

        return tmp;
    }

    @XMapping("demo12")
    public Object test12() throws Exception {
        AppxModel tmp = appxMapper2.appx_get2(48);

        return tmp;
    }

}
