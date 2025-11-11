package demo.sqlutils;

import org.noear.solon.data.sql.SqlUtils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 *
 * @author noear 2025/11/10 created
 *
 */
public class Demo {
    public void demo(Connection connection, String sql) throws Exception {
        try(ConnectionDataSource ds = new ConnectionDataSource(connection)){
            SqlUtils.of(ds).sql(sql).update();
        }
    }


    public static class ConnectionDataSource implements DataSource, AutoCloseable {
        private Connection connection;

        public ConnectionDataSource(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return connection;
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return connection;
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return false;
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {

        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {

        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }

        @Override
        public void close() throws Exception {
            connection.close();
        }
    }
}
