package demo;

import org.noear.solon.hotplug.PluginInfo;
import org.noear.solon.hotplug.PluginManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author noear 2022/7/31 created
 */
public class App {
    public static void main(String[] args){
       List<PluginInfo> list =  PluginManager.getPlugins().stream().filter(p->p.getStarted()).collect(Collectors.toList());
    }
}
