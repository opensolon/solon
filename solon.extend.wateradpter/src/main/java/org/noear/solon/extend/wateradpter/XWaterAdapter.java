package org.noear.solon.extend.wateradpter;

import noear.water.WaterClient;
import noear.weed.WeedConfig;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XPlugin;

import java.util.HashMap;
import java.util.Map;

//
// Water for service project adapter
//
public abstract class XWaterAdapter extends XWaterAdapterBase implements XPlugin {

    private static XWaterAdapter _global;
    public static XWaterAdapter global(){
        return _global;
    }

    private Map<String,XMessageHandler> _router;
    public Map<String,XMessageHandler> router(){return _router; }


    public String msg_receiver_url(){return null;}

    public XWaterAdapter() {
        super(XApp.global().prop().argx(),XApp.global().port());
        _global = this;
    }

    @Override
    public void start(XApp app) {
        app.all(service_check_path, this::handle);
        app.all(msg_receiver_path,  this::handle);
    }

    @Override
    protected void onInit() {
        _router = new HashMap<>();

        registerService();

        messageListening(_router);

        messageSubscribe();

        initWeed();
    }

    protected void initWeed() {
        Class<?> clz = XUtil.loadClass("noear.bcf.BcfClient");

        if (clz == null) {
            WeedConfig.onExecuteAft(cmd -> {
                WaterClient.Registry.track(service_name(), cmd, 1000);
            });
        }
    }

    //支持手动加入监听(保持旧的兼容)
    public void messageListening(Map<String, XMessageHandler> map){};

    @Override
    public void messageSubscribeHandler() {
        if (_router.size() == 0) {
            return;
        }

        String[] topics = new String[_router.size()];
        _router.keySet().toArray(topics);

        try {
            messageSubscribeTopic(msg_receiver_url(), 0, topics);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public boolean messageReceiveHandler(WaterClient.MessageModel msg) throws Exception {
        XMessageHandler handler = _router.get(msg.topic);
        if(handler == null){
            return true;
        }else{
            return handler.handler(msg);
        }
    }
}
