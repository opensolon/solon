import org.noear.solon.boot.web.MimeType;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.DownloadedFile;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author noear 2023/2/15 created
 */
public class ZipTest {
    public void test(Context ctx) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ctx.headerSet("Content-Disposition", "attachment; filename=\"test.json.zip\"");
        ctx.contentType("application/zip");

        //创建压缩包
        ZipOutputStream zipOutputStream = new ZipOutputStream(ctx.outputStream());


        //压缩包里创建一个空文件
        zipOutputStream.putNextEntry(new ZipEntry("test.json"));

        byte[] bytes = "{code:1}".getBytes();

        //写入压缩文件
        zipOutputStream.write(bytes, 0, bytes.length);
        //关闭压缩包打包
        zipOutputStream.closeEntry();

        zipOutputStream.flush();
        zipOutputStream.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());


        DownloadedFile downloadedFile = new org.noear.solon.core.handle.DownloadedFile("zip", inputStream, "test.json.zip");

        ctx.outputAsFile(downloadedFile);
    }
}
