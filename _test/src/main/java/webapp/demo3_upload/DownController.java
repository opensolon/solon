package webapp.demo3_upload;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.DownloadedFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author noear 2021/2/10 created
 */
@Mapping("/demo3/down")
@Controller
public class DownController {
    @Mapping("f1")
    public DownloadedFile down() {
        InputStream stream = new ByteArrayInputStream("{code:1}".getBytes(StandardCharsets.UTF_8));

        //使用 InputStream 实例化
        return new DownloadedFile("text/json", stream, "test.json");
    }

    @Mapping("f2")
    public DownloadedFile down12() {
        byte[] bytes = "test".getBytes(StandardCharsets.UTF_8);

        //使用 byte[] 实例化
        return new DownloadedFile("text/json", bytes, "test.txt");

    }

    @Mapping("f3")
    public File down2() {
        String filePath = Utils.getResource("static/debug.htm").getFile();

        return new File(filePath);
    }

    @Mapping("f4")
    public void down3(Context ctx) throws IOException {
        String filePath = Utils.getResource("static/debug.htm").getFile();

        File file = new File(filePath);

        ctx.outputAsFile(file);
    }
}
