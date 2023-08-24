package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.RangeUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author noear 2023/8/23 created
 */
@Controller
public class DownController {
    @Mapping("/demo2/range1/")
    public File range1() {
        return new File("/Users/noear/Movies/range_test2.mp4");
    }

    @Mapping("/demo2/range2/")
    public void range2(Context ctx) throws IOException {
        RangeUtil.global().outputFile(ctx, new File("/Users/noear/Movies/range_test2.mp4"), false);
    }
}
