package demo.controller;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.core.handle.DownloadedFile;
import org.noear.solon.core.handle.UploadedFile;


/**
 * @author noear 2022/12/12 created
 */
@Mapping("file")
@Controller
public class File1 {
    @Mapping("upload")
    public Object upload(UploadedFile file) {
        String fileName = "demo/2022/" + Utils.md5(file.getName()) + "." + file.getExtension();
        return CloudClient.file().put(fileName, new Media(file.getContent(), file.getContentType()));
    }

    @Mapping("get")
    public DownloadedFile get(String fileName) {
        Media media = CloudClient.file().get(fileName);

        return new DownloadedFile(media.contentType(), media.body(), fileName);
    }
}
