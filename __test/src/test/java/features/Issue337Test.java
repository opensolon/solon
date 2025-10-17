package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.Utils;
import org.noear.solon.test.SolonTest;
import org.noear.solon.Solon;
import webapp.App;

@SolonTest(App.class)
public class Issue337Test {
    @Test
    public void testFolderLocation() {
        try {
            String appFolder = Utils.appFolder();
            System.out.println("文件路径: " + appFolder);
        } catch (Throwable e) {
            e.printStackTrace();  
        }
    }
}
