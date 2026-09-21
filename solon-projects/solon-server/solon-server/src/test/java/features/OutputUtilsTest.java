package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.server.util.OutputUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author dyrnq 2026/9/20 created
 */
public class OutputUtilsTest {

    /**
     * 模拟 gzip 压缩输出（同 ContextBase::outputStreamAsGzip）
     */
    private static class GzipContext extends ContextEmpty {
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        @Override
        public OutputStream outputStream() {
            return buffer;
        }

        @Override
        public GZIPOutputStream outputStreamAsGzip() throws IOException {
            headerSet("Vary", "Accept-Encoding");
            headerSet("Content-Encoding", "gzip");
            return new GZIPOutputStream(outputStream(), 4096, true);
        }

        public byte[] outputBytes() {
            return buffer.toByteArray();
        }
    }

    /**
     * 解压输出内容
     */
    private static byte[] ungzip(byte[] bytes) throws IOException {
        try (GZIPInputStream gzipIn = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            return IoUtil.transferToBytes(gzipIn);
        }
    }

    @Test
    public void outputStreamAsGzip_case1() throws IOException {
        //可压缩内容（重复度很高）
        byte[] data = new byte[12000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) ('a' + (i % 26));
        }

        GzipContext ctx = new GzipContext();
        OutputUtils.global().outputStreamAsGzip(ctx, new ByteArrayInputStream(data));

        assertEquals("gzip", ctx.headerOfResponse("Content-Encoding"));

        //输出必须是完整的 gzip 流：缺了结束块和 CRC32/ISIZE 时，这里会抛 EOFException
        assertArrayEquals(data, ungzip(ctx.outputBytes()));
    }

    @Test
    public void outputStreamAsGzip_case2() throws IOException {
        //不可压缩内容（压缩后比原文还大）
        byte[] data = new byte[8192];
        new Random(1).nextBytes(data);

        GzipContext ctx = new GzipContext();
        OutputUtils.global().outputStreamAsGzip(ctx, new ByteArrayInputStream(data));

        assertArrayEquals(data, ungzip(ctx.outputBytes()));
    }

    @Test
    public void outputStreamAsGzip_case3() throws IOException {
        //空内容，也要能解出一个空流
        byte[] data = new byte[0];

        GzipContext ctx = new GzipContext();
        OutputUtils.global().outputStreamAsGzip(ctx, new ByteArrayInputStream(data));

        assertArrayEquals(data, ungzip(ctx.outputBytes()));
    }
}
