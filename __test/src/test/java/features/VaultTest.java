package features;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import org.noear.solon.vault.annotation.VaultInject;

/**
 * @author noear 2022/9/24 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
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
