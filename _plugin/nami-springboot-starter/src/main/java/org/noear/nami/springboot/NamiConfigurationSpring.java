package org.noear.nami.springboot;

import org.noear.nami.*;
import org.noear.nami.annotation.NamiClient;
import org.noear.nami.channel.http.okhttp.HttpChannel;
import org.noear.nami.coder.fastjson.FastjsonDecoder;
import org.noear.nami.coder.fastjson.FastjsonEncoder;
import org.noear.nami.coder.fastjson.FastjsonTypeEncoder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.function.Supplier;

public class NamiConfigurationSpring implements NamiConfiguration {
    @Autowired(required = false)
    NamiConfigurationCustom namiConfiguration;


    @PostConstruct
    private void init(){
        NamiConfigurationDefault.proxy=this;
        NamiManager.reg(new FastjsonDecoder(){
            @Override
            public <T> T decode(Result rst, Type type) {
                if(byte[].class.isAssignableFrom((Class)type)){
                    return (T)rst.body();
                }
                if(String.class.isAssignableFrom((Class)type)){
                    return (T)rst.bodyAsString();
                }
                if(Result.class.isAssignableFrom((Class<?>) type)){
                    return (T)rst;
                }
                return super.decode(rst,type);
            }
        });
        NamiManager.reg(FastjsonEncoder.instance);
        NamiManager.reg(FastjsonTypeEncoder.instance);
        NamiManager.regIfAbsent("http", HttpChannel.instance);
        NamiManager.regIfAbsent("https", HttpChannel.instance);
    }
    @Override
    public void config(NamiClient client, NamiBuilder builder) {
        if(namiConfiguration!=null){
            namiConfiguration.config(client,builder);
        }
       // builder.upstream()
        String name=client.name();
        if(!StringUtils.isEmpty(name)){
           Supplier<String> up= NamiUpstreamFactory.getUpstream(name);
           if(up!=null){
               builder.upstream(up);
           }
        }
    }
}
