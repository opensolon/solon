package demo;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.extend.file.s3.service.CloudFileServiceImpl;
import org.noear.solon.core.Props;

/**
 * @author 等風來再離開 2022/11/29 created
 */
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args);

        CloudFileServiceImpl fileService = (CloudFileServiceImpl)CloudClient.file();
        fileService.addBucket("xxx", new Props());
    }
}
