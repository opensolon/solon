package webapp.demo3_upload;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.UploadedFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author noear 2021/2/10 created
 */
@Mapping("/demo3/down")
@Controller
public class DownController {
    @Mapping("f1")
    public UploadedFile down() {
        ByteArrayInputStream stream = new ByteArrayInputStream("{code:1}".getBytes(StandardCharsets.UTF_8));
        UploadedFile file = new UploadedFile("text/json", stream.available(), stream, "test.json", "json");

        return file;
    }

    @Mapping("f2")
    public File down2() {
        String filePath = Utils.getResource("static/debug.htm").getFile();
        File file = new File(filePath);
        return file;
    }
}
