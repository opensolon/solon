package org.noear.solon.web.servlet;

import jakarta.servlet.http.Part;
import java.lang.reflect.Method;

/**
 * @author noear
 * @since 2.7
 */
public class MultipartDelete implements Runnable {
    private final Part part;
    private Method partDeleteMethod;

    public MultipartDelete(Part part) {
        this.part = part;

        if (part != null) {
            try {
                partDeleteMethod = part.getClass().getDeclaredMethod("delete");

                if (partDeleteMethod != null) {
                    partDeleteMethod.setAccessible(true);
                }
            } catch (Throwable e) {
                //乎略
            }
        }
    }

    @Override
    public void run() {
        if (partDeleteMethod != null) {
            try {
                partDeleteMethod.invoke(part);
            } catch (Throwable e) {
                //乎略
            }
        }
    }
}
