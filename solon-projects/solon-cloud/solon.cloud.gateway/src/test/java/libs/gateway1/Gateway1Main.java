package libs.gateway1;

import org.noear.solon.Solon;

public class Gateway1Main {
    public static void main(String[] args) {
        Solon.start(Gateway1Main.class, new String[]{"--cfg=gateway1.yml"});
    }
}
