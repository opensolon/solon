package mybatis.nativex;

import mybatis.nativex.app.MybatisApp;
import mybatis.nativex.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import org.noear.solon.test.aot.RuntimeNativeMetadataAssert;

/**
 * @author songyinyin
 * @since 2023/10/29 18:20
 */
@SolonTest(value = MybatisApp.class, isAot = true)
@ExtendWith(SolonJUnit5Extension.class)
public class NativeTest {

    @Test
    public void test() {
        RuntimeNativeMetadataAssert.hasJdkProxyType(UserMapper.class);
    }
}
