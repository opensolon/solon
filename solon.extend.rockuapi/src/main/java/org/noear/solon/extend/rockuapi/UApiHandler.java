package org.noear.solon.extend.rockuapi;

import noear.water.utils.TextUtil;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//必须要使用 XMapping + XBean
public abstract class UApiHandler implements XHandler {
    //构造
    public UApiHandler() {
        _apis = new HashMap<>();
        XMapping _map = this.getClass().getAnnotation(XMapping.class);
        if (_map == null) {
            throw new RuntimeException("No XMapping!");
        }

        _path = _map.value();

        register();
    }

    public abstract void register();


    //接口相关
    //
    private String _path=null;
    private Map<String,UApiCreator> _apis;
    private UApiCreator _api_def = null;

    public void addApi(UApiCreator api) {
        String name = api.run().name();

        if (TextUtil.isEmpty(name)) { //添加默认接口的支持
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

    //XHandlerEx实现：处理
    @Override
    public void handle(XContext context) throws Exception {

        UApiCreator creator = _apis.get(context.pathAsUpper());//同一转为大写，用时可支持大小写

        if(creator == null){
            context.attrSet("noapi",true);
            creator = _api_def;
        }

        if (creator == null) {
            return;
        }

        UApi api = creator.run();

        context.attrSet("api",api);

        for(XHandler interceptor : _interceptors){
            try {
                interceptor.handle(context);
            }catch (Exception ex){
                ex.printStackTrace();

                //如果异常，则设为已处理；并记录异常（告诉下个拦截器知道）
                //
                context.setHandled(true);
                context.attrSet("error",ex);
            }
        }
        context.setHandled(true);
    }
}
