package org.noear.solon.extend.uapi;

import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class UApiHandler<P> implements XHandler {

    public UApiHandler(ParamParser<P> parser){

    }

    public abstract void register();

    //接口相关
    //
    private String _path=null;
    private Map<String,UApiCreator> _apis;
    private UApiCreator _api_def = null;

    public void addApi(UApiCreator api) {
        String name = api.run().name();

        if (XUtil.isEmpty(name)) { //添加默认接口的支持
            _api_def = api;
        } else {
            _apis.put(XUtil.mergePath(_path, name).toUpperCase(), api);//同一转为大写，用时可支持大小写
        }
    }

    //拦截器相关
    //
    private List<XHandler> _interceptors = new ArrayList<>();
    public void addInterceptor(XHandler interceptor){
        _interceptors.add(interceptor);
    }

    @Override
    public void handle(XContext ctx) throws Throwable {

    }
}
