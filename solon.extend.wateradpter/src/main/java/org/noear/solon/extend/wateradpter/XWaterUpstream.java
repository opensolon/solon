package org.noear.solon.extend.wateradpter;


import noear.water.WaterClient;
import noear.water.utils.TextUtil;
import org.noear.solonclient.XProxy;
import org.noear.solonclient.annotation.XClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Water Upstream （不能引用  XWaterAdapter）
 * */
public class XWaterUpstream {
    private final String TAG_SERVER = "{server}";

    private String _service;
    private WaterClient.DiscoverModel _model;
    private int _upstream_val = 0;
    private int _count;
    protected final List<String> _list = new ArrayList<>();
    private boolean _use_url;

    protected static String _consumer;
    protected static String _consumer_address;
    protected final static Map<String,XWaterUpstream> _map = new ConcurrentHashMap<>();

    private XWaterUpstream(String service){
        _service = service;
    }
    public static XWaterUpstream get(String service) {
        if(_map.containsKey(service)){
            return _map.get(service);
        }else{
            XWaterUpstream tmp = new XWaterUpstream(service);
            tmp.reload();
            _map.put(service, tmp);
            return tmp;
        }
    }

    protected static XWaterUpstream getOnly(String service){
        return _map.get(service);
    }

    /** 重新加载网关配置 */
    public void reload() {
        if(_consumer == null){
            _consumer = "";
        }

        if(_consumer_address == null){
            _consumer_address ="";
        }

        WaterClient.DiscoverModel mod = WaterClient.Registry.discover(_service, _consumer,_consumer_address);

        synchronized (_service) {
            doLoad(mod);
        }
    }

    /** 服务名 */
    public String name(){
        return _service;
    }

    /** 负载策略 */
    public String policy(){
        if(_model == null) {
            return null;
        }
        else{
            return _model.policy;
        }
    }

    /** 服务节点 */
    public List<String> nodes(){
        return Collections.unmodifiableList(_list);
    }

    private void doLoad(WaterClient.DiscoverModel model) {
        if (model == null || TextUtil.isEmpty(model.policy)) {
            return;
        }

        _model = model;

        //检查model.url 是否可用
        if (_model.url != null) {
            if (_model.url.indexOf("://") > 0) {
                _use_url = true;
            }
        } else {
            _model.url = "";
        }

        //构建可用服务地址 //支持轮询和带权重的轮询
        String sev_url;
        int sev_wgt;
        _list.clear();
        for (WaterClient.TargetModel m : _model.list) {
            sev_wgt = m.weight;
            sev_url = m.protocol + "://" + m.address;

            if (_model.url.contains(TAG_SERVER)) {
                sev_url = _model.url.replace(TAG_SERVER, sev_url);
            }

            while (sev_wgt > 0) {
                _list.add(sev_url);
                sev_wgt--;
            }
        }

        //记录可用服务数
        _count = _list.size();
    }

    public String get() {
        if (_use_url) {
            return _model.url;
        }

        if (_count == 0) {
            return null;
        }

        synchronized (_service) {
            if (_upstream_val == 9999999) {
                _upstream_val = 0;
            }

            _upstream_val++;
            int idx = _upstream_val % _count;

            return _list.get(idx);
        }
    }

    public static <T> T xclient(Class<?> clz){
        XClient c_meta = clz.getAnnotation(XClient.class);

        if(c_meta == null){
            throw new RuntimeException("No xclient annotation");
        }

        String c_sev = c_meta.value();
        if(TextUtil.isEmpty(c_sev)){
            throw new RuntimeException("XClient no name");
        }

        XWaterUpstream upstream = XWaterUpstream.get(c_sev);

        return new XProxy()
                .upstream((s)->upstream.get())
                .create(clz);
    }
}
