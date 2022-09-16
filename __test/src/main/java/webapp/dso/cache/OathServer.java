package webapp.dso.cache;

import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.aspect.annotation.Service;

import java.time.LocalDateTime;

/**
 * @author noear 2022/1/15 created
 */
@Service
public class OathServer {
    @Cache(key = "oath_test_${code}", seconds = 2592000)
    public Oauth queryInfoByCode(String code) {
        Oauth oauth = new Oauth();
        oauth.setCode(code);
        oauth.setExpTime(LocalDateTime.now());

        return oauth;
    }


    @CacheRemove(keys = "oath_test_${.code}")
    public Oauth updateInfo2(Oauth oauth) {
        return oauth;
    }

    @CacheRemove(keys = "oath_test_${oauth.code}")
    public void updateInfo(Oauth oauth) {

    }
}
