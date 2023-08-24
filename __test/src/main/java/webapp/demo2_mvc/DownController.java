package webapp.demo2_mvc;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.DownloadedFile;

import java.io.File;
import java.io.IOException;

/**
 * @author noear 2023/8/23 created
 */
@Slf4j
@Controller
public class DownController {
    @Mapping("/demo2/range1/")
    public DownloadedFile range1(Context ctx) throws IOException {
        log.debug(ctx.method() + ":: " + ctx.header("Range"));
        return new DownloadedFile(new File("/Users/noear/Movies/range_test.mov"));
    }

    @Mapping("/demo2/range2/")
    public DownloadedFile range2() throws IOException {
        return new DownloadedFile(new File("/Users/noear/Movies/range_test2.mp4"))
                .asAttachment(false);
    }
}
