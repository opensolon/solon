package org.noear.solon.extend.activerecord.proxy;

import java.lang.reflect.Parameter;

/**
 * Mapper 中各方法上下文
 *
 * @author 胡高 (https://gitee.com/gollyhu)
 * @since 1.10
 */
public class MapperMethodContext {
    private String sql;
    private Parameter[] parameters;
    private Class<?> returnClz;
    private boolean isReturnPage;
    private boolean isReturnList;
    private boolean isSqlStatement;
    private boolean isUpdate;

    public MapperMethodContext(String sqlStatement, Parameter[] parameters, Class<?> returnClz, boolean isReturnList,
        boolean isReturnPage, boolean isSqlStatement, boolean isUpdate) {
        this.sql = sqlStatement;
        this.parameters = parameters;
        this.returnClz = returnClz;
        this.isReturnList = isReturnList;
        this.isReturnPage = isReturnPage;
        this.isSqlStatement = isSqlStatement;
        this.isUpdate = isUpdate;
    }

    /**
     * @return 方法参数
     */
    public Parameter[] getParameters() {
        return this.parameters;
    }

    /**
     * @return 返回的类型（泛型类型）
     */
    public Class<?> getReturnClz() {
        return this.returnClz;
    }

    /**
     * @return isStatement 为true时则是SQL语句，否则为SQL模板名
     */
    public String getSql() {
        return this.sql;
    }

    /**
     * @return 返回值是否为List<?>对象
     */
    public boolean isReturnList() {
        return this.isReturnList;
    }

    /**
     * @return 返回值是否为JFinal的Page<?>对象
     */
    public boolean isReturnPage() {
        return this.isReturnPage;
    }

    /**
     * @return 标识sql参数是否为SQL语句
     */
    public boolean isSqlStatement() {
        return this.isSqlStatement;
    }

    public boolean isUpdate() {
        return this.isUpdate;
    }

}
