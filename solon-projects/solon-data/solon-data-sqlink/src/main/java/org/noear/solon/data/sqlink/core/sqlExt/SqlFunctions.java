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
package org.noear.solon.data.sqlink.core.sqlExt;

import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.sqlExt.SqlExtensionExpression;
import org.noear.solon.data.sqlink.base.sqlExt.SqlTimeUnit;
import org.noear.solon.data.sqlink.core.sqlExt.h2.H2CastExtension;
import org.noear.solon.data.sqlink.core.sqlExt.mysql.MySqlCastExtension;
import org.noear.solon.data.sqlink.core.sqlExt.mysql.MySqlDateTimeDiffExtension;
import org.noear.solon.data.sqlink.core.sqlExt.oracle.OracleAddOrSubDateExtension;
import org.noear.solon.data.sqlink.core.sqlExt.oracle.OracleCastExtension;
import org.noear.solon.data.sqlink.core.sqlExt.oracle.OracleDateTimeDiffExtension;
import org.noear.solon.data.sqlink.core.sqlExt.oracle.OracleJoinExtension;
import org.noear.solon.data.sqlink.core.sqlExt.pgsql.PostgreSQLAddOrSubDateExtension;
import org.noear.solon.data.sqlink.core.sqlExt.pgsql.PostgreSQLCastExtension;
import org.noear.solon.data.sqlink.core.sqlExt.pgsql.PostgreSQLDateTimeDiffExtension;
import org.noear.solon.data.sqlink.core.sqlExt.sqlite.SqliteAddOrSubDateExtension;
import org.noear.solon.data.sqlink.core.sqlExt.sqlite.SqliteCastExtension;
import org.noear.solon.data.sqlink.core.sqlExt.sqlite.SqliteDateTimeDiffExtension;
import org.noear.solon.data.sqlink.core.sqlExt.sqlite.SqliteJoinExtension;
import org.noear.solon.data.sqlink.core.sqlExt.sqlserver.SQLServerCastExtension;
import org.noear.solon.data.sqlink.core.sqlExt.types.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.noear.solon.data.sqlink.core.exception.Winner.boom;

/**
 * Sql函数
 *
 * @author kiryu1223
 * @since 3.0
 */
public class SqlFunctions {

    // region [聚合函数]

//    /**
//     * 聚合函数COUNT(*)
//     */
//    @SqlExtensionExpression(template = "COUNT(*)")
//    public static long count() {
//        boom();
//        return 0;
//    }
//
//    /**
//     * 聚合函数COUNT(t)
//     */
//    @SqlExtensionExpression(template = "COUNT({t})")
//    public static <T> long count(T t) {
//        boom();
//        return 0;
//    }
//
//    /**
//     * 聚合函数SUM(t)
//     */
//    @SqlExtensionExpression(template = "SUM({t})")
//    public static <T> BigDecimal sum(T t) {
//        boom();
//        return BigDecimal.ZERO;
//    }
//
//    /**
//     * 聚合函数AVG(t)
//     */
//    @SqlExtensionExpression(template = "AVG({t})")
//    public static <T extends Number> BigDecimal avg(T t) {
//        boom();
//        return BigDecimal.ZERO;
//    }
//
//    /**
//     * 聚合函数MIN(t)
//     */
//    @SqlExtensionExpression(template = "MIN({t})")
//    public static <T> T min(T t) {
//        boom();
//        return (T) new Object();
//    }
//
//    /**
//     * 聚合函数MAX(t)
//     */
//    @SqlExtensionExpression(template = "MAX({t})")
//    public static <T> T max(T t) {
//        boom();
//        return (T) new Object();
//    }
//
//    @SqlExtensionExpression(dbType = DbType.H2, template = "GROUP_CONCAT({property})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "GROUP_CONCAT({property})")
//    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LISTAGG({property}) WITHIN GROUP (ORDER BY {property})")
//    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "STRING_AGG({property},',')")
//    @SqlExtensionExpression(dbType = DbType.SQLite, template = "GROUP_CONCAT({property})")
//    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "STRING_AGG({property}::TEXT,',')")
//    public static String groupJoin(String property) {
//        boom();
//        return "";
//    }
//
//    @SqlExtensionExpression(dbType = DbType.H2, template = "GROUP_CONCAT({property} SEPARATOR {delimiter})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "GROUP_CONCAT({property} SEPARATOR {delimiter})")
//    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LISTAGG({property},{delimiter}) WITHIN GROUP (ORDER BY {property})")
//    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "STRING_AGG({property},{delimiter})")
//    @SqlExtensionExpression(dbType = DbType.SQLite, template = "GROUP_CONCAT({property},{delimiter})")
//    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "STRING_AGG({property}::TEXT,{delimiter})")
//    public static <T> String groupJoin(String delimiter, T property) {
//        boom();
//        return "";
//    }

    // endregion

    // region [时间]

    /**
     * 获取当前日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "NOW()")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "NOW()")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "CAST(CURRENT_TIMESTAMP AS TIMESTAMP)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "GETDATE()")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "DATETIME('now','localtime')")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "NOW()")
    public static LocalDateTime now() {
        boom();
        return LocalDateTime.now();
    }

    /**
     * 获取当前utc日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "UTC_TIMESTAMP()")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "UTC_TIMESTAMP()")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "(CURRENT_TIMESTAMP AT TIME ZONE 'UTC')")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "GETUTCDATE()")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "DATETIME('now')")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(CURRENT_TIMESTAMP AT TIME ZONE 'UTC')")
    public static LocalDateTime utcNow() {
        boom();
        return LocalDateTime.now();
    }

    /**
     * 获取当前日期
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "CURDATE()")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "CURDATE()")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "CURRENT_DATE")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CAST(GETDATE() AS DATE)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "DATE('now','localtime')")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "CURRENT_DATE")
    public static LocalDate nowDate() {
        boom();
        return LocalDate.now();
    }

    /**
     * 获取当前时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "CURTIME()")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "CURTIME()")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "CURRENT_DATE")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CAST(GETDATE() AS TIME)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "TIME('now','localtime')")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "CURRENT_TIME")
    public static LocalTime nowTime() {
        boom();
        return LocalTime.now();
    }

    /**
     * 获取当前utc日期
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "UTC_DATE()")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "UTC_DATE()")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "CAST(SYS_EXTRACT_UTC(CURRENT_TIMESTAMP) AS DATE)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CAST(GETUTCDATE() AS DATE)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "DATE('now')")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(CURRENT_TIMESTAMP AT TIME ZONE 'UTC')::DATE")
    public static LocalDate utcNowDate() {
        boom();
        return LocalDate.now();
    }

    /**
     * 获取当前utc时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "UTC_TIME()")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "UTC_TIME()")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "CAST(SYS_EXTRACT_UTC(CURRENT_TIMESTAMP) AS DATE)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CAST(GETUTCDATE() AS TIME)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "TIME('now')")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(CURRENT_TIMESTAMP AT TIME ZONE 'UTC')::TIME")
    public static LocalTime utcNowTime() {
        boom();
        return LocalTime.now();
    }

    /**
     * 指定日期或日期时间加上指定单位的时间
     *
     * @param time 指定的日期或日期时间
     * @param unit 时间单位
     * @param num  单位数量
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "ADDDATE({time},INTERVAL {num} {unit})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "ADDDATE({time},INTERVAL {num} {unit})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "", extension = OracleAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEADD({unit},{num},{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLAddOrSubDateExtension.class)
    public static LocalDateTime addDate(LocalDateTime time, SqlTimeUnit unit, int num) {
        boom();
        return LocalDateTime.now();
    }

    /**
     * 指定日期或日期时间加上指定单位的时间
     *
     * @param time 指定的日期或日期时间
     * @param unit 时间单位
     * @param num  单位数量
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "ADDDATE({time},INTERVAL {num} {unit})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "ADDDATE({time},INTERVAL {num} {unit})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "", extension = OracleAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEADD({unit},{num},{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLAddOrSubDateExtension.class)
    public static LocalDate addDate(LocalDate time, SqlTimeUnit unit, int num) {
        boom();
        return LocalDate.now();
    }

    /**
     * 指定日期或日期时间加上指定的天数
     *
     * @param time 指定的日期或日期时间
     * @param days 天数
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "ADDDATE({time},{days})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "ADDDATE({time},{days})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "", extension = OracleAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEADD(DAY,{days},{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLAddOrSubDateExtension.class)
    public static LocalDateTime addDate(LocalDateTime time, int days) {
        boom();
        return LocalDateTime.now();
    }

    /**
     * 指定日期或日期时间加上指定的天数
     *
     * @param time 指定的日期或日期时间
     * @param days 天数
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "ADDDATE({time},{days})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "ADDDATE({time},{days})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "", extension = OracleAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEADD(DAY,{days},{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLAddOrSubDateExtension.class)
    public static LocalDate addDate(LocalDate time, int days) {
        boom();
        return LocalDate.now();
    }

    /**
     * 指定日期或日期时间减去指定单位的时间
     *
     * @param time 指定的日期或日期时间
     * @param unit 时间单位
     * @param num  单位数量
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "SUBDATE({time},INTERVAL {num} {unit})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "SUBDATE({time},INTERVAL {num} {unit})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "", extension = OracleAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEADD({unit},-({num}),{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLAddOrSubDateExtension.class)
    public static LocalDateTime subDate(LocalDateTime time, SqlTimeUnit unit, int num) {
        boom();
        return LocalDateTime.now();
    }

    /**
     * 指定日期或日期时间减去指定单位的时间
     *
     * @param time 指定的日期或日期时间
     * @param unit 时间单位
     * @param num  单位数量
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "SUBDATE({time},INTERVAL {num} {unit})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "SUBDATE({time},INTERVAL {num} {unit})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "", extension = OracleAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEADD({unit},-({num}),{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLAddOrSubDateExtension.class)
    public static LocalDate subDate(LocalDate time, SqlTimeUnit unit, int num) {
        boom();
        return LocalDate.now();
    }

    /**
     * 指定日期或日期时间减去指定的天数
     *
     * @param time 指定的日期或日期时间
     * @param days 天数
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "SUBDATE({time},{days})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "SUBDATE({time},{days})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "", extension = OracleAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEADD(DAY,-({days}),{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLAddOrSubDateExtension.class)
    public static LocalDateTime subDate(LocalDateTime time, int days) {
        boom();
        return LocalDateTime.now();
    }

    /**
     * 指定日期或日期时间减去指定的天数
     *
     * @param time 指定的日期或日期时间
     * @param days 天数
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "SUBDATE({time},{days})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "SUBDATE({time},{days})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "", extension = OracleAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEADD(DAY,-({days}),{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteAddOrSubDateExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLAddOrSubDateExtension.class)
    public static LocalDate subDate(LocalDate time, int days) {
        boom();
        return LocalDate.now();
    }

    /**
     * 计算两个日期或日期时间的指定的时间单位的差距
     *
     * @param unit 时间单位
     * @param from 过去时间
     * @param to   将来时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TIMESTAMPDIFF({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "", extension = MySqlDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "", extension = OracleDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEDIFF_BIG({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLDateTimeDiffExtension.class)
    public static long dateTimeDiff(SqlTimeUnit unit, LocalDateTime from, LocalDateTime to) {
        boom();
        return 0;
    }

    /**
     * 计算两个日期或日期时间的指定的时间单位的差距
     *
     * @param unit 时间单位
     * @param from 过去时间
     * @param to   将来时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TIMESTAMPDIFF({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "", extension = MySqlDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.Oracle, extension = OracleDateTimeDiffExtension.class, template = "")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEDIFF_BIG({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLDateTimeDiffExtension.class)
    public static long dateTimeDiff(SqlTimeUnit unit, LocalDateTime from, LocalDate to) {
        boom();
        return 0;
    }

    /**
     * 计算两个日期或日期时间的指定的时间单位的差距
     *
     * @param unit 时间单位
     * @param from 过去时间
     * @param to   将来时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TIMESTAMPDIFF({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "", extension = MySqlDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.Oracle, extension = OracleDateTimeDiffExtension.class, template = "")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEDIFF_BIG({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLDateTimeDiffExtension.class)
    public static long dateTimeDiff(SqlTimeUnit unit, LocalDateTime from, String to) {
        boom();
        return 0;
    }

    /**
     * 计算两个日期或日期时间的指定的时间单位的差距
     *
     * @param unit 时间单位
     * @param from 过去时间
     * @param to   将来时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TIMESTAMPDIFF({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "", extension = MySqlDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.Oracle, extension = OracleDateTimeDiffExtension.class, template = "")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEDIFF_BIG({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLDateTimeDiffExtension.class)
    public static long dateTimeDiff(SqlTimeUnit unit, LocalDate from, LocalDate to) {
        boom();
        return 0;
    }

    /**
     * 计算两个日期或日期时间的指定的时间单位的差距
     *
     * @param unit 时间单位
     * @param from 过去时间
     * @param to   将来时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TIMESTAMPDIFF({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "", extension = MySqlDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.Oracle, extension = OracleDateTimeDiffExtension.class, template = "")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEDIFF({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLDateTimeDiffExtension.class)
    public static long dateTimeDiff(SqlTimeUnit unit, LocalDate from, LocalDateTime to) {
        boom();
        return 0;
    }

    /**
     * 计算两个日期或日期时间的指定的时间单位的差距
     *
     * @param unit 时间单位
     * @param from 过去时间
     * @param to   将来时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TIMESTAMPDIFF({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "", extension = MySqlDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.Oracle, extension = OracleDateTimeDiffExtension.class, template = "")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEDIFF_BIG({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLDateTimeDiffExtension.class)
    public static long dateTimeDiff(SqlTimeUnit unit, LocalDate from, String to) {
        boom();
        return 0;
    }

    /**
     * 计算两个日期或日期时间的指定的时间单位的差距
     *
     * @param unit 时间单位
     * @param from 过去时间
     * @param to   将来时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TIMESTAMPDIFF({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "", extension = MySqlDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.Oracle, extension = OracleDateTimeDiffExtension.class, template = "")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEDIFF_BIG({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLDateTimeDiffExtension.class)
    public static long dateTimeDiff(SqlTimeUnit unit, String from, String to) {
        boom();
        return 0;
    }

    /**
     * 计算两个日期或日期时间的指定的时间单位的差距
     *
     * @param unit 时间单位
     * @param from 过去时间
     * @param to   将来时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TIMESTAMPDIFF({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "", extension = MySqlDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.Oracle, extension = OracleDateTimeDiffExtension.class, template = "")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEDIFF_BIG({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLDateTimeDiffExtension.class)
    public static long dateTimeDiff(SqlTimeUnit unit, String from, LocalDate to) {
        boom();
        return 0;
    }

    /**
     * 计算两个日期或日期时间的指定的时间单位的差距
     *
     * @param unit 时间单位
     * @param from 过去时间
     * @param to   将来时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TIMESTAMPDIFF({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "", extension = MySqlDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.Oracle, extension = OracleDateTimeDiffExtension.class, template = "")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEDIFF_BIG({unit},{from},{to})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteDateTimeDiffExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLDateTimeDiffExtension.class)
    public static long dateTimeDiff(SqlTimeUnit unit, String from, LocalDateTime to) {
        boom();
        return 0;
    }

    /**
     * 格式化时间到字符串
     *
     * @param time   日期或日期时间
     * @param format 格式
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DATE_FORMAT({time},{format})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DATE_FORMAT({time},{format})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_CHAR({time},{format})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "FORMAT({time},{format})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "STRFTIME({format},{time})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "TO_CHAR({time},{format})")
    public static String dateFormat(LocalDateTime time, String format) {
        boom();
        return "";
    }

    /**
     * 格式化时间到字符串
     *
     * @param time   日期或日期时间
     * @param format 格式
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DATE_FORMAT({time},{format})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DATE_FORMAT({time},{format})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_CHAR({time},{format})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "FORMAT({time},{format})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "STRFTIME({format},{time})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "TO_CHAR({time},{format})")
    public static String dateFormat(LocalDate time, String format) {
        boom();
        return "";
    }

    /**
     * 格式化时间到字符串
     *
     * @param time   日期或日期时间
     * @param format 格式
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DATE_FORMAT({time},{format})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DATE_FORMAT({time},{format})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_CHAR(TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'),{format})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "FORMAT({time},{format})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "STRFTIME({format},{time})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "TO_CHAR({time}::TIMESTAMP,{format})")
    public static String dateFormat(String time, String format) {
        boom();
        return "";
    }

    /**
     * 提取日期或日期时间中的日期
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DAY({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DAY({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(DAY FROM {time})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(DAY,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%d',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(DAY FROM {time})::INT4")
    public static int getDay(LocalDateTime time) {
        boom();
        return 0;
    }

    /**
     * 提取日期或日期时间中的日期
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DAY({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DAY({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(DAY FROM {time})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(DAY,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%d',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(DAY FROM {time})::INT4")
    public static int getDay(LocalDate time) {
        boom();
        return 0;
    }

    /**
     * 提取日期或日期时间中的日期
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DAY({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DAY({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(DAY FROM TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(DAY,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%d',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(DAY FROM {time}::TIMESTAMP)::INT4")
    public static int getDay(String time) {
        boom();
        return 0;
    }

    /**
     * 获取指定日期或日期时间为星期几
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DAYNAME({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DAYNAME({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_CHAR({time},'DAY')")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATENAME(WEEKDAY,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(CASE STRFTIME('%w',{time}) WHEN '0' THEN 'Sunday' WHEN '1' THEN 'Monday' WHEN '2' THEN 'Tuesday' WHEN '3' THEN 'Wednesday' WHEN '4' THEN 'Thursday' WHEN '5' THEN 'Friday' WHEN '6' THEN 'Saturday' END)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "TRIM(TO_CHAR({time},'Day'))")
    public static String getDayName(LocalDateTime time) {
        boom();
        return "";
    }

    /**
     * 获取指定日期或日期时间为星期几
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DAYNAME({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DAYNAME({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_CHAR({time},'DAY')")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATENAME(WEEKDAY,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(CASE STRFTIME('%w',{time}) WHEN '0' THEN 'Sunday' WHEN '1' THEN 'Monday' WHEN '2' THEN 'Tuesday' WHEN '3' THEN 'Wednesday' WHEN '4' THEN 'Thursday' WHEN '5' THEN 'Friday' WHEN '6' THEN 'Saturday' END)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "TRIM(TO_CHAR({time},'Day'))")
    public static String getDayName(LocalDate time) {
        boom();
        return "";
    }

    /**
     * 获取指定日期或日期时间为星期几
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DAYNAME({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DAYNAME({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_CHAR(TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'),'DAY')")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATENAME(WEEKDAY,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(CASE STRFTIME('%w',{time}) WHEN '0' THEN 'Sunday' WHEN '1' THEN 'Monday' WHEN '2' THEN 'Tuesday' WHEN '3' THEN 'Wednesday' WHEN '4' THEN 'Thursday' WHEN '5' THEN 'Friday' WHEN '6' THEN 'Saturday' END)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "TRIM(TO_CHAR({time}::TIMESTAMP,'Day'))")
    public static String getDayName(String time) {
        boom();
        return "";
    }

    /**
     * 从指定的日期或日期时间中取出星期几
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DAYOFWEEK({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DAYOFWEEK({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_NUMBER(TO_CHAR({time},'D'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "(DATEPART(WEEKDAY,{time}))")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(STRFTIME('%w',{time}) + 1)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(EXTRACT(DOW FROM {time}) + 1)::INT4")
    public static int getDayOfWeek(LocalDateTime time) {
        boom();
        return 0;
    }

    /**
     * 从指定的日期或日期时间中取出星期几
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DAYOFWEEK({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DAYOFWEEK({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_NUMBER(TO_CHAR({time},'D'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "(DATEPART(WEEKDAY,{time}))")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(STRFTIME('%w',{time}) + 1)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(EXTRACT(DOW FROM {time}) + 1)::INT4")
    public static int getDayOfWeek(LocalDate time) {
        boom();
        return 0;
    }

    /**
     * 从指定的日期或日期时间中取出星期几
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DAYOFWEEK({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DAYOFWEEK({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_NUMBER(TO_CHAR(TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'),'D'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(WEEKDAY,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(STRFTIME('%w',{time}) + 1)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(EXTRACT(DOW FROM {time}::TIMESTAMP) + 1)::INT4")
    public static int getDayOfWeek(String time) {
        boom();
        return 0;
    }

    /**
     * 获取指定日期或日期时间为今年的多少天
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DAYOFYEAR({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DAYOFYEAR({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_NUMBER(TO_CHAR({time},'DDD'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(DAYOFYEAR,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%j',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(DOY FROM {time})::INT4")
    public static int getDayOfYear(LocalDateTime time) {
        boom();
        return 0;
    }

    /**
     * 获取指定日期或日期时间为今年的多少天
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DAYOFYEAR({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DAYOFYEAR({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_NUMBER(TO_CHAR({time},'DDD'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(DAYOFYEAR,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%j',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(DOY FROM {time})::INT4")
    public static int getDayOfYear(LocalDate time) {
        boom();
        return 0;
    }

    /**
     * 获取指定日期或日期时间为今年的多少天
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DAYOFYEAR({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DAYOFYEAR({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_NUMBER(TO_CHAR(TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'),'DDD'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(DAYOFYEAR,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%j',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(DOY FROM {time}::TIMESTAMP)::INT4")
    public static int getDayOfYear(String time) {
        boom();
        return 0;
    }

//    @SqlExtensionExpression(dbType = DbType.H2, template = "SEC_TO_TIME({second})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "SEC_TO_TIME({second})")
//    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_DATE(TO_CHAR(FLOOR({second} / 3600), 'FM00')||':'||TO_CHAR(FLOOR(MOD({second}, 3600) / 60), 'FM00')||':'||TO_CHAR(MOD({second}, 60), 'FM00'))")
//    public static LocalTime toTime(int second)
//    {
//        boom();
//        return LocalTime.now();
//    }
//
//    @SqlExtensionExpression(dbType = DbType.H2, template = "STR_TO_DATE({time})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "STR_TO_DATE({time})")
//    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_DATE({time}, 'YYYY-MM-DD')")
//    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CONVERT(DATE,{time},23)")
//    public static LocalDate toDate(String time)
//    {
//        boom();
//        return LocalDate.now();
//    }
//
//    @SqlExtensionExpression(dbType = DbType.H2, template = "STR_TO_DATE({time},{format})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "STR_TO_DATE({time},{format})")
//    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_DATE({time},{format})")
//    public static LocalDate toDate(String time, String format)
//    {
//        boom();
//        return LocalDate.now();
//    }
//
//    @SqlExtensionExpression(dbType = DbType.H2, template = "FROM_DAYS({days})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "FROM_DAYS({days})")
//    @SqlExtensionExpression(dbType = DbType.Oracle, template = "(TO_DATE('1970-01-01', 'YYYY-MM-DD') + ({days} - 719163))")
//    public static LocalDate toDate(long days)
//    {
//        boom();
//        return LocalDate.now();
//    }
//
//    @SqlExtensionExpression(dbType = DbType.H2, template = "STR_TO_DATE({time},{format})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "STR_TO_DATE({time},{format})")
//    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_DATE({time},{format})")
//    public static LocalDateTime toDateTime(String time, String format)
//    {
//        boom();
//        return LocalDateTime.now();
//    }

    /**
     * 获取指定日期或日期时间从公元到今天的天数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TO_DAYS({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "TO_DAYS({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TRUNC({time} - (TO_DATE('0001-01-01', 'YYYY-MM-DD') - INTERVAL '1' YEAR) - 2)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "(DATEDIFF(DAY,'0001-01-01',{time}) + 366)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(JULIANDAY({time}) - JULIANDAY('0000-01-01'))")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(EXTRACT(EPOCH FROM {time})::INT4 / 86400 + 719528)")
    public static int dateToDays(LocalDate time) {
        boom();
        return 0;
    }

    /**
     * 获取指定日期或日期时间从公元到今天的天数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TO_DAYS({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "TO_DAYS({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TRUNC({time} - (TO_DATE('0001-01-01', 'YYYY-MM-DD') - INTERVAL '1' YEAR) - 2)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "(DATEDIFF(DAY,'0001-01-01',{time}) + 366)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(JULIANDAY({time}) - JULIANDAY('0000-01-01'))")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(EXTRACT(EPOCH FROM {time})::INT4 / 86400 + 719528)")
    public static int dateToDays(LocalDateTime time) {
        boom();
        return 0;
    }

    /**
     * 获取指定日期或日期时间从公元到今天的天数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TO_DAYS({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "TO_DAYS({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(DAY FROM (TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff') - (TO_TIMESTAMP('0001-01-01', 'YYYY-MM-DD') - INTERVAL '1' YEAR) - INTERVAL '2' DAY))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "(DATEDIFF(DAY,'0001-01-01',{time}) + 366)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(JULIANDAY({time}) - JULIANDAY('0000-01-01'))")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(EXTRACT(EPOCH FROM {time}::TIMESTAMP)::INT4 / 86400 + 719528)")
    public static int dateToDays(String time) {
        boom();
        return 0;
    }

//    @SqlExtensionExpression(dbType = DbType.H2, template = "TIME_TO_SEC({time})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "TIME_TO_SEC({time})")
//    @SqlExtensionExpression(dbType = DbType.Oracle, template = "((EXTRACT(HOUR FROM {time}) * 3600) + (EXTRACT(MINUTE FROM {time}) * 60) + EXTRACT(SECOND FROM {time}))")
//    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(HOUR,{time}) * 3600 + DATEPART(MINUTE,{time}) * 60 + DATEPART(SECOND,{time})")
//    public static int toSecond(LocalDateTime time)
//    {
//        boom();
//        return 0;
//    }
//
//    @SqlExtensionExpression(dbType = DbType.H2, template = "TIME_TO_SEC({time})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "TIME_TO_SEC({time})")
//    @SqlExtensionExpression(dbType = DbType.Oracle, template = "((EXTRACT(HOUR FROM {time}) * 3600) + (EXTRACT(MINUTE FROM {time}) * 60) + EXTRACT(SECOND FROM {time}))")
//    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(HOUR,{time}) * 3600 + DATEPART(MINUTE,{time}) * 60 + DATEPART(SECOND,{time})")
//    public static int toSecond(LocalTime time)
//    {
//        boom();
//        return 0;
//    }
//
//    @SqlExtensionExpression(dbType = DbType.H2, template = "TIME_TO_SEC({time})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "TIME_TO_SEC({time})")
//    @SqlExtensionExpression(dbType = DbType.Oracle, template = "((EXTRACT(HOUR FROM TO_DATE({time})) * 3600) + (EXTRACT(MINUTE FROM TO_DATE({time})) * 60) + EXTRACT(SECOND FROM TO_DATE({time})))")
//    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(HOUR,{time}) * 3600 + DATEPART(MINUTE,{time}) * 60 + DATEPART(SECOND,{time})")
//    public static int toSecond(String time)
//    {
//        boom();
//        return 0;
//    }

    /**
     * 提取日期时间中的小时
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "HOUR({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "HOUR({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(HOUR FROM {time})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(HOUR,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%H',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(HOUR FROM {time})::INT4")
    public static int getHour(LocalDateTime time) {
        boom();
        return 0;
    }

    /**
     * 提取日期时间中的小时
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "HOUR({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "HOUR({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(HOUR FROM {time})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(HOUR,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%H',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(HOUR FROM {time})::INT4")
    public static int getHour(LocalDate time) {
        boom();
        return 0;
    }

    /**
     * 提取日期时间中的小时
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "HOUR({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "HOUR({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(HOUR FROM TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(HOUR,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%H',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(HOUR FROM {time}::TIMESTAMP)::INT4")
    public static int getHour(String time) {
        boom();
        return 0;
    }

    /**
     * 获取指定的日期或日期时间当月的最后一天的日期
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "LAST_DAY({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LAST_DAY({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LAST_DAY({time})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "EOMONTH({time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "DATE({time},'start of month','+1 month','-1 day')")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(DATE_TRUNC('MONTH',{time}) + INTERVAL '1' MONTH - INTERVAL '1' DAY)::DATE")
    public static LocalDate getLastDay(LocalDateTime time) {
        boom();
        return LocalDate.now();
    }

    /**
     * 获取指定的日期或日期时间当月的最后一天的日期
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "LAST_DAY({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LAST_DAY({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LAST_DAY({time})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "EOMONTH({time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "DATE({time},'start of month','+1 month','-1 day')")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(DATE_TRUNC('MONTH',{time}) + INTERVAL '1' MONTH - INTERVAL '1' DAY)::DATE")
    public static LocalDate getLastDay(LocalDate time) {
        boom();
        return LocalDate.now();
    }

    /**
     * 获取指定的日期或日期时间当月的最后一天的日期
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "LAST_DAY({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LAST_DAY({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LAST_DAY(TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "EOMONTH({time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "DATE({time},'start of month','+1 month','-1 day')")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(DATE_TRUNC('MONTH',{time}::TIMESTAMP) + INTERVAL '1' MONTH - INTERVAL '1' DAY)::DATE")
    public static LocalDate getLastDay(String time) {
        boom();
        return LocalDate.now();
    }

//    @SqlExtensionExpression(dbType = DbType.H2, template = "MAKEDATE({},{})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "MAKEDATE({},{})")
//    public static LocalDate createDate(int year, int days)
//    {
//        boom();
//        return 0;
//    }
//
//    @SqlExtensionExpression(dbType = DbType.H2, template = "MAKETIME({},{},{})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "MAKETIME({},{},{})")
//    public static LocalTime createTime(int hour, int minute, int second)
//    {
//        boom();
//        return 0;
//    }

    /**
     * 提取日期时间中的分钟
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "MINUTE({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "MINUTE({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(MINUTE FROM {time})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(MINUTE,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%M',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(MINUTE FROM {time})::INT4")
    public static int getMinute(LocalTime time) {
        boom();
        return 0;
    }

    /**
     * 提取日期时间中的分钟
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "MINUTE({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "MINUTE({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(MINUTE FROM {time})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(MINUTE,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%M',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(MINUTE FROM {time})::INT4")
    public static int getMinute(LocalDateTime time) {
        boom();
        return 0;
    }

    /**
     * 提取日期时间中的分钟
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "MINUTE({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "MINUTE({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(MINUTE FROM TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(MINUTE,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%M',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(MINUTE FROM {time}::TIMESTAMP)::INT4")
    public static int getMinute(String time) {
        boom();
        return 0;
    }

    /**
     * 提取日期或日期时间中的月份
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "MONTH({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "MONTH({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(MONTH FROM {time})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(MINUTE,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%m',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(MONTH FROM {time})::INT4")
    public static int getMonth(LocalDate time) {
        boom();
        return 0;
    }

    /**
     * 提取日期或日期时间中的月份
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "MONTH({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "MONTH({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(MONTH FROM {time})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(MONTH,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%m',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(MONTH FROM {time})::INT4")
    public static int getMonth(LocalDateTime time) {
        boom();
        return 0;
    }

    /**
     * 提取日期或日期时间中的月份
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "MONTH({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "MONTH({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(MONTH FROM TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(MONTH,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%m',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(MONTH FROM {time}::TIMESTAMP)::INT4")
    public static int getMonth(String time) {
        boom();
        return 0;
    }

    /**
     * 获取指定日期或日期时间那个月的名字
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "MONTHNAME({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "MONTHNAME({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_CHAR({time},'FMMONTH')")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "FORMAT({time}, 'MMMM')")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(CASE STRFTIME('%m',{time}) WHEN '01' THEN 'January' WHEN '02' THEN 'February' WHEN '03' THEN 'March' WHEN '04' THEN 'April' WHEN '05' THEN 'May' WHEN '06' THEN 'June' WHEN '07' THEN 'July' WHEN '08' THEN 'August' WHEN '09' THEN 'September' WHEN '10' THEN 'October' WHEN '11' THEN 'November' WHEN '12' THEN 'December' END)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "TRIM(TO_CHAR({time},'Month'))")
    public static String getMonthName(LocalDate time) {
        boom();
        return "";
    }

    /**
     * 获取指定日期或日期时间那个月的名字
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "MONTHNAME({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "MONTHNAME({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_CHAR({time},'FMMONTH')")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "FORMAT({time}, 'MMMM')")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(CASE STRFTIME('%m',{time}) WHEN '01' THEN 'January' WHEN '02' THEN 'February' WHEN '03' THEN 'March' WHEN '04' THEN 'April' WHEN '05' THEN 'May' WHEN '06' THEN 'June' WHEN '07' THEN 'July' WHEN '08' THEN 'August' WHEN '09' THEN 'September' WHEN '10' THEN 'October' WHEN '11' THEN 'November' WHEN '12' THEN 'December' END)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "TRIM(TO_CHAR({time},'Month'))")
    public static String getMonthName(LocalDateTime time) {
        boom();
        return "";
    }

    /**
     * 获取指定日期或日期时间那个月的名字
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "MONTHNAME({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "MONTHNAME({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_CHAR(TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'),'FMMONTH')")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "FORMAT(CONVERT(DATE,{time}), 'MMMM')")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(CASE STRFTIME('%m',{time}) WHEN '01' THEN 'January' WHEN '02' THEN 'February' WHEN '03' THEN 'March' WHEN '04' THEN 'April' WHEN '05' THEN 'May' WHEN '06' THEN 'June' WHEN '07' THEN 'July' WHEN '08' THEN 'August' WHEN '09' THEN 'September' WHEN '10' THEN 'October' WHEN '11' THEN 'November' WHEN '12' THEN 'December' END)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "TRIM(TO_CHAR({time}::TIMESTAMP,'Month'))")
    public static String getMonthName(String time) {
        boom();
        return "";
    }

    /**
     * 提取日期或日期时间所在季度
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "QUARTER({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "QUARTER({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "CEIL(EXTRACT(MONTH FROM {time}) / 3)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(QUARTER,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "((strftime('%m',{time}) + 2) / 3)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(QUARTER FROM {time})::INT4")
    public static int getQuarter(LocalDate time) {
        boom();
        return 0;
    }

    /**
     * 提取日期或日期时间所在季度
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "QUARTER({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "QUARTER({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "CEIL(EXTRACT(MONTH FROM {time}) / 3)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(QUARTER,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "((strftime('%m',{time}) + 2) / 3)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(QUARTER FROM {time})::INT4")
    public static int getQuarter(LocalDateTime time) {
        boom();
        return 0;
    }

    /**
     * 提取日期或日期时间所在季度
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "QUARTER({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "QUARTER({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "CEIL(EXTRACT(MONTH FROM TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff')) / 3)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(QUARTER,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "((strftime('%m',{time}) + 2) / 3)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(QUARTER FROM {time}::TIMESTAMP)::INT4")
    public static int getQuarter(String time) {
        boom();
        return 0;
    }

    /**
     * 提取日期时间中的秒数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "SECOND({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "SECOND({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(SECOND FROM {time})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(SECOND,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%S',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(SECOND FROM {time})::INT4")
    public static int getSecond(LocalTime time) {
        boom();
        return 0;
    }

    /**
     * 提取日期时间中的秒数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "SECOND({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "SECOND({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(SECOND FROM {time})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(SECOND,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%S',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(SECOND FROM {time})::INT4")
    public static int getSecond(LocalDateTime time) {
        boom();
        return 0;
    }

    /**
     * 提取日期时间中的秒数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "SECOND({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "SECOND({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(SECOND FROM TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(SECOND,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%S',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(SECOND FROM {time}::TIMESTAMP)::INT4")
    public static int getSecond(String time) {
        boom();
        return 0;
    }

    /**
     * 提取日期时间中的毫秒
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "(MICROSECOND({time}) / 1000)")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "(MICROSECOND({time}) / 1000)")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "(EXTRACT(SECOND FROM {time}) * 1000)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(MS,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(STRFTIME('%f',{time}) * 1000)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(MILLISECOND from {time})::INT4")
    public static int getMilliSecond(LocalTime time) {
        boom();
        return 0;
    }

    /**
     * 提取日期时间中的毫秒
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "(MICROSECOND({time}) / 1000)")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "(MICROSECOND({time}) / 1000)")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "(EXTRACT(SECOND FROM {time}) * 1000)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(MS,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(STRFTIME('%f',{time}) * 1000)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(MILLISECOND from {time})::INT4")
    public static int getMilliSecond(LocalDateTime time) {
        boom();
        return 0;
    }

    /**
     * 提取日期时间中的毫秒
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "(MICROSECOND({time}) / 1000)")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "(MICROSECOND({time}) / 1000)")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "(EXTRACT(SECOND FROM {time}) * 1000)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(MS,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(STRFTIME('%f',{time}) * 1000)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(MILLISECOND from {time}::TIMESTAMP)::INT4")
    public static int getMilliSecond(String time) {
        boom();
        return 0;
    }

    /**
     * 提取日期或日期时间中的周数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "WEEK({time},1)")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "WEEK({time},1)")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_NUMBER(TO_CHAR({time},'IW'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(WEEK,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(STRFTIME('%W',{time}) + 1)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(WEEK from {time})::INT4")
    public static int getWeek(LocalDate time) {
        boom();
        return 0;
    }

    /**
     * 提取日期或日期时间中的周数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "WEEK({time},1)")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "WEEK({time},1)")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_NUMBER(TO_CHAR({time},'IW'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(WEEK,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(STRFTIME('%W',{time}) + 1)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(WEEK from {time})::INT4")
    public static int getWeek(LocalDateTime time) {
        boom();
        return 0;
    }

    /**
     * 提取日期或日期时间中的周数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "WEEK({time},1)")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "WEEK({time},1)")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_NUMBER(TO_CHAR(TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'),'IW'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(WEEK,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(STRFTIME('%W',{time}) + 1)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(WEEK from {time}::TIMESTAMP)::INT4")
    public static int getWeek(String time) {
        boom();
        return 0;
    }

//    @SqlExtensionExpression(dbType = DbType.H2, template = "WEEK({time},{firstDayofweek})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "WEEK({time},{firstDayofweek})")
//    public static int getWeek(String time, int firstDayofweek)
//    {
//        boom();
//        return 0;
//    }

    /**
     * 返回给定日期或日期时间的工作日编号
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "WEEKDAY({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "WEEKDAY({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "(CASE TO_NUMBER(TO_CHAR({time},'D')) WHEN 1 THEN 6 ELSE TO_NUMBER(TO_CHAR({time},'D')) - 2 END)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "(CASE DATEPART(WEEKDAY,{time}) WHEN 1 THEN 6 ELSE DATEPART(WEEKDAY,{time}) - 2 END)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(CASE STRFTIME('%w',{time}) WHEN '0' THEN 6 ELSE STRFTIME('%w',{time}) - 1 END)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "((EXTRACT(DOW FROM {time}) + 6) % 7)::INT4")
    public static int getWeekDay(LocalDate time) {
        boom();
        return 0;
    }

    /**
     * 返回给定日期或日期时间的工作日编号
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "WEEKDAY({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "WEEKDAY({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "(CASE TO_NUMBER(TO_CHAR({time},'D')) WHEN 1 THEN 6 ELSE TO_NUMBER(TO_CHAR({time},'D')) - 2 END)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "(CASE DATEPART(WEEKDAY,{time}) WHEN 1 THEN 6 ELSE DATEPART(WEEKDAY,{time}) - 2 END)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(CASE STRFTIME('%w',{time}) WHEN '0' THEN 6 ELSE STRFTIME('%w',{time}) - 1 END)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "((EXTRACT(DOW FROM {time}) + 6) % 7)::INT4")
    public static int getWeekDay(LocalDateTime time) {
        boom();
        return 0;
    }

    /**
     * 返回给定日期或日期时间的工作日编号
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "WEEKDAY({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "WEEKDAY({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "(CASE TO_NUMBER(TO_CHAR(TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'),'D')) WHEN 1 THEN 6 ELSE TO_NUMBER(TO_CHAR(TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'),'D')) - 2 END)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "(CASE DATEPART(WEEKDAY,{time}) WHEN 1 THEN 6 ELSE DATEPART(WEEKDAY,{time}) - 2 END)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(CASE STRFTIME('%w',{time}) WHEN '0' THEN 6 ELSE STRFTIME('%w',{time}) - 1 END)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "((EXTRACT(DOW FROM {time}::TIMESTAMP) + 6) % 7)::INT4")
    public static int getWeekDay(String time) {
        boom();
        return 0;
    }

    /**
     * 获取日期或日期时间中的周数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "WEEKOFYEAR({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "WEEKOFYEAR({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_NUMBER(TO_CHAR({time},'IW'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(ISO_WEEK,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(STRFTIME('%W',{time}) + 1)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(WEEK FROM {time})::INT4")
    public static int getWeekOfYear(LocalDate time) {
        boom();
        return 0;
    }

    /**
     * 获取日期或日期时间中的周数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "WEEKOFYEAR({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "WEEKOFYEAR({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_NUMBER(TO_CHAR({time},'IW'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(ISO_WEEK,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(STRFTIME('%W',{time}) + 1)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(WEEK FROM {time})::INT4")
    public static int getWeekOfYear(LocalDateTime time) {
        boom();
        return 0;
    }

    /**
     * 获取日期或日期时间中的周数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "WEEKOFYEAR({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "WEEKOFYEAR({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_NUMBER(TO_CHAR(TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'),'IW'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(ISO_WEEK,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(STRFTIME('%W',{time}) + 1)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(WEEK FROM {time}::TIMESTAMP)::INT4")
    public static int getWeekOfYear(String time) {
        boom();
        return 0;
    }

    /**
     * 提取日期或日期时间中的年数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "YEAR({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "YEAR({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(YEAR FROM {time})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(YEAR,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%Y',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(YEAR FROM {time})::INT4")
    public static int getYear(LocalDateTime time) {
        boom();
        return 0;
    }

    /**
     * 提取日期或日期时间中的年数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "YEAR({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "YEAR({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(YEAR FROM {time})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(YEAR,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%Y',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(YEAR FROM {time})::INT4")
    public static int getYear(LocalTime time) {
        boom();
        return 0;
    }

    /**
     * 提取日期或日期时间中的年数
     *
     * @param time 日期或日期时间
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "YEAR({time})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "YEAR({time})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXTRACT(YEAR FROM TO_TIMESTAMP({time},'YYYY-MM-DD hh24:mi:ss:ff'))")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATEPART(YEAR,{time})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(STRFTIME('%Y',{time}) AS INTEGER)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXTRACT(YEAR FROM {time}::TIMESTAMP)::INT4")
    public static int getYear(String time) {
        boom();
        return 0;
    }

    // endregion

    // region [数值]

    /**
     * 取绝对值
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "ABS({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "ABS({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "ABS({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "ABS({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "ABS({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "ABS({a})")
    public static <T extends Number> T abs(T a) {
        boom();
        return (T) new Num();
    }

    /**
     * 计算cos
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "COS({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "COS({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "COS({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "COS({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "COS({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "COS({a})")
    public static <T extends Number> double cos(T a) {
        boom();
        return 0;
    }

    /**
     * 计算sin
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "SIN({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "SIN({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "SIN({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "SIN({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "SIN({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "SIN({a})")
    public static <T extends Number> double sin(T a) {
        boom();
        return 0;
    }

    /**
     * 计算tan
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TAN({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "TAN({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TAN({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "TAN({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "TAN({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "TAN({a})")
    public static <T extends Number> double tan(T a) {
        boom();
        return 0;
    }

    /**
     * 计算acos
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "ACOS({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "ACOS({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "ACOS({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "ACOS({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "ACOS({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "ACOS({a})")
    public static <T extends Number> double acos(T a) {
        boom();
        return 0;
    }

    /**
     * 计算asin
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "ASIN({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "ASIN({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "ASIN({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "ASIN({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "ASIN({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "ASIN({a})")
    public static <T extends Number> double asin(T a) {
        boom();
        return 0;
    }

    /**
     * 计算atan
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "ATAN({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "ATAN({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "ATAN({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "ATAN({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "ATAN({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "ATAN({a})")
    public static <T extends Number> double atan(T a) {
        boom();
        return 0;
    }

    /**
     * 计算atan2
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "ATAN2({a},{b})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "ATAN2({a},{b})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "ATAN2({a},{b})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "ATN2({a},{b})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "ATAN2({a},{b})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "ATAN2({a},{b})")
    public static <T extends Number> double atan2(T a, T b) {
        boom();
        return 0;
    }

    /**
     * 向上取整
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "CEIL({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "CEIL({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "CEIL({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CEILING({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CEIL({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "CEIL({a})::INT4")
    public static <T extends Number> int ceil(T a) {
        boom();
        return 0;
    }

    /**
     * 向下取整
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "FLOOR({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "FLOOR({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "FLOOR({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "FLOOR({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "FLOOR({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "FLOOR({a})::INT4")
    public static <T extends Number> int floor(T a) {
        boom();
        return 0;
    }

    /**
     * 余切函数
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "COT({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "COT({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "(CASE SIN({a}) WHEN 0 THEN 0 ELSE COS({a}) / SIN({a}) END)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "COT({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "COT({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "COT({a})")
    public static <T extends Number> double cot(T a) {
        boom();
        return 0;
    }

    /**
     * 将弧度转换为角度
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "DEGREES({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "DEGREES({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "({a} * 180 / " + Math.PI + ")")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DEGREES({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "DEGREES({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "DEGREES({a})")
    public static <T extends Number> double degrees(T a) {
        boom();
        return 0;
    }

    /**
     * 计算给定数值的指数函数值
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "EXP({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "EXP({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "EXP({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "EXP({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "EXP({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "EXP({a})")
    public static <T extends Number> double exp(T a) {
        boom();
        return 0;
    }

    /**
     * 获取最大值
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "GREATEST({a},{b})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "GREATEST({a},{b})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "GREATEST({a},{b})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "GREATEST({a},{b})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "MAX({a},{b})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "GREATEST({a},{b})")
    public static <T extends Number> T big(T a, T b) {
        boom();
        return (T) new Num();
    }

    /**
     * 获取最大值
     */
    @SafeVarargs
    @SqlExtensionExpression(dbType = DbType.H2, template = "GREATEST({a},{b},{cs})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "GREATEST({a},{b},{cs})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "GREATEST({a},{b},{cs})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "GREATEST({a},{b},{cs})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "MAX({a},{b},{cs})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "GREATEST({a},{b},{cs})")
    public static <T extends Number> T big(T a, T b, T... cs) {
        boom();
        return (T) new Num();
    }

    /**
     * 获取最小值
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "LEAST({a},{b})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LEAST({a},{b})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LEAST({a},{b})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "LEAST({a},{b})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "MIN({a},{b})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "LEAST({a},{b})")
    public static <T extends Number> T small(T a, T b) {
        boom();
        return (T) new Num();
    }

    /**
     * 获取最小值
     */
    @SafeVarargs
    @SqlExtensionExpression(dbType = DbType.H2, template = "LEAST({a},{b},{cs})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LEAST({a},{b},{cs})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LEAST({a},{b},{cs})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "LEAST({a},{b},{cs})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "MIN({a},{b},{cs})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "LEAST({a},{b},{cs})")
    public static <T extends Number> T small(T a, T b, T... cs) {
        boom();
        return (T) new Num();
    }


    /**
     * 计算指定基数的对数
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "LN({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LN({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LN({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "LOG({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "LN({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "LN({a})")
    public static <T extends Number> double ln(T a) {
        boom();
        return 0;
    }

    /**
     * 计算指定基数的对数
     *
     * @param a    数值，用于计算其对数
     * @param base 对数的基数
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "LOG({base},{a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LOG({base},{a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LOG({base},{a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "LOG({a},{base})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "LOG({base},{a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "LOG({base},{a})::FLOAT8")
    public static <T extends Number, Base extends Number> double log(T a, Base base) {
        boom();
        return 0;
    }

    /**
     * 计算指定基数为2的对数
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "LOG2({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LOG2({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LOG(2,{a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "LOG({a},2)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "LOG2({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "LOG(2,{a})::FLOAT8")
    public static <T extends Number> double log2(T a) {
        boom();
        return 0;
    }

    /**
     * 计算指定基数为10的对数
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "LOG10({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LOG10({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LOG(10,{a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "LOG({a},10)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "LOG10({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "LOG10({a})::FLOAT8")
    public static <T extends Number> double log10(T a) {
        boom();
        return 0;
    }

    /**
     * 取模运算
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "MOD({a},{b})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "MOD({a},{b})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "MOD({a},{b})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "({a} % {b})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "MOD({a},{b})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "MOD({a},{b})")
    public static <T extends Number> T mod(T a, T b) {
        boom();
        return (T) new Num();
    }

    /**
     * 获取π
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "PI()")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "PI()")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "(" + Math.PI + ")")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "PI()")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "PI()")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "PI()")
    public static double pi() {
        boom();
        return 3.14159;
    }

    /**
     * 幂运算
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "POWER({a},{b})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "POWER({a},{b})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "POWER({a},{b})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "POWER({a},{b})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "POWER({a},{b})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "POWER({a},{b})")
    public static <T extends Number> double pow(T a, T b) {
        boom();
        return 0;
    }

    /**
     * 将角度转换为弧度
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "RADIANS({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "RADIANS({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "({a} * " + Math.PI + " / 180)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "RADIANS({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "RADIANS({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "RADIANS({a})")
    public static <T extends Number> double radians(T a) {
        boom();
        return 0;
    }

    /**
     * 获取从0-1的随机数
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "RAND()")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "RAND()")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "DBMS_RANDOM.VALUE")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "RAND()")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "ABS(RANDOM() / 10000000000000000000.0)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "RANDOM()")
    public static double random() {
        boom();
        return 0;
    }

//    @SqlExtensionExpression(dbType = DbType.H2, template = "RAND({a})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "RAND({a})")
//    @SqlExtensionExpression(dbType = DbType.Oracle, template = "DBMS_RANDOM.VALUE")
//    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "RAND({a})")
//    @SqlExtensionExpression(dbType = DbType.SQLite, template = "ABS(RANDOM() / 10000000000000000000.0)")
//    public static double random(int a)
//    {
//        boom();
//        return 0;
//    }

    /**
     * 四舍五入
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "ROUND({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "ROUND({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "ROUND({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "ROUND({a},0)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "ROUND({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "ROUND({a})::INT4")
    public static <T extends Number> int round(T a) {
        boom();
        return 0;
    }

    /**
     * 指定到多少位的小数位为止，四舍五入
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "ROUND({a},{b})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "ROUND({a},{b})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "ROUND({a},{b})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "ROUND({a},{b})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "ROUND({a},{b})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "ROUND({a}::NUMERIC,{b})::FLOAT8")
    public static <T extends Number> T round(T a, int b) {
        boom();
        return (T) new Num();
    }

    /**
     * 如果数字大于0，sign函数返回1；如果数字小于0，返回-1；如果数字等于0，返回0
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "SIGN({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "SIGN({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "SIGN({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "SIGN({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "SIGN({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "SIGN({a})::INT4")
    public static <T extends Number> int sign(T a) {
        boom();
        return 0;
    }

    /**
     * 计算平方根
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "SQRT({a})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "SQRT({a})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "SQRT({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "SQRT({a})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "SQRT({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "SQRT({a})")
    public static <T extends Number> double sqrt(T a) {
        boom();
        return 0;
    }

    /**
     * 截取到指定小数位的小数
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TRUNCATE({a},{b})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "TRUNCATE({a},{b})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TRUNC({a},{b})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "ROUND({a},{b},1)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST(SUBSTR({a} * 1.0,1,INSTR({a} * 1.0,'.') + {b}) AS REAL)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "TRUNC({a}::NUMERIC,{b})::FLOAT8")
    public static <T extends Number> double truncate(T a, int b) {
        boom();
        return 0;
    }

    /**
     * 截取到整数
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TRUNCATE({a},0)")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "TRUNCATE({a},0)")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TRUNC({a})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "ROUND({a},0,1)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "TRUNC({a})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "TRUNC({a})::INT4")
    public static <T extends Number> int truncate(T a) {
        boom();
        return 0;
    }

    // endregion

    // region [字符串]

    /**
     * 判断字符串是否为空
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "(CHAR_LENGTH({str}) = 0)")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "(CHAR_LENGTH({str}) = 0)")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "(NVL(LENGTH({str}),0) = 0)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "(DATALENGTH({str}) = 0)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(LENGTH({str}) = 0)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(LENGTH({str}) = 0)")
    public static boolean isEmpty(String str) {
        boom();
        return true;
    }

    /**
     * 字符串转ASCII码
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "ASCII({str})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "ASCII({str})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "ASCII({str})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "ASCII({str})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "UNICODE({str})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "ASCII({str})")
    public static int strToAscii(String str) {
        boom();
        return 0;
    }

    /**
     * ASCII码转字符串
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "CHAR({t})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "CHAR({t})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "CHR({t})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CHAR({t})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CHAR({t})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "CHR({t})")
    public static String asciiToStr(int t) {
        boom();
        return "";
    }

    /**
     * 获取字符串长度
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "CHAR_LENGTH({str})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "CHAR_LENGTH({str})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "NVL(LENGTH({str}),0)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "LEN({str})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "LENGTH({str})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "LENGTH({str})")
    public static int length(String str) {
        boom();
        return 0;
    }

    /**
     * 获取字节长度
     * <p>
     * <b><span style='color:red;'>特殊说明</span>：各数据库的字节长度计算方式不同，请根据实际情况选择。</b>
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "LENGTH({str})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LENGTH({str})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LENGTHB({str})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "DATALENGTH({str})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "LENGTH(CAST({str} AS BLOB))")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "OCTET_LENGTH({str})")
    public static int byteLength(String str) {
        boom();
        return 0;
    }

    /**
     * 字符串拼接
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "CONCAT({s1},{s2})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "CONCAT({s1},{s2})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "CONCAT({s1},{s2})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CONCAT({s1},{s2})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "({s1}||{s2})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "CONCAT({s1},{s2})")
    public static String concat(String s1, String s2) {
        boom();
        return "";
    }

    /**
     * 字符串拼接
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "CONCAT({s1},{s2},{ss})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "CONCAT({s1},{s2},{ss})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "({s1}||{s2}||{ss})", separator = "||")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CONCAT({s1},{s2},{ss})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "({s1}||{s2}||{ss})", separator = "||")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "CONCAT({s1},{s2},{ss})")
    public static String concat(String s1, String s2, String... ss) {
        boom();
        return "";
    }

    /**
     * 以指定字符为间隔，拼接字符串
     *
     * @param separator 间隔
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "CONCAT_WS({separator},{s1},{s2})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "CONCAT_WS({separator},{s1},{s2})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "({s1}||{separator}||{s2})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CONCAT_WS({separator},{s1},{s2})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "({s1}||{separator}||{s2})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "CONCAT_WS({separator},{s1},{s2})")
    public static String join(String separator, String s1, String s2) {
        boom();
        return "";
    }

    /**
     * 以指定字符为间隔，拼接字符串
     *
     * @param separator 间隔
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "CONCAT_WS({separator},{s1},{s2},{ss})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "CONCAT_WS({separator},{s1},{s2},{ss})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "", extension = OracleJoinExtension.class)
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CONCAT_WS({separator},{s1},{s2},{ss})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteJoinExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "CONCAT_WS({separator},{s1},{s2},{ss})")
    public static String join(String separator, String s1, String s2, String... ss) {
        boom();
        return "";
    }

//    todo
//    @SqlExtensionExpression(dbType = DbType.H2, template = "FORMAT({t},{format})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "FORMAT({t},{format})")
//    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TO_CHAR({t},{format})")
//    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "FORMAT({t},{format})")
//    @SqlExtensionExpression(dbType = DbType.SQLite, template = "PRINTF({format},{t})")
//    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "FORMAT({format},{t})")
//    public static <T extends Number> String format(T t, String format)
//    {
//        boom();
//        return "";
//    }

    /**
     * 获取子串在字符串中的位置
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "INSTR({str},{subStr})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "INSTR({str},{subStr})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "INSTR({str},{subStr})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CHARINDEX({subStr},{str})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "INSTR({str},{subStr})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "STRPOS({str},{subStr})")
    public static int indexOf(String str, String subStr) {
        boom();
        return 0;
    }

    /**
     * 获取子串在字符串中的位置，并且设置起始偏移
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "LOCATE({subStr},{str},{offset})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LOCATE({subStr},{str},{offset})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "INSTR({str},{subStr},{offset})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CHARINDEX({subStr},{str},{offset})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(INSTR(SUBSTR({str},{offset} + 1),{subStr}) + {offset})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(STRPOS(SUBSTR({str},{offset} + 1),{subStr}) + {offset})")
    public static int indexOf(String str, String subStr, int offset) {
        boom();
        return 0;
    }

    /**
     * 将字符串转换为小写
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "LOWER({str})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LOWER({str})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LOWER({str})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "LOWER({str})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "LOWER({str})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "LOWER({str})")
    public static String toLowerCase(String str) {
        boom();
        return "";
    }

    /**
     * 将字符串转换为大写
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "UPPER({str})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "UPPER({str})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "UPPER({str})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "UPPER({str})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "UPPER({str})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "UPPER({str})")
    public static String toUpperCase(String str) {
        boom();
        return "";
    }

    /**
     * 返回字符串中从左开始的指定数量字符
     *
     * @param length 指定的数量
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "LEFT({str},{length})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LEFT({str},{length})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "SUBSTR({str},1,{length})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "LEFT({str},{length})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "SUBSTR({str},1,{length})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "LEFT({str},{length})")
    public static String left(String str, int length) {
        boom();
        return "";
    }

    /**
     * 返回字符串中从右开始的指定数量字符
     *
     * @param length 指定的数量
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "RIGHT({str},{length})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "RIGHT({str},{length})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "SUBSTR({str},LENGTH({str}) - ({length} - 1),{length})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "RIGHT({str},{length})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "SUBSTR({str},LENGTH({str}) - ({length} - 1),{length})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "RIGHT({str},{length})")
    public static String right(String str, int length) {
        boom();
        return "";
    }

    /**
     * 将字符串左侧重复指定字符以填充到指定长度
     *
     * @param length  指定长度
     * @param lpadStr 指定字符
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "LPAD({str},{length},{lpadStr})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LPAD({str},{length},{lpadStr})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LPAD({str},{length},{lpadStr})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "IIF({length} - LEN({str}) <= 0,{str},CONCAT(REPLICATE({lpadStr},{length} - LEN({str})),{str}))")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "IIF({length} - LENGTH({str}) <= 0,{str},(REPLICATE({lpadStr},{length} - LENGTH({str}))||{str}))")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "LPAD({str},{length},{lpadStr})")
    public static String leftPad(String str, int length, String lpadStr) {
        boom();
        return "";
    }

    /**
     * 将字符串右侧重复指定字符以填充到指定长度
     *
     * @param length  指定长度
     * @param rpadStr 指定字符
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "RPAD({str},{length},{rpadStr})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "RPAD({str},{length},{rpadStr})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "RPAD({str},{length},{rpadStr})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "IIF({length} - LEN({str}) <= 0,{str},CONCAT({str},REPLICATE({rpadStr},{length} - LEN({str}))))")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "IIF({length} - LENGTH({str}) <= 0,{str},({str}||REPLICATE({rpadStr},{length} - LENGTH({str}))))")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "RPAD({str},{length},{rpadStr})")
    public static String rightPad(String str, int length, String rpadStr) {
        boom();
        return "";
    }

    /**
     * 去除字符串两端空格
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "TRIM({str})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "TRIM({str})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "TRIM({str})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "TRIM({str})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "TRIM({str})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "TRIM({str})")
    public static String trim(String str) {
        boom();
        return "";
    }

    /**
     * 去除字符串左侧的空格
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "LTRIM({str})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "LTRIM({str})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "LTRIM({str})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "LTRIM({str})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "LTRIM({str})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "LTRIM({str})")
    public static String trimStart(String str) {
        boom();
        return "";
    }

    /**
     * 去除字符串右侧的空格
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "RTRIM({str})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "RTRIM({str})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "RTRIM({str})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "RTRIM({str})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "RTRIM({str})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "RTRIM({str})")
    public static String trimEnd(String str) {
        boom();
        return "";
    }

    /**
     * 替换字符串
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "REPLACE({cur},{subs},{news})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "REPLACE({cur},{subs},{news})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "REPLACE({cur},{subs},{news})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "REPLACE({cur},{subs},{news})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "REPLACE({cur},{subs},{news})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "REPLACE({cur},{subs},{news})")
    public static String replace(String cur, String subs, String news) {
        boom();
        return "";
    }

    /**
     * 反转字符串
     * <p>
     * <b><span style='color:red;'>特殊说明</span>：oracle的REVERSE函数似乎只支持ASCII。</b>
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "REVERSE({str})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "REVERSE({str})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "REVERSE({str})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "REVERSE({str})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "REVERSE({str})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "REVERSE({str})")
    public static String reverse(String str) {
        boom();
        return "";
    }

    /**
     * 比较两个字符串的大小
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "STRCMP({s1},{s2})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "STRCMP({s1},{s2})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "(CASE WHEN {s1} < {s2} THEN -1 WHEN {s1} = {s2} THEN 0 ELSE 1 END)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "(CASE WHEN {s1} < {s2} THEN -1 WHEN {s1} = {s2} THEN 0 ELSE 1 END)")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "(CASE WHEN {s1} < {s2} THEN -1 WHEN {s1} = {s2} THEN 0 ELSE 1 END)")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(CASE WHEN {s1} < {s2} THEN -1 WHEN {s1} = {s2} THEN 0 ELSE 1 END)")
    public static int compare(String s1, String s2) {
        boom();
        return 0;
    }

    /**
     * 截取字符串
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "SUBSTR({str},{beginIndex})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "SUBSTR({str},{beginIndex})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "SUBSTR({str},{beginIndex})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "SUBSTRING({str},{beginIndex},LEN({str}) - ({beginIndex} - 1))")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "SUBSTR({str},{beginIndex})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "SUBSTR({str},{beginIndex})")
    public static String subString(String str, int beginIndex) {
        boom();
        return "";
    }

    /**
     * 截取字符串
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "SUBSTR({str},{beginIndex},{endIndex})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "SUBSTR({str},{beginIndex},{endIndex})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "SUBSTR({str},{beginIndex},{endIndex})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "SUBSTRING({str},{beginIndex},{endIndex})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "SUBSTR({str},{beginIndex},{endIndex})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "SUBSTR({str},{beginIndex},{endIndex})")
    public static String subString(String str, int beginIndex, int endIndex) {
        boom();
        return "";
    }

    // endregion

    // region [控制流程]

    /**
     * 发起一段CASE表达式
     */
    @SqlExtensionExpression(template = "CASE {when} END")
    public static <R> R Case(When<R> when) {
        boom();
        return when.getResult();
    }

    /**
     * 发起一段CASE表达式
     */
    @SafeVarargs
    @SqlExtensionExpression(template = "CASE {when} {rs} END")
    public static <R> R Case(When<R> when, When<R>... rs) {
        boom();
        return when.getResult();
    }

    /**
     * 发起一段CASE表达式，并设置ELSE
     */
    @SqlExtensionExpression(template = "CASE {when} ELSE {elsePart} END")
    public static <R> R Case(R elsePart, When<R> when) {
        boom();
        return when.getResult();
    }

    /**
     * 发起一段CASE表达式，并设置ELSE
     */
    @SafeVarargs
    @SqlExtensionExpression(template = "CASE {when} {rs} ELSE {elsePart} END", separator = " ")
    public static <R> R Case(R elsePart, When<R> when, When<R>... rs) {
        boom();
        return elsePart;
    }

    /**
     * 编写CASE表达式的WHEN子句
     */
    @SqlExtensionExpression(template = "WHEN {condition} THEN {then}")
    public static <R> When<R> when(boolean condition, R then) {
        boom();
        return new When<>();
    }

    /**
     * @param condition 判断条件
     * @param truePart  如果条件成立，返回的值
     * @param falsePart 如果条件不成立，返回的值
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "IF({condition},{truePart},{falsePart})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "IF({condition},{truePart},{falsePart})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "(CASE WHEN {condition} THEN {truePart} ELSE {falsePart} END)")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "IIF({condition},{truePart},{falsePart})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "IIF({condition},{truePart},{falsePart})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "(CASE WHEN {condition} THEN {truePart} ELSE {falsePart} END)")
    public static <T> T If(boolean condition, T truePart, T falsePart) {
        boom();
        return (T) new Object();
    }

    /**
     * 如果值不为NULL，则返回该值，否则返回另一个值
     *
     * @param valueNotNull 如果值不为NULL，则返回该值
     * @param valueIsNull  如果值为NULL，则返回该值
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "IFNULL({valueNotNull},{valueIsNull})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "IFNULL({valueNotNull},{valueIsNull})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "NVL({valueNotNull},{valueIsNull})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "ISNULL({valueNotNull},{valueIsNull})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "IFNULL({valueNotNull},{valueIsNull})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "COALESCE({valueNotNull},{valueIsNull})")
    public static <T> T ifNull(T valueNotNull, T valueIsNull) {
        boom();
        return (T) new Object();
    }

    /**
     * 如果两个值相等，则返回NULL，否则返回第一个值
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "NULLIF({t1},{t2})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "NULLIF({t1},{t2})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "NULLIF({t1},{t2})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "NULLIF({t1},{t2})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "NULLIF({t1},{t2})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "NULLIF({t1},{t2})")
    public static <T> T nullIf(T t1, T t2) {
        boom();
        return (T) new Object();
    }

    /**
     * 类型转换
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "", extension = H2CastExtension.class)
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "", extension = MySqlCastExtension.class)
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "", extension = OracleCastExtension.class)
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "", extension = SQLServerCastExtension.class)
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "", extension = SqliteCastExtension.class)
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "", extension = PostgreSQLCastExtension.class)
    public static <T> T cast(Object value, Class<T> targetType) {
        boom();
        return (T) new Object();
    }

    /**
     * 类型转换
     */
    @SqlExtensionExpression(dbType = DbType.H2, template = "CAST({value} AS {targetType})")
    @SqlExtensionExpression(dbType = DbType.MySQL, template = "CAST({value} AS {targetType})")
    @SqlExtensionExpression(dbType = DbType.Oracle, template = "CAST({value} AS {targetType})")
    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CAST({value} AS {targetType})")
    @SqlExtensionExpression(dbType = DbType.SQLite, template = "CAST({value} AS {targetType})")
    @SqlExtensionExpression(dbType = DbType.PostgreSQL, template = "{value}::{targetType}")
    public static <T> T cast(Object value, SqlTypes<T> targetType) {
        boom();
        return (T) new Object();
    }

//    @SqlExtensionExpression(dbType = DbType.H2, template = "CAST({value} AS {targetType})")
//    @SqlExtensionExpression(dbType = DbType.MySQL, template = "CONVERT({targetType},{value})")
//    @SqlExtensionExpression(dbType = DbType.Oracle, template = "CAST({value} AS {targetType})")
//    @SqlExtensionExpression(dbType = DbType.SQLServer, template = "CONVERT({targetType},{value})")
//    public static <T> T convert(Object value, Class<T> targetType)
//    {
//        boom();
//        return (T) new Object();
//    }

    /**
     * 判断是否为NULL
     */
    @SqlExtensionExpression(template = "{t} IS NULL")
    public static <T> boolean isNull(T t) {
        boom();
        return false;
    }

    /**
     * 判断是否不为NULL
     */
    @SqlExtensionExpression(template = "{t} IS NOT NULL")
    public static <T> boolean isNotNull(T t) {
        boom();
        return false;
    }

    // endregion

    /**
     * 请勿使用
     */
    private static class Num extends Number {
        private Num() {
        }

        @Override
        public int intValue() {
            return 0;
        }

        @Override
        public long longValue() {
            return 0;
        }

        @Override
        public float floatValue() {
            return 0;
        }

        @Override
        public double doubleValue() {
            return 0;
        }
    }

    /**
     * 请勿使用
     */
    public static class When<R> {
        public R getResult() {
            boom();
            return (R) new Object();
        }
    }
}
