package org.noear.solon.extend.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;

import java.util.*;

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
    Map<String,Long> watchedIndex=new HashMap<>();

    /**
     * 配置版本号
     */
    long configVer = 0;
    String token;
    String configKey;
    Set<String> watchKeys=new HashSet<>();

    public ConsulConfigTask(ConsulClient client) {
        this.client = client;

        //token 可为null
        token=Solon.cfg().get(Constants.TOKEN);

        //1.优先用config.key
        configKey = Solon.cfg().get(Constants.CONFIG_KEY);

        //2.其次用app group
        if(Utils.isEmpty(configKey)){
            configKey = Solon.cfg().appGroup();
        }

        //3.再镒用app name
        if(Utils.isEmpty(configKey)){
            configKey = Solon.cfg().appName();
        }

        //需要监听的配置项
        String watchKeys=Solon.cfg().get(Constants.CONFIG_WATCH);
        if(Utils.isNotEmpty(watchKeys)){
            this.watchKeys.addAll(Arrays.asList(watchKeys.split(",")));
        }
    }

    @Override
    public void run() {
        Response<GetValue> v = client.getKVValue(configKey,token);

        GetValue gv = v.getValue();

        if (gv != null) {
            if (gv.getModifyIndex() > configVer) {
                configVer = gv.getModifyIndex();

                String configValue = gv.getDecodedValue();
                Properties keyValues = Utils.buildProperties(configValue);

                if (keyValues != null) {
                    Solon.cfg().putAll(keyValues);
                }
            }
        }
        Map<String,String> updatedValues=new HashMap<>();
        for(String key:watchKeys){
            Response<List<GetValue>> response= client.getKVValues(key,token);
            List<GetValue> getValues=response.getValue();
            if(getValues!=null){
                for(GetValue value:getValues){
                   Long index= watchedIndex.get(value.getKey());
                   if(index==null||index.longValue()!=value.getModifyIndex()){
                       watchedIndex.put(value.getKey(),value.getModifyIndex());
                       updatedValues.put(value.getKey(),value.getDecodedValue());
                   }
                }
            }
        }
        if(updatedValues.size()>0){
            EventBus.push(new ConsulKvUpdateEvent(updatedValues));
        }
    }
}
