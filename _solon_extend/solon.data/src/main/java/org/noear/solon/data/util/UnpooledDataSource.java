package org.noear.solon.data.util;

import org.noear.solon.Utils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * 无池数据源
 *
 * @author noear
 * @since 1.6
 */
public class UnpooledDataSource implements DataSource {
    private PrintWriter logWriter;
    private String url;
    private String username;
    private String password;
    private String driverClassName;

    public UnpooledDataSource(Properties props) {
        this.url = props.getProperty("url");
        if (Utils.isEmpty(this.url)) {
            this.url = props.getProperty("jdbcUrl");
        }

        if (Utils.isEmpty(this.url)) {
            throw new IllegalArgumentException("Invalid ds url parameter");
        }

        this.logWriter = new PrintWriter(System.out);

        this.username = props.getProperty("username");
        this.password = props.getProperty("password");

        setDriverClassName(driverClassName);
    }

    public UnpooledDataSource(String url, String username, String password, String driverClassName) {
        if (Utils.isEmpty(url)) {
            throw new IllegalArgumentException("Invalid ds url parameter");
        }

        this.logWriter = new PrintWriter(System.out);

        this.url = url;
        this.username = username;
        this.password = password;

        setDriverClassName(driverClassName);
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDriverClassName(String driverClassName) {
        if (driverClassName == null) {
            return;
        }

        try {
            this.driverClassName = driverClassName;
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (username == null) {
            return DriverManager.getConnection(url);
        } else {
            return DriverManager.getConnection(url, username, password);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
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
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        logWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnpooledDataSource that = (UnpooledDataSource) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, username, password);
    }
}