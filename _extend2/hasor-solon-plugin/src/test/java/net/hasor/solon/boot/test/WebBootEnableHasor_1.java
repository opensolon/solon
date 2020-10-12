package net.hasor.solon.boot.test;
import net.hasor.solon.boot.EnableHasor;
import net.hasor.solon.boot.EnableHasorWeb;
import net.hasor.solon.boot.SolonModule;
import net.hasor.test.spring.web.Hello;
import net.hasor.test.spring.web.JsonRender;
import net.hasor.core.TypeSupplier;

import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.noear.solon.annotation.XImport;

@EnableHasorWeb
@EnableHasor(startWith = WebBootEnableHasor_1.class)
@XImport(basePackages = "net.hasor.test.spring.web")
public class WebBootEnableHasor_1 implements WebModule, SolonModule {
    @Override
    public void loadModule(WebApiBinder apiBinder) {
        TypeSupplier springTypeSupplier = springTypeSupplier(apiBinder);
        //Hello的创建使用 Spring，因为它已经被 Spring 托管了
        apiBinder.loadMappingTo(Hello.class, springTypeSupplier);
        apiBinder.addRender("json").toInstance(new JsonRender());
    }
}
