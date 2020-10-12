package net.hasor.solon.boot.test;

import net.hasor.solon.boot.EnableHasor;
import org.noear.solon.annotation.XImport;

@EnableHasor
@XImport(scanPackages = { "net.hasor.test.spring.mod1" })
public class BootEnableHasor_2 {

}
