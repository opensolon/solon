package demo;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import org.noear.solon.Solon;

/**
 * @author noear 2022/11/5 created
 */
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            //订阅事件，后以定制
            app.onEvent(ActiveRecordPlugin.class, arp -> {
                //启用开发或调试模式（可以打印sql）
                if (Solon.cfg().isDebugMode() || Solon.cfg().isFilesMode()) {
                    arp.setDevMode(true);
                }
            });
        });
    }
}
