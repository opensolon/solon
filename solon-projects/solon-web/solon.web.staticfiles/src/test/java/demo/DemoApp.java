package demo;

import org.noear.solon.Solon;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;

/**
 * @author noear 2023/1/25 created
 */
public class DemoApp {
    public static void main(String[] args){
        Solon.start(DemoApp.class, args, app->{
            StaticMappings.add("/doc.html",  new ClassPathStaticRepository("/META-INF/resources/"));
            StaticMappings.add("/webjars/",  new ClassPathStaticRepository("/META-INF/resources/webjars/"));
        });
    }
}
