package webapp.controller;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;

/**
 * 给定侦听器类必须实现以下一个或多个接口：
 *
 * ServletContextAttributeListener
 * ServletRequestListener
 * ServletRequestAttributeListener
 * HttpSessionListener
 * HttpSessionAttributeListener
 * */
@WebListener
public class TmpListener implements ServletRequestListener {

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        System.out.println("我被触发了");
    }
}
