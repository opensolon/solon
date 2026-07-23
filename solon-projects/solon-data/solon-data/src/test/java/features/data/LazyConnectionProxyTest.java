package features.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.solon.data.datasource.LazyConnectionDataSourceProxy;
import org.noear.solon.data.datasource.LazyConnectionProxy;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * LazyConnectionDataSourceProxy / LazyConnectionProxy 全分支覆盖测试
 *
 * @author noear
 * @since 4.1
 */
public class LazyConnectionProxyTest {

    private DataSource dataSource;
    private Connection realConnection;

    @BeforeEach
    public void setUp() throws SQLException {
        dataSource = mock(DataSource.class);
        realConnection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(realConnection);
        when(dataSource.getConnection(anyString(), anyString())).thenReturn(realConnection);
    }

    // ==================== LazyConnectionDataSourceProxy ====================

    @Test
    public void dataSourceProxy_getConnection_returnsLazyProxy() throws SQLException {
        LazyConnectionDataSourceProxy proxy = new LazyConnectionDataSourceProxy(dataSource);

        Connection conn = proxy.getConnection();

        assertTrue(conn instanceof LazyConnectionProxy);
        assertFalse(((LazyConnectionProxy) conn).hasTarget());
        // 尚未真正取连接
        verify(dataSource, never()).getConnection();
    }

    @Test
    public void dataSourceProxy_getConnectionWithCredentials_returnsLazyProxy() throws SQLException {
        LazyConnectionDataSourceProxy proxy = new LazyConnectionDataSourceProxy(dataSource);

        Connection conn = proxy.getConnection("user", "pass");

        assertTrue(conn instanceof LazyConnectionProxy);
        assertFalse(((LazyConnectionProxy) conn).hasTarget());
        verify(dataSource, never()).getConnection(anyString(), anyString());
    }

    // ==================== 延迟语义核心 ====================

    @Test
    public void noSql_transactionLifecycle_neverFetchesConnection() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        // 模拟 DbTran 初始化
        assertTrue(conn.getAutoCommit());
        conn.setAutoCommit(false);
        assertFalse(conn.isReadOnly());
        conn.setReadOnly(true);
        conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        conn.setCatalog("cat");
        conn.setSchema("sch");

        // 无 SQL，直接提交关闭
        conn.commit();
        conn.rollback();
        conn.close();

        assertFalse(conn.hasTarget());
        assertTrue(conn.isClosed());
        verify(dataSource, never()).getConnection();
        verify(dataSource, never()).getConnection(anyString(), anyString());
    }

    @Test
    public void firstSql_fetchesConnectionAndAppliesStashedSettings() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);
        Statement statement = mock(Statement.class);
        when(realConnection.createStatement()).thenReturn(statement);

        conn.setAutoCommit(false);
        conn.setReadOnly(true);
        conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        conn.setCatalog("mydb");
        conn.setSchema("public");

        assertFalse(conn.hasTarget());

        Statement st = conn.createStatement();
        assertSame(statement, st);
        assertTrue(conn.hasTarget());

        verify(dataSource, times(1)).getConnection();
        verify(realConnection).setReadOnly(true);
        verify(realConnection).setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        verify(realConnection).setAutoCommit(false);
        verify(realConnection).setCatalog("mydb");
        verify(realConnection).setSchema("public");
    }

    @Test
    public void getConnectionWithCredentials_usesCredentialPath() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource, "u1", "p1");
        when(realConnection.prepareStatement("select 1")).thenReturn(mock(PreparedStatement.class));

        conn.prepareStatement("select 1");

        verify(dataSource).getConnection("u1", "p1");
        verify(dataSource, never()).getConnection();
    }

    @Test
    public void getTarget_whenClosed_throwsSqlException() {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        assertDoesNotThrow(conn::close);
        SQLException ex = assertThrows(SQLException.class, conn::getTarget);
        assertTrue(ex.getMessage().contains("connection is closed"));
    }

    @Test
    public void getTarget_secondCall_returnsSameTarget() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        Connection t1 = conn.getTarget();
        Connection t2 = conn.getTarget();

        assertSame(t1, t2);
        verify(dataSource, times(1)).getConnection();
    }

    @Test
    public void getTarget_withoutStashedSettings_skipsApply() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        conn.getTarget();

        verify(realConnection, never()).setReadOnly(anyBoolean());
        verify(realConnection, never()).setTransactionIsolation(anyInt());
        verify(realConnection, never()).setAutoCommit(anyBoolean());
        verify(realConnection, never()).setCatalog(anyString());
        verify(realConnection, never()).setSchema(anyString());
    }

    // ==================== 属性暂存 / 委托 ====================

    @Test
    public void autoCommit_stashAndDefault() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        assertTrue(conn.getAutoCommit()); // default
        conn.setAutoCommit(false);
        assertFalse(conn.getAutoCommit());
        conn.setAutoCommit(true);
        assertTrue(conn.getAutoCommit());
        assertFalse(conn.hasTarget());
    }

    @Test
    public void autoCommit_withTarget_delegates() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);
        conn.getTarget();

        when(realConnection.getAutoCommit()).thenReturn(false);
        conn.setAutoCommit(false);
        assertFalse(conn.getAutoCommit());

        verify(realConnection).setAutoCommit(false);
        verify(realConnection).getAutoCommit();
    }

    @Test
    public void readOnly_stashAndDefault() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        assertFalse(conn.isReadOnly()); // default
        conn.setReadOnly(true);
        assertTrue(conn.isReadOnly());
        conn.setReadOnly(false);
        assertFalse(conn.isReadOnly());
        assertFalse(conn.hasTarget());
    }

    @Test
    public void readOnly_withTarget_delegates() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);
        conn.getTarget();

        when(realConnection.isReadOnly()).thenReturn(true);
        conn.setReadOnly(true);
        assertTrue(conn.isReadOnly());

        verify(realConnection).setReadOnly(true);
        verify(realConnection).isReadOnly();
    }

    @Test
    public void transactionIsolation_stashAndDefault() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        assertEquals(Connection.TRANSACTION_READ_COMMITTED, conn.getTransactionIsolation());
        conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        assertEquals(Connection.TRANSACTION_SERIALIZABLE, conn.getTransactionIsolation());
        assertFalse(conn.hasTarget());
    }

    @Test
    public void transactionIsolation_withTarget_delegates() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);
        conn.getTarget();

        when(realConnection.getTransactionIsolation()).thenReturn(Connection.TRANSACTION_REPEATABLE_READ);
        conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        assertEquals(Connection.TRANSACTION_REPEATABLE_READ, conn.getTransactionIsolation());

        verify(realConnection).setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        verify(realConnection).getTransactionIsolation();
    }

    @Test
    public void catalog_stashAndDelegate() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        assertNull(conn.getCatalog());
        conn.setCatalog("c1");
        assertEquals("c1", conn.getCatalog());
        assertFalse(conn.hasTarget());

        // after target
        when(realConnection.getCatalog()).thenReturn("c2");
        conn.getTarget();
        conn.setCatalog("c2");
        assertEquals("c2", conn.getCatalog());
        verify(realConnection).setCatalog("c2");
        verify(realConnection).getCatalog();
    }

    @Test
    public void schema_stashAndDelegate() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        assertNull(conn.getSchema());
        conn.setSchema("s1");
        assertEquals("s1", conn.getSchema());
        assertFalse(conn.hasTarget());

        when(realConnection.getSchema()).thenReturn("s2");
        conn.getTarget();
        conn.setSchema("s2");
        assertEquals("s2", conn.getSchema());
        verify(realConnection).setSchema("s2");
        verify(realConnection).getSchema();
    }

    // ==================== 事务控制 ====================

    @Test
    public void commit_rollback_withAndWithoutTarget() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        // without target
        conn.commit();
        conn.rollback();
        conn.rollback(mock(Savepoint.class));
        verify(realConnection, never()).commit();
        verify(realConnection, never()).rollback();
        verify(realConnection, never()).rollback(any(Savepoint.class));

        // with target
        conn.getTarget();
        Savepoint sp = mock(Savepoint.class);
        conn.commit();
        conn.rollback();
        conn.rollback(sp);
        verify(realConnection).commit();
        verify(realConnection).rollback();
        verify(realConnection).rollback(sp);
    }

    @Test
    public void close_idempotent_withoutTarget() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        assertFalse(conn.isClosed());
        conn.close();
        assertTrue(conn.isClosed());
        conn.close(); // 二次关闭不抛异常
        assertTrue(conn.isClosed());
        verify(dataSource, never()).getConnection();
    }

    @Test
    public void close_withTarget_closesRealConnection() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);
        conn.getTarget();

        conn.close();
        assertTrue(conn.isClosed());
        verify(realConnection).close();

        conn.close(); // 二次关闭不再调用 target.close
        verify(realConnection, times(1)).close();
    }

    @Test
    public void isClosed_delegatesToTargetWhenOpen() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);
        conn.getTarget();

        when(realConnection.isClosed()).thenReturn(false);
        assertFalse(conn.isClosed());
        when(realConnection.isClosed()).thenReturn(true);
        assertTrue(conn.isClosed());
    }

    @Test
    public void warnings_withoutAndWithTarget() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        assertNull(conn.getWarnings());
        conn.clearWarnings();
        assertFalse(conn.hasTarget());

        SQLWarning warning = new SQLWarning("w");
        when(realConnection.getWarnings()).thenReturn(warning);
        conn.getTarget();
        assertSame(warning, conn.getWarnings());
        conn.clearWarnings();
        verify(realConnection).clearWarnings();
    }

    // ==================== 触发取连接的方法 ====================

    @Test
    public void statementFactories_triggerFetch() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);
        Statement st = mock(Statement.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        CallableStatement cs = mock(CallableStatement.class);

        when(realConnection.createStatement()).thenReturn(st);
        when(realConnection.createStatement(anyInt(), anyInt())).thenReturn(st);
        when(realConnection.createStatement(anyInt(), anyInt(), anyInt())).thenReturn(st);
        when(realConnection.prepareStatement(anyString())).thenReturn(ps);
        when(realConnection.prepareStatement(anyString(), anyInt())).thenReturn(ps);
        when(realConnection.prepareStatement(anyString(), anyInt(), anyInt())).thenReturn(ps);
        when(realConnection.prepareStatement(anyString(), anyInt(), anyInt(), anyInt())).thenReturn(ps);
        when(realConnection.prepareStatement(anyString(), any(int[].class))).thenReturn(ps);
        when(realConnection.prepareStatement(anyString(), any(String[].class))).thenReturn(ps);
        when(realConnection.prepareCall(anyString())).thenReturn(cs);
        when(realConnection.prepareCall(anyString(), anyInt(), anyInt())).thenReturn(cs);
        when(realConnection.prepareCall(anyString(), anyInt(), anyInt(), anyInt())).thenReturn(cs);

        assertSame(st, conn.createStatement());
        assertSame(st, conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY));
        assertSame(st, conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT));
        assertSame(ps, conn.prepareStatement("sql1"));
        assertSame(ps, conn.prepareStatement("sql2", Statement.RETURN_GENERATED_KEYS));
        assertSame(ps, conn.prepareStatement("sql3", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY));
        assertSame(ps, conn.prepareStatement("sql4", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT));
        assertSame(ps, conn.prepareStatement("sql5", new int[]{1}));
        assertSame(ps, conn.prepareStatement("sql6", new String[]{"id"}));
        assertSame(cs, conn.prepareCall("{call x}"));
        assertSame(cs, conn.prepareCall("{call y}", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY));
        assertSame(cs, conn.prepareCall("{call z}", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT));

        verify(dataSource, times(1)).getConnection();
    }

    @Test
    public void metadataAndNativeSql_triggerFetch() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);
        DatabaseMetaData meta = mock(DatabaseMetaData.class);
        when(realConnection.getMetaData()).thenReturn(meta);
        when(realConnection.nativeSQL("select 1")).thenReturn("select 1");

        assertSame(meta, conn.getMetaData());
        assertEquals("select 1", conn.nativeSQL("select 1"));
        assertTrue(conn.hasTarget());
    }

    @Test
    public void typeMapAndHoldability_triggerFetch() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);
        Map<String, Class<?>> typeMap = new HashMap<>();
        when(realConnection.getTypeMap()).thenReturn(typeMap);
        when(realConnection.getHoldability()).thenReturn(ResultSet.HOLD_CURSORS_OVER_COMMIT);

        assertSame(typeMap, conn.getTypeMap());
        conn.setTypeMap(typeMap);
        conn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
        assertEquals(ResultSet.HOLD_CURSORS_OVER_COMMIT, conn.getHoldability());

        verify(realConnection).setTypeMap(typeMap);
        verify(realConnection).setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    @Test
    public void savepointMethods_triggerFetch() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);
        Savepoint sp1 = mock(Savepoint.class);
        Savepoint sp2 = mock(Savepoint.class);
        when(realConnection.setSavepoint()).thenReturn(sp1);
        when(realConnection.setSavepoint("sp")).thenReturn(sp2);

        assertSame(sp1, conn.setSavepoint());
        assertSame(sp2, conn.setSavepoint("sp"));
        conn.releaseSavepoint(sp1);

        verify(realConnection).releaseSavepoint(sp1);
    }

    @Test
    public void createSqlObjects_triggerFetch() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);
        Clob clob = mock(Clob.class);
        Blob blob = mock(Blob.class);
        NClob nclob = mock(NClob.class);
        SQLXML sqlxml = mock(SQLXML.class);
        Array array = mock(Array.class);
        Struct struct = mock(Struct.class);

        when(realConnection.createClob()).thenReturn(clob);
        when(realConnection.createBlob()).thenReturn(blob);
        when(realConnection.createNClob()).thenReturn(nclob);
        when(realConnection.createSQLXML()).thenReturn(sqlxml);
        when(realConnection.createArrayOf(eq("varchar"), any())).thenReturn(array);
        when(realConnection.createStruct(eq("obj"), any())).thenReturn(struct);

        assertSame(clob, conn.createClob());
        assertSame(blob, conn.createBlob());
        assertSame(nclob, conn.createNClob());
        assertSame(sqlxml, conn.createSQLXML());
        assertSame(array, conn.createArrayOf("varchar", new Object[]{"a"}));
        assertSame(struct, conn.createStruct("obj", new Object[]{1}));
    }

    @Test
    public void isValid_withoutAndWithTarget() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        assertTrue(conn.isValid(1));
        assertFalse(conn.hasTarget());

        when(realConnection.isValid(2)).thenReturn(false);
        conn.getTarget();
        assertFalse(conn.isValid(2));
    }

    @Test
    public void clientInfo_successAndFailure() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);
        Properties props = new Properties();
        props.setProperty("k", "v");

        when(realConnection.getClientInfo("k")).thenReturn("v");
        when(realConnection.getClientInfo()).thenReturn(props);

        conn.setClientInfo("k", "v");
        conn.setClientInfo(props);
        assertEquals("v", conn.getClientInfo("k"));
        assertSame(props, conn.getClientInfo());

        // failure path: target 抛 SQLClientInfoException（继承 SQLException）被重新包装
        doThrow(new SQLClientInfoException("fail-name", Collections.emptyMap()))
                .when(realConnection).setClientInfo("bad", "x");
        SQLClientInfoException ex1 = assertThrows(SQLClientInfoException.class,
                () -> conn.setClientInfo("bad", "x"));
        assertTrue(ex1.getMessage().contains("fail-name"));

        doThrow(new SQLClientInfoException("fail-props", Collections.emptyMap()))
                .when(realConnection).setClientInfo(any(Properties.class));
        SQLClientInfoException ex2 = assertThrows(SQLClientInfoException.class,
                () -> conn.setClientInfo(props));
        assertTrue(ex2.getMessage().contains("fail-props"));
    }

    @Test
    public void abort_withoutAndWithTarget() throws SQLException {
        Executor executor = Executors.newSingleThreadExecutor();

        LazyConnectionProxy conn1 = new LazyConnectionProxy(dataSource);
        conn1.abort(executor);
        assertTrue(conn1.isClosed());
        verify(realConnection, never()).abort(any());

        LazyConnectionProxy conn2 = new LazyConnectionProxy(dataSource);
        conn2.getTarget();
        conn2.abort(executor);
        assertTrue(conn2.isClosed());
        verify(realConnection).abort(executor);

        // abort 异常时也要标记 closed
        LazyConnectionProxy conn3 = new LazyConnectionProxy(dataSource);
        conn3.getTarget();
        doThrow(new SQLException("abort-fail")).when(realConnection).abort(executor);
        assertThrows(SQLException.class, () -> conn3.abort(executor));
        assertTrue(conn3.isClosed());
    }

    @Test
    public void networkTimeout_successAndUnsupported() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);
        Executor executor = Runnable::run;

        when(realConnection.getNetworkTimeout()).thenReturn(1000);
        conn.setNetworkTimeout(executor, 1000);
        assertEquals(1000, conn.getNetworkTimeout());

        // 驱动不支持：吞掉异常
        doThrow(new AbstractMethodError("unsupported")).when(realConnection)
                .setNetworkTimeout(any(), anyInt());
        assertDoesNotThrow(() -> conn.setNetworkTimeout(executor, 2000));

        when(realConnection.getNetworkTimeout()).thenThrow(new AbstractMethodError("unsupported"));
        assertEquals(0, conn.getNetworkTimeout());
    }

    // ==================== unwrap / isWrapperFor / toString ====================

    @Test
    public void unwrap_selfWithoutFetching() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        assertSame(conn, conn.unwrap(LazyConnectionProxy.class));
        assertSame(conn, conn.unwrap(Connection.class));
        assertTrue(conn.isWrapperFor(LazyConnectionProxy.class));
        assertTrue(conn.isWrapperFor(Connection.class));
        assertFalse(conn.hasTarget());
    }

    @Test
    public void unwrap_otherInterface_delegatesToTarget() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);
        when(realConnection.unwrap(DatabaseMetaData.class)).thenReturn(mock(DatabaseMetaData.class));
        when(realConnection.isWrapperFor(DatabaseMetaData.class)).thenReturn(true);

        assertNotNull(conn.unwrap(DatabaseMetaData.class));
        assertTrue(conn.isWrapperFor(DatabaseMetaData.class));
        assertTrue(conn.hasTarget());
    }

    @Test
    public void toString_withAndWithoutTarget() throws SQLException {
        LazyConnectionProxy conn = new LazyConnectionProxy(dataSource);

        assertEquals("LazyConnectionProxy{target=not-fetched-yet}", conn.toString());

        when(realConnection.toString()).thenReturn("RealConn");
        conn.getTarget();
        assertEquals("LazyConnectionProxy{target=" + realConnection + "}", conn.toString());
    }

    // ==================== 综合场景 ====================

    @Test
    public void fullFlow_withSql_commitAndClose() throws SQLException {
        LazyConnectionDataSourceProxy dsProxy = new LazyConnectionDataSourceProxy(dataSource);
        LazyConnectionProxy conn = (LazyConnectionProxy) dsProxy.getConnection();
        PreparedStatement ps = mock(PreparedStatement.class);
        when(realConnection.prepareStatement("insert into t values(1)")).thenReturn(ps);

        // DbTran 风格初始化
        conn.setAutoCommit(false);
        conn.setReadOnly(false);

        // 执行 SQL
        PreparedStatement statement = conn.prepareStatement("insert into t values(1)");
        assertSame(ps, statement);
        assertTrue(conn.hasTarget());

        // 提交关闭
        conn.commit();
        conn.close();

        verify(realConnection).setAutoCommit(false);
        verify(realConnection).setReadOnly(false);
        verify(realConnection).commit();
        verify(realConnection).close();
    }
}
