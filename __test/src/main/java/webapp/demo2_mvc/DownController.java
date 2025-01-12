/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
