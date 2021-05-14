package webapp.demo2_cache;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.extend.data.annotation.Cache;
import org.noear.solon.extend.data.annotation.CachePut;
import org.noear.solon.extend.data.annotation.CacheRemove;

import java.time.LocalDateTime;
import java.util.Date;

@Controller
public class CacheController2 {
    /**
     * 执行结果缓存10秒，并添加 test_${label} 和 test1 标签
     */
    @Cache(tags = "test2_${label}", seconds = 600)
    @Mapping("/cache2/cache")
    public String cache(int label) {
        return LocalDateTime.now().toString();
    }

    @Cache(tags = "test2", seconds = 600)
    @Mapping("/cache2/cache2")
    public String cache2() {
        return LocalDateTime.now().toString();
    }


    /**
     * 执行后，清除 标签为 test_${label}  的缓存
     */
    @CachePut(tags = "test2_${label}")
    @CacheRemove(tags = "test2")
    @Mapping("/cache2/update")
    public String remove(int label) {
        return "清除成功-" + new Date();
    }
}
