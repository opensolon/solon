package features;

import org.noear.solon.web.staticfiles.StaticRepository;

/**
 * @author noear 2022/12/7 created
 */
public class PreheatDemo {
    StaticRepository staticRepository = null;

    public void demo() throws Exception {
        staticRepository.preheat("demo/file.htm", false);
        staticRepository.preheat("demo/file.js", false);
        staticRepository.preheat("demo/file.pin", false);

    }
}
