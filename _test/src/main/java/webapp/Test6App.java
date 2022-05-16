package webapp;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.plugind.PluginManager;
import org.noear.solon.plugind.PluginPackage;

import java.io.File;

/**
 * @author noear 2022/5/14 created
 */
public class Test6App {
    public static void main(String[] args) {
        Solon.start(Test6App.class, args);

        File dsMysqlJar = new File("/xxx/ds/mysql.jar");
        File dsKafkaJar = new File("/xxx/ds/kafka.jar");

        //1.加载插件包，并启动
        PluginPackage dsMysql = PluginManager.loadJar(dsMysqlJar).start();
        PluginPackage dsKafka = PluginManager.loadJar(dsKafkaJar).start();


        //2.使用插件包内的接口
        IEtlSource dsMysqlSource = dsMysql.newInstance("xxx.ds.MySqlSource");
        IEtlTarget dsKafkaTarget = dsKafka.newInstance("xxx.ds.KafkaTarget");
        IEtlTask task = Utils.newInstance("xxx.job.EtlTask");

        task.run(dsMysqlSource, dsKafkaTarget);

        //3.卸载Jar插件包
        PluginManager.unloadJar(dsMysql);
        PluginManager.unloadJar(dsKafka);
    }

    public interface IEtlTask {
        void run(IEtlSource source, IEtlTarget target);
    }

    public interface IEtlSource {
    }

    public interface IEtlTarget {
    }
}
