package webapp.dso.cache;

import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.extend.aspect.annotation.Service;

import java.time.LocalDateTime;

/**
 * @author noear 2022/1/15 created
 */
@Service
public class OathServer2 {
    @Cache(key = "oath2_test_${code}", seconds = 2592000)
    public Oauth queryInfoByCode(String code) {
        Oauth oauth = new Oauth();
        oauth.setCode(code);
        oauth.setExpTime(LocalDateTime.now());

        return oauth;
    }

    @CacheRemove(keys = "oath2_test_${.code}")
    public Oauth updateInfo2(Oauth oauth) {
        return oauth;
    }
}
