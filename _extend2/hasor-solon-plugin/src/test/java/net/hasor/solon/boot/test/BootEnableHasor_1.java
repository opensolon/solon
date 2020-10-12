package net.hasor.solon.boot.test;

import net.hasor.solon.boot.EnableHasor;
import org.noear.solon.annotation.XImport;

@EnableHasor(scanPackages = "net.hasor.test.spring.mod1.*")
@XImport(basePackages = { "net.hasor.test.spring.mod1" })
public class BootEnableHasor_1 {
}
