package webapp;

import org.noear.solon.Utils;

import java.util.Collection;

/**
 * @author noear 2022/12/22 created
 */
public class TestApp2 {
    public static void main(String[] args){
        Collection<String> paths = Utils.resolvePaths("static_test/**/dir2/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = Utils.resolvePaths("static_test/**/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 2;
    }
}
