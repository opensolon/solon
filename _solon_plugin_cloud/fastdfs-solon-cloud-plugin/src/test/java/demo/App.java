package demo;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Bean;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.fastdfs.service.CloudFileServiceFastDFSImpl;
import org.noear.solon.cloud.service.CloudFileService;

/**
 * @author liaocp
 */
public class App {

    @Bean
    public static CloudFileService cloudFileService(CloudProps cloudProps) {
        return new CloudFileServiceFastDFSImpl(cloudProps);
    }

    public static void main(String[] args) {
        SolonApp app = Solon.start(App.class, args);
        CloudProps cloudProps = new CloudProps(app.context(), "file.fastdfs");
        CloudManager.register(cloudFileService(cloudProps));
    }
}
