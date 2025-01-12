/*
 * Copyright 2017-2025 noear.org and authors
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
package org.noear.solon.data.sqlink.integration.configuration;

import org.noear.solon.annotation.Configuration;
//import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.core.Option;

/**
 * @author kiryu1223
 * @since 3.0
 */
@Configuration
public class SqLinkProperties {
    //private DbType dbType;
    private boolean printSql = true;
    private boolean printBatch = false;
    private boolean ignoreUpdateNoWhere = false;
    private boolean ignoreDeleteNoWhere = false;

//    public DbType getDbType() {
//        return dbType;
//    }
//
//    public void setDbType(DbType dbType) {
//        this.dbType = dbType;
//    }

    public boolean isPrintSql() {
        return printSql;
    }

    public void setPrintSql(boolean printSql) {
        this.printSql = printSql;
    }

    public boolean isPrintBatch() {
        return printBatch;
    }

    public void setPrintBatch(boolean printBatch) {
        this.printBatch = printBatch;
    }

    public boolean isIgnoreUpdateNoWhere() {
        return ignoreUpdateNoWhere;
    }

    public void setIgnoreUpdateNoWhere(boolean ignoreUpdateNoWhere) {
        this.ignoreUpdateNoWhere = ignoreUpdateNoWhere;
    }

    public boolean isIgnoreDeleteNoWhere() {
        return ignoreDeleteNoWhere;
    }

    public void setIgnoreDeleteNoWhere(boolean ignoreDeleteNoWhere) {
        this.ignoreDeleteNoWhere = ignoreDeleteNoWhere;
    }

    public Option bulidOption() {
        Option option = new Option();
        option.setPrintSql(isPrintSql());
        //option.setPrintUseDs(isPrintUseDs());
        option.setPrintBatch(isPrintBatch());
        option.setIgnoreUpdateNoWhere(isIgnoreUpdateNoWhere());
        option.setIgnoreDeleteNoWhere(isIgnoreDeleteNoWhere());
        return option;
    }
}
