/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.data.sqlink.api.crud.create;

import org.noear.solon.data.sqlink.api.crud.CRUD;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.SqLinkDialect;
import org.noear.solon.data.sqlink.base.annotation.GenerateStrategy;
import org.noear.solon.data.sqlink.base.annotation.InsertDefaultValue;
import org.noear.solon.data.sqlink.base.generate.DynamicGenerator;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.session.SqlSession;
import org.noear.solon.data.sqlink.base.session.SqlValue;
import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;
import org.noear.solon.data.sqlink.core.sqlBuilder.InsertSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public abstract class InsertBase extends CRUD {
    public final static Logger log = LoggerFactory.getLogger(InsertBase.class);

    private final InsertSqlBuilder sqlBuilder;

    protected InsertSqlBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    protected SqLinkConfig getConfig() {
        return sqlBuilder.getConfig();
    }

    public InsertBase(SqLinkConfig c) {
        this.sqlBuilder = new InsertSqlBuilder(c);
    }

    /**
     * 执行sql语句
     *
     * @return 执行后的结果
     */
    public long executeRows() {
        List<Object> objects = getObjects();
        if (!objects.isEmpty()) {
            return objectsExecuteRows();
        }
        else {
            return 0;
        }
    }

    public String toSql() {
        if (!getObjects().isEmpty()) {
            return makeByObjects(MetaDataCache.getMetaData(getTableType()).getNotIgnorePropertys(), null);
        }
        else {
            return sqlBuilder.getSql();
        }
    }

    protected <T> List<T> getObjects() {
        return Collections.emptyList();
    }

    protected abstract <T> Class<T> getTableType();

    private long objectsExecuteRows() {
        MetaData metaData = MetaDataCache.getMetaData(getTableType());
        List<FieldMetaData> notIgnorePropertys = metaData.getNotIgnorePropertys();
        SqLinkConfig config = getConfig();
        List<SqlValue> sqlValues = new ArrayList<>();
        String sql = makeByObjects(notIgnorePropertys, sqlValues);
        //tryPrintUseDs(log,config.getDataSourceManager().getDsKey());
        tryPrintSql(log, sql);
        SqlSession session = config.getSqlSessionFactory().getSession();

        if (getObjects().size() > 1) {
            tryPrintBatch(log, getObjects().size());
        }
        else {
            tryPrintNoBatch(log, getObjects().size());
        }

        return session.executeInsert(sql, sqlValues, notIgnorePropertys.size());
    }

    private String makeByObjects(List<FieldMetaData> notIgnorePropertys, List<SqlValue> sqlValues) {
        MetaData metaData = MetaDataCache.getMetaData(getTableType());
        List<String> tableFields = new ArrayList<>();
        List<String> tableValues = new ArrayList<>();
        for (FieldMetaData fieldMetaData : notIgnorePropertys) {
            InsertDefaultValue insertDefaultValue = fieldMetaData.getInsertDefaultValue();
            // 如果不是数据库生成策略，则添加
            if (insertDefaultValue == null || insertDefaultValue.strategy() != GenerateStrategy.DataBase) {
                tableFields.add(fieldMetaData.getColumn());
                tableValues.add("?");
            }
        }
        if (sqlValues != null) {
            for (Object object : getObjects()) {
                for (FieldMetaData fieldMetaData : notIgnorePropertys) {
                    Object value = fieldMetaData.getValueByObject(object);
                    ITypeHandler<?> typeHandler = fieldMetaData.getTypeHandler();
                    InsertDefaultValue onInsert = fieldMetaData.getInsertDefaultValue();
                    if (onInsert != null) {
                        switch (onInsert.strategy()) {
                            case DataBase:
                                // 交给数据库
                                break;
                            case Static:
                                if (value == null) {
                                    value = typeHandler.castStringToTarget(onInsert.value());
                                }
                                sqlValues.add(new SqlValue(value, typeHandler,fieldMetaData.getOnInsert()));
                                break;
                            case Dynamic:
                                if (value == null) {
                                    DynamicGenerator generator = DynamicGenerator.get(onInsert.dynamic());
                                    value = generator.generate(getConfig(), fieldMetaData);
                                }
                                sqlValues.add(new SqlValue(value, typeHandler,fieldMetaData.getOnInsert()));
                                break;
                        }
                    }
                    else {
                        sqlValues.add(new SqlValue(value, typeHandler,fieldMetaData.getOnInsert()));
                    }
                }
            }
        }
        SqLinkDialect dialect = getSqlBuilder().getConfig().getDisambiguation();
        return "INSERT INTO " + dialect.disambiguationTableName(metaData.getTableName()) + "(" + String.join(",", tableFields)
                + ") VALUES(" + String.join(",", tableValues) + ")";
    }
}
