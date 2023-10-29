package mybatis.nativex;

import mybatis.nativex.app.MybatisApp;
import mybatis.nativex.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import org.noear.solon.test.aot.RuntimeNativeMetadataAssert;

/**
 * @author songyinyin
 * @since 2023/10/29 18:20
 */
@SolonTest(value = MybatisApp.class, isAot = true)
@RunWith(SolonJUnit4ClassRunner.class)
public class NativeTest {

    @Test
    public void test() {
        RuntimeNativeMetadataAssert.hasJdkProxyType(UserMapper.class);
    }
}
