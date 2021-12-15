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

        DownloadedFile file = new DownloadedFile("text/json", stream, "没有耳多 test.json");

        return file;
    }

    @Mapping("f12")
    public DownloadedFile down12() {
        return new DownloadedFile("text/json",
                "test".getBytes(StandardCharsets.UTF_8),
                "test.txt");

    }

    @Mapping("f2")
    public File down2() {
        String filePath = Utils.getResource("static/debug.htm").getFile();

        File file = new File(filePath);

        return file;
    }

    @Mapping("f3")
    public void down3(Context ctx) throws IOException {
        String filePath = Utils.getResource("static/debug.htm").getFile();

        File file = new File(filePath);

        ctx.outputAsFile(file);
    }
}
