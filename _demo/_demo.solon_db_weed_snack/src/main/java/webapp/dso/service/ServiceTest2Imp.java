package webapp.dso.service;

import org.noear.solon.annotation.Bean;

@Bean
public class ServiceTest2Imp implements ServiceTest2{
    public String test(){
        return "ddd";
    }
}
