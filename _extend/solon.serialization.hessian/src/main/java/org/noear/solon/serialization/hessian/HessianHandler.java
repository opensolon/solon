package org.noear.solon.serialization.hessian;


import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

import java.io.InputStream;
import java.io.OutputStream;

public class HessianHandler implements Handler {
    private Class<?> _homeAPI;
    private Object _homeImpl;

    private HessianSkeleton _homeSkeleton;

    private SerializerFactory _serializerFactory;


    public HessianHandler(Class<?> homeAPI, Object homeImpl) {
        _homeAPI = homeAPI;
        _homeImpl = homeImpl;

        _homeSkeleton = new HessianSkeleton(_homeImpl, _homeAPI);
        _serializerFactory = new SerializerFactory();
    }

    @Override
    public void handle(Context ctx) throws Throwable {

        try {
            InputStream is = ctx.bodyAsStream();
            OutputStream os = ctx.outputStream();

            ctx.contentType("x-application/hessian");

            _homeSkeleton.invoke(is, os, _serializerFactory);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
