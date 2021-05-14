package webapp.demo2_cache;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.extend.data.annotation.Cache;
import org.noear.solon.extend.data.annotation.CachePut;
import org.noear.solon.extend.data.annotation.CacheRemove;

import java.util.Date;

@Controller
public class CacheController {
    /**
     * 执行结果缓存10秒，并添加 test_${label} 和 test1 标签
     * */
    @Cache(tags = "test_${label},test1" , seconds = 60)
    @Mapping("/cache/")
    public Date test(int label) {
        return new Date();
    }

    /**
     * 执行后，清除 标签为 test  的缓存（不过，目前没有 test 的示签...）
     * */
    @CachePut(tags = "test1")
    @Mapping("/cache/update")
    public Date update() {
        return new Date();
    }

    /**
     * 执行后，清除 标签为 test_${label}  的缓存
     * */
    @CacheRemove(tags = "test_${label}")
    @Mapping("/cache/remove")
    public String remove(int label) {
        return "清除成功-" + new Date();
    }
}
