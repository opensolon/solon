package webapp.demo2_cache;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CachePut;
import org.noear.solon.data.annotation.CacheRemove;
import webapp.models.UserModel;

import java.time.LocalDateTime;
import java.util.Date;

@Controller
public class CacheController4 {
    @Cache(tags = "test4_user,test4_${.id}", seconds = 600)
    @Mapping("/cache4/cache")
    public UserModel cache() {
        UserModel u = new UserModel();

        u.setId(12);
        u.setDate(new Date());

        return u;
    }

    @CacheRemove(tags = "test4_${id}")
    @Mapping("/cache4/remove")
    public int remove(int id) {
        return id;
    }
}
