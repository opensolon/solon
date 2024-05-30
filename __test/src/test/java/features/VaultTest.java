package features;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import org.noear.solon.vault.annotation.VaultInject;
import webapp.App;

/**
 * @author noear 2022/9/24 created
 */
@SolonTest(App.class)
public class VaultTest {
    @VaultInject("${vault.test.db1}")
    HikariDataSource dsTmp;

    @VaultInject("${vault.test.db1.password}")
    private String password2;

    @Test
    public void test(){
        assert "root".equals(dsTmp.getUsername());
        assert "123456".equals(dsTmp.getPassword());
        assert "123456".equals(password2);
    }
}
