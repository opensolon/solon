package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.DownloadedFile;

import java.io.File;
import java.io.IOException;

/**
 * @author noear 2023/8/23 created
 */
@Controller
public class DownController {
    @Mapping("/demo2/range1/")
    public DownloadedFile range1() throws IOException{
        return new DownloadedFile(new File("/Users/noear/Movies/range_test2.mp4"));
    }

    @Mapping("/demo2/range2/")
    public DownloadedFile range2() throws IOException {
        return new DownloadedFile(new File("/Users/noear/Movies/range_test2.mp4"))
                .asAttachment(false);
    }
}
