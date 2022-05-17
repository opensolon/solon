package demo;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.staticfiles.StaticMappings;
import org.noear.solon.extend.staticfiles.StaticMimes;
import org.noear.solon.extend.staticfiles.repository.ClassPathStaticRepository;
import org.noear.solon.extend.staticfiles.repository.ExtendStaticRepository;
import org.noear.solon.extend.staticfiles.repository.FileStaticRepository;

/**
 * @author noear
 */
@Component
public class InitPluginDemo implements Plugin {
    @Override
    public void start(AopContext context) {
        //添加静态目录印射

        //1.添加扩展目录：${solon.extend}/static/
        StaticMappings.add("/", new ExtendStaticRepository());
        //2.添加本地绝对目录
        StaticMappings.add("/", new FileStaticRepository("/data/sss/water/water_ext/"));
        //3.添加资源路径
        StaticMappings.add("/", new ClassPathStaticRepository("user"));


        //添加mime
        StaticMimes.add(".vue", "text/html");
    }
}
