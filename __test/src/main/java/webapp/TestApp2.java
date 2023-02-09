package webapp;

import org.noear.solon.core.util.ResourceUtil;

import java.util.Collection;

/**
 * @author noear 2022/12/22 created
 */
public class TestApp2 {
    public static void main(String[] args){
        Collection<String> paths = ResourceUtil.resolvePaths("static_test/**/dir2/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.resolvePaths("static_test/**/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 2;
    }
}
