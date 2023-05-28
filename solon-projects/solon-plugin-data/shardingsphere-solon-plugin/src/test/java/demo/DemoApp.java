package demo;

import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.noear.solon.Solon;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author noear 2023/05/28 created
 */
public class DemoApp {
    public static void main(String[] args) throws SQLException {
        Solon.start(DemoApp.class, args);

        ShardingSphereDataSource shardingSphereDataSource = (ShardingSphereDataSource) Solon.context().getBean(DataSource.class);
        // 读
        Connection connectionRead = shardingSphereDataSource.getConnection();
        Statement statementRead = connectionRead.createStatement();
        ResultSet resultSet = statementRead.executeQuery("select 1");
        System.out.println("execute = " + resultSet);
        statementRead.close();
        connectionRead.close();
        // ---------------------------------------------------------------------------
        // 写
        Connection connectionWrite = shardingSphereDataSource.getConnection();
        connectionWrite.setAutoCommit(false);
        Statement statementWrite = connectionWrite.createStatement();
        statementWrite.executeUpdate("INSERT INTO e_dict (create_by, create_time, update_by, update_time, code, name, remark, erupt_tenant_id)\n" +
                "VALUES (null, null, null, null, null, '1', null, null);\n" +
                "\n");
        connectionWrite.rollback();

        System.out.println("------------------ Sharding Ds 2 ------------------");
        shardingSphereDataSource = (ShardingSphereDataSource) Solon.context().getBean("shardingDs2");
        // 读
        connectionRead = shardingSphereDataSource.getConnection();
        statementRead = connectionRead.createStatement();
        resultSet = statementRead.executeQuery("select 1");
        System.out.println("execute = " + resultSet);
        statementRead.close();
        connectionRead.close();
        // ---------------------------------------------------------------------------
        // 写
        connectionWrite = shardingSphereDataSource.getConnection();
        connectionWrite.setAutoCommit(false);
        statementWrite = connectionWrite.createStatement();
        statementWrite.executeUpdate("INSERT INTO e_dict (create_by, create_time, update_by, update_time, code, name, remark, erupt_tenant_id)\n" +
                "VALUES (null, null, null, null, null, '1', null, null);\n" +
                "\n");
        connectionWrite.rollback();

    }
}
