package webapp;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.addin.AddinLoader;
import org.noear.solon.addin.AddinPackage;
import org.noear.solon.core.ExtendLoader;

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
        AddinPackage dsMysql = AddinLoader.load(dsMysqlJar).start();
        AddinPackage dsKafka = AddinLoader.load(dsKafkaJar).start();


        //2.使用插件包内的接口
        IEtlSource dsMysqlSource = dsMysql.newInstance("xxx.ds.MySqlSource");
        IEtlTarget dsKafkaTarget = dsKafka.newInstance("xxx.ds.KafkaTarget");
        IEtlTask task = Utils.newInstance("xxx.job.EtlTask");

        task.run(dsMysqlSource, dsKafkaTarget);

        //3.卸载Jar插件包
        AddinLoader.unload(dsMysql);
        AddinLoader.unload(dsKafka);
    }

    public interface IEtlTask {
        void run(IEtlSource source, IEtlTarget target);
    }

    public interface IEtlSource {
    }

    public interface IEtlTarget {
    }
}
