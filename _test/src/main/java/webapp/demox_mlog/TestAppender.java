package webapp.demox_mlog;

import org.noear.mlog.Level;
import org.noear.mlog.Metainfo;
import org.noear.solon.logging.LogAbstractAppender;

/**
 * @author noear 2021/2/25 created
 */
public class TestAppender extends LogAbstractAppender {
    @Override
    public String getName() {
        return "test";
    }


    @Override
    protected void appendDo(String loggerName, Class<?> clz, Level level, Metainfo metainfo, Object content) {
        System.out.println("[Test] " + content);
    }
}
