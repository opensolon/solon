package net.hasor.spring.boot;
import net.hasor.test.spring.mod1.TestModuleA;
import net.hasor.solon.boot.EnableHasor;
import org.noear.solon.XApp;
import org.noear.solon.annotation.XImport;

@XImport(basePackages = { "net.hasor.test.spring.mod1" })
@EnableHasor(useProperties = false)
public class BootShareHasor_2 {
}
