package org.noear.solon.extend.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.PropsLoader;

import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

/**
 * 配置获取任务
 *
 * <p><code>
 * @Controller
 * public class Xxx{
 *     @Inject("${hello}")
 *     String hello;
 *
 *     @Mapping("/hello")
 *     public String hello(){
 *         return hello;
 *     }
 * }
 * </code></p>
 *
 * @author 夜の孤城
 * @since 1.2
 */
class ConsulConfigTask extends TimerTask {
    ConsulClient client;

    /**
     * 配置版本号
     * */
    long configVer = 0;
    /**
     * 配置key
     * */
    String configKey;

    public ConsulConfigTask(ConsulClient client) {
        this.client = client;
        this.configKey = "config/" + Solon.cfg().get(Constants.APP_SERVICE_NAME);
    }

    @Override
    public void run() {
        Response<GetValue> v = client.getKVValue(configKey);
        GetValue gv = v.getValue();
        if (gv != null) {
            if (gv.getModifyIndex() > configVer) {
                configVer = gv.getModifyIndex();
                String configValue=gv.getDecodedValue();
                Properties keyValues= Utils.buildProperties(configValue);
                if(keyValues!=null){
                    Solon.cfg().putAll(keyValues);
                }
            }
        }else{
            //初始化默认的配置项
            client.setKVValue(configKey,"");
        }
    }
}
