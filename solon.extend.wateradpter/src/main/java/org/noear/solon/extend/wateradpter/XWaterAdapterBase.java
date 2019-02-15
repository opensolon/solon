package org.noear.solon.extend.wateradpter;

import noear.snacks.OMapper;
import noear.snacks.ONode;
import noear.water.utils.TextUtil;
import org.noear.solon.XApp;
import org.noear.solon.core.XMap;
import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;
import org.noear.solon.core.XMethod;
import noear.water.WaterAdapterBase;
import noear.water.WaterClient;
import org.noear.solon.core.XPlugin;

import java.io.IOException;

//
// Water for service project adapter
//
abstract class XWaterAdapterBase extends WaterAdapterBase {

    XMap service_args;
    private String _localHost;
    private String _note = "";
    public XWaterAdapterBase(XMap args, int port) {
        service_args = args;
        service_port = port;
        service_check_path = "/run/check/";
        msg_receiver_path = "/msg/receive";

        onInit();
    }

    protected void onInit(){
        registerService();
        messageSubscribe();
    }

    /** 当前服务的本地地址(host:port) */
    public String localHost(){
        return _localHost;
    }

    @Override
    protected void registerService() {
        _localHost = getLocalAddress(this.service_port);

        XWaterUpstream._consumer = service_name();
        XWaterUpstream._consumer_address = _localHost;

        if (service_args == null || service_args.size()==0) {
            _note = "";
        } else {
            ONode tmp = OMapper.map(service_args);
            tmp.remove("server.port");
            _note = tmp.toJson();
            if (_note.length() < 3) {
                _note = "";
            }
        }

        if (service_port > 0) {
            WaterClient.Registry.add(this.service_name(), _localHost, _note, this.service_check_path, 0, this.alarm_mobile());
        } else {
            WaterClient.Registry.add(this.service_name(), _localHost, _note, this.service_check_path, 1, this.alarm_mobile());
        }
    }

    /** 设置状态 */
    public void stateSet(boolean enabled) {
        if (TextUtil.isEmpty(_localHost) == false) {
            WaterClient.Registry.set(this.service_name(), _localHost, _note, enabled);
        }
    }

    @Override
    public void cacheUpdateHandler(String tag) {
        super.cacheUpdateHandler(tag);
        String[] ss = tag.split(":");
        if("upstream".equals(ss[0])){
            XWaterUpstream tmp = XWaterUpstream.getOnly(ss[1]);
            if(tmp!=null){
                try {
                    tmp.reload();
                }catch (Exception ex){
                    ex.printStackTrace();//最后日志记录到服务端
                    WaterClient.Logger.append("water_log_upstream",
                            0, ss[1], "reload","", XUtil.getFullStackTrace(ex));

                }
            }
        }
    }

    //2.1.提供服务供查入口 //必须重写
    public String serviceCheck(XContext cxt) throws Exception {
        String ups = cxt.param("upstream");
        String enabled = cxt.param("enabled");

        if(TextUtil.isEmpty(ups) == false){
            //用于检查负责的情况
            ONode odata  = new ONode().asObject();

            if("*".equals(ups)){
                XWaterUpstream._map.forEach((k, v) -> {
                    ONode n = odata.get(k);

                    n.set("service", k);
                    ONode nl = n.get("upstream").asArray();
                    v._list.forEach((s) -> {
                        nl.add(s);
                    });
                });
            }else{
                XWaterUpstream v = XWaterUpstream.getOnly(ups);
                if(v!=null) {
                    ONode n = odata.get(ups);

                    n.set("service", ups);
                    ONode nl = n.get("upstream").asArray();
                    v._list.forEach((s) -> {
                        nl.add(s);
                    });
                }
            }

            return odata.toJson();
        }else {
            if (TextUtil.isEmpty(enabled) ==false) {
                //手动控制服务是否关掉或开启
                if ("1".equals(enabled)) {
                    stateSet(true);
                } else if ("0".equals(enabled)) {
                    stateSet(false);
                }
            }

            return "{\"code\":1}";
        }
    }

    //::可以重写，且需要RequestMapping
    //2.2.接收消息
    public String messageReceive(XContext cxt) throws Exception {
        return doMessageReceive(k -> cxt.param(k));
    }

    public void handle(XContext context) throws IOException {
        String path = context.path();

        String text = "";
        try {
            if (service_check_path.equals(path)) {
                text = serviceCheck(context);
            }

            if (msg_receiver_path.equals(path)) {
                text = messageReceive(context);
            }
        } catch (Exception ex) {
            text = XUtil.getFullStackTrace(ex);ex.printStackTrace();
        }

        context.output(text);
    }
}
