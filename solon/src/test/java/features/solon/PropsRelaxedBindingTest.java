package features.solon;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.Props;
import org.noear.solon.core.util.PropNameMapper;
import org.noear.solon.core.util.PropUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Props 宽松绑定契约：存储保真 + kebab/camel 读时映射。
 * <p>
 * 契约要点：仅 {@code getProperty}/{@code get(String)} 等读路径宽松；
 * {@code Map#get}/{@code containsKey}/{@code remove}/forEach 保持物理键语义。
 *
 * @author noear
 * @since 4.0
 */
public class PropsRelaxedBindingTest {

    // ------------------------------------------------------------------
    // PropNameMapper：转换与候选
    // ------------------------------------------------------------------

    @Test
    public void propNameMapper_camelToKebab() {
        assertEquals("jdbc-url", PropNameMapper.camelToKebab("jdbcUrl"));
        assertEquals("server-port", PropNameMapper.camelToKebab("serverPort"));
        assertEquals("a-b-c", PropNameMapper.camelToKebab("aBC"));
        assertEquals("user-i-d", PropNameMapper.camelToKebab("userID"));
        assertEquals("URL", PropNameMapper.camelToKebab("URL")); // 无小写+大写边界，不转
        assertEquals("jdbc", PropNameMapper.camelToKebab("jdbc"));
        assertEquals("jdbc-url", PropNameMapper.camelToKebab("jdbc-url")); // 已是 kebab
        assertNull(PropNameMapper.camelToKebab(null));
        assertEquals("", PropNameMapper.camelToKebab(""));
        assertEquals("A", PropNameMapper.camelToKebab("A"));
        assertEquals("a", PropNameMapper.camelToKebab("a"));
        assertEquals("http-url-path", PropNameMapper.camelToKebab("httpUrlPath"));
    }

    @Test
    public void propNameMapper_logicalKey() {
        assertEquals("jdbcUrl", PropNameMapper.logicalKey("jdbc-url"));
        assertEquals("jdbcUrl", PropNameMapper.logicalKey("jdbcUrl"));
        assertEquals(".jdbcUrl", PropNameMapper.logicalKey(".jdbc-url"));
        assertEquals("userName", PropNameMapper.logicalKey("user-name"));
        assertNull(PropNameMapper.logicalKey(null));
        assertEquals("", PropNameMapper.logicalKey(""));
        assertEquals("plain", PropNameMapper.logicalKey("plain"));
        assertEquals("aBC", PropNameMapper.logicalKey("a-b-c"));
    }

    @Test
    public void propNameMapper_candidates() {
        assertTrue(PropNameMapper.candidates(null).isEmpty());
        assertTrue(PropNameMapper.candidates("").isEmpty());

        List<String> kebab = PropNameMapper.candidates("jdbc-url");
        assertEquals(2, kebab.size());
        assertEquals("jdbc-url", kebab.get(0));
        assertEquals("jdbcUrl", kebab.get(1));

        List<String> camel = PropNameMapper.candidates("jdbcUrl");
        assertEquals(2, camel.size());
        assertEquals("jdbcUrl", camel.get(0));
        assertEquals("jdbc-url", camel.get(1));

        // 无别名：单元素
        List<String> plain = PropNameMapper.candidates("server.port");
        assertEquals(1, plain.size());
        assertEquals("server.port", plain.get(0));

        List<String> upper = PropNameMapper.candidates("URL");
        assertEquals(1, upper.size());
        assertEquals("URL", upper.get(0));

        // 已是 kebab 再 candidates 仍可生成 camel
        List<String> multi = PropNameMapper.candidates("a-b-c");
        assertEquals("a-b-c", multi.get(0));
        assertEquals("aBC", multi.get(1));
    }

    @Test
    public void propNameMapper_alternate() {
        // 热路径：单值 API，无别名返回 null（零 List 分配；无 mayHave 预检双扫描）
        assertNull(PropNameMapper.alternate(null));
        assertNull(PropNameMapper.alternate(""));
        assertNull(PropNameMapper.alternate("plain"));
        assertNull(PropNameMapper.alternate("URL"));
        assertNull(PropNameMapper.alternate("server.port"));
        assertNull(PropNameMapper.alternate("jdbc"));
        assertNull(PropNameMapper.alternate("ALLCAPS"));

        assertEquals("jdbcUrl", PropNameMapper.alternate("jdbc-url"));
        assertEquals("jdbc-url", PropNameMapper.alternate("jdbcUrl"));
        assertEquals("aBC", PropNameMapper.alternate("a-b-c"));
        assertEquals("a-b-c", PropNameMapper.alternate("aBC"));
        // 有驼峰：仅一遍 camelToKebab（不再 mayHave + camelToKebab 双扫）
        assertEquals("user-name", PropNameMapper.alternate("userName"));
    }

    @Test
    public void propNameMapper_alternates() {
        // 兼容 List 形态 API
        assertTrue(PropNameMapper.alternates(null).isEmpty());
        assertTrue(PropNameMapper.alternates("").isEmpty());
        assertTrue(PropNameMapper.alternates("plain").isEmpty());
        assertTrue(PropNameMapper.alternates("URL").isEmpty());
        assertTrue(PropNameMapper.alternates("server.port").isEmpty());

        List<String> fromKebab = PropNameMapper.alternates("jdbc-url");
        assertEquals(1, fromKebab.size());
        assertEquals("jdbcUrl", fromKebab.get(0));

        List<String> fromCamel = PropNameMapper.alternates("jdbcUrl");
        assertEquals(1, fromCamel.size());
        assertEquals("jdbc-url", fromCamel.get(0));

        assertTrue(PropNameMapper.alternates("jdbc").isEmpty());
    }

    @Test
    public void snakeToCamel_singlePass_matchesEdges() {
        // 与历史 split 语义对齐（含空段）
        assertEquals("jdbcUrl", org.noear.solon.Utils.snakeToCamel("jdbc-url"));
        assertEquals("aBC", org.noear.solon.Utils.snakeToCamel("a-b-c"));
        assertEquals("itemProp12", org.noear.solon.Utils.snakeToCamel("item-prop-12"));
        assertEquals("plain", org.noear.solon.Utils.snakeToCamel("plain"));
        assertNull(org.noear.solon.Utils.snakeToCamel(null));
        assertEquals("a", org.noear.solon.Utils.snakeToCamel("a-"));
        assertEquals("A", org.noear.solon.Utils.snakeToCamel("-a"));
        assertEquals("aB", org.noear.solon.Utils.snakeToCamel("a--b"));
        assertEquals("aB", org.noear.solon.Utils.snakeToCamel("a-b-"));
        assertEquals("", org.noear.solon.Utils.snakeToCamel("-"));
        assertEquals("", org.noear.solon.Utils.snakeToCamel("--"));
        assertEquals("AB", org.noear.solon.Utils.snakeToCamel("-a-b"));
    }

    @Test
    public void propNameMapper_mayHaveAlternate() {
        assertFalse(PropNameMapper.mayHaveAlternate(null));
        assertFalse(PropNameMapper.mayHaveAlternate(""));
        assertFalse(PropNameMapper.mayHaveAlternate("plain"));
        assertFalse(PropNameMapper.mayHaveAlternate("URL"));
        assertFalse(PropNameMapper.mayHaveAlternate("server.port"));
        assertFalse(PropNameMapper.mayHaveAlternate("a"));
        assertFalse(PropNameMapper.mayHaveAlternate("jdbc"));

        assertTrue(PropNameMapper.mayHaveAlternate("jdbc-url"));
        assertTrue(PropNameMapper.mayHaveAlternate("jdbcUrl"));
        assertTrue(PropNameMapper.mayHaveAlternate("aBC"));
        assertTrue(PropNameMapper.mayHaveAlternate("aB"));
        assertTrue(PropNameMapper.mayHaveAlternate("a-b"));

        // alternate 不依赖 mayHaveAlternate 预检：两边结果仍一致
        String[] keys = {"plain", "URL", "jdbc", "jdbc-url", "jdbcUrl", "aB", "a-b", "server.port"};
        for (String k : keys) {
            boolean may = PropNameMapper.mayHaveAlternate(k);
            String alt = PropNameMapper.alternate(k);
            if (!may) {
                assertNull(alt, k);
            } else {
                // may=true 时可能仍无实质别名（如仅含 - 的退化键），但典型键应有别名
                if ("jdbc-url".equals(k) || "jdbcUrl".equals(k) || "aB".equals(k) || "a-b".equals(k)) {
                    assertNotNull(alt, k);
                }
            }
        }
    }

    @Test
    public void propNameMapper_extremeEdges() {
        // 单字符驼峰
        assertEquals("a-b", PropNameMapper.camelToKebab("aB"));
        assertEquals("aB", PropNameMapper.alternate("a-b"));
        assertEquals("a-b", PropNameMapper.alternate("aB"));

        // 已含 '-' 的 camelToKebab 原样
        assertEquals("jdbc-url", PropNameMapper.camelToKebab("jdbc-url"));
        assertEquals("a--b", PropNameMapper.camelToKebab("a--b"));

        // candidates 精确键永远 index 0
        assertEquals("jdbcUrl", PropNameMapper.candidates("jdbcUrl").get(0));
        assertEquals("jdbc-url", PropNameMapper.candidates("jdbc-url").get(0));

        // logicalKey 与 alternate 对称性
        assertEquals("jdbcUrl", PropNameMapper.logicalKey("jdbc-url"));
        assertEquals("jdbcUrl", PropNameMapper.logicalKey(PropNameMapper.alternate("jdbcUrl")));
        assertEquals("userName", PropNameMapper.logicalKey("user-name"));

        // 数字邻接
        assertEquals("p2pUrl", org.noear.solon.Utils.snakeToCamel("p2p-url"));
        assertEquals("http2Port", org.noear.solon.Utils.snakeToCamel("http2-port"));
    }

    // ------------------------------------------------------------------
    // 基础读写：get / getProperty / 默认值 / 类型转换
    // ------------------------------------------------------------------

    @Test
    public void loadKebab_getCamelAndKebab() {
        Props props = new Props();
        Properties src = new Properties();
        src.setProperty("db1.jdbc-url", "jdbc:mysql://localhost/demo");
        props.loadAdd(src);

        assertEquals("jdbc:mysql://localhost/demo", props.get("db1.jdbc-url"));
        assertEquals("jdbc:mysql://localhost/demo", props.get("db1.jdbcUrl"));
        assertEquals("jdbc:mysql://localhost/demo", props.getProperty("db1.jdbc-url"));
        assertEquals("jdbc:mysql://localhost/demo", props.getProperty("db1.jdbcUrl"));
        // 物理存储只有原文键
        assertTrue(props.containsKey("db1.jdbc-url"));
        assertFalse(props.containsKey("db1.jdbcUrl"));
        assertEquals(1, props.size());
    }

    @Test
    public void loadCamel_getKebab() {
        Props props = new Props();
        Properties src = new Properties();
        src.setProperty("db1.jdbcUrl", "jdbc:mysql://localhost/demo");
        props.loadAdd(src);

        assertEquals("jdbc:mysql://localhost/demo", props.get("db1.jdbcUrl"));
        assertEquals("jdbc:mysql://localhost/demo", props.get("db1.jdbc-url"));
        assertTrue(props.containsKey("db1.jdbcUrl"));
        assertFalse(props.containsKey("db1.jdbc-url"));
        assertEquals(1, props.size());
    }

    @Test
    public void putOnly_relaxedGet() {
        Props props = new Props();
        props.put("x-y", "1");

        assertEquals("1", props.get("x-y"));
        assertEquals("1", props.get("xY"));
        assertEquals("1", props.getProperty("xY"));
        assertFalse(props.containsKey("xY"));
        assertEquals(1, props.size());
    }

    @Test
    public void putCamel_getKebab() {
        Props props = new Props();
        props.put("serverPort", "8080");

        assertEquals("8080", props.get("serverPort"));
        assertEquals("8080", props.get("server-port"));
        assertFalse(props.containsKey("server-port"));
    }

    @Test
    public void exactKeyPreferred_whenBothExist() {
        Props props = new Props();
        props.put("jdbc-url", "a");
        props.put("jdbcUrl", "b");

        assertEquals("a", props.get("jdbc-url"));
        assertEquals("b", props.get("jdbcUrl"));
        assertEquals(2, props.size());
    }

    @Test
    public void get_withDefault() {
        Props props = new Props();
        props.put("jdbc-url", "v1");

        assertEquals("v1", props.get("jdbcUrl", "def"));
        assertEquals("v1", props.getProperty("jdbcUrl", "def"));
        assertEquals("def", props.get("missing", "def"));
        assertEquals("def", props.getProperty("missing", "def"));
        assertEquals("def", props.get("missing-key", "def"));
        assertNull(props.get("missing"));
        assertNull(props.getProperty("missing"));
    }

    @Test
    public void getByKeys_relaxed() {
        Props props = new Props();
        props.put("db.jdbc-url", "url-v");

        assertEquals("url-v", props.getByKeys("db.jdbcUrl", "other"));
        assertEquals("url-v", props.getByKeys("none", "db.jdbc-url"));
        assertEquals("url-v", props.getByKeys("none", "db.jdbcUrl"));
        assertNull(props.getByKeys("none1", "none2"));
        // 空串视为无值，继续尝试
        props.put("empty", "");
        assertEquals("url-v", props.getByKeys("empty", "db.jdbcUrl"));
    }

    @Test
    public void typedGetters_relaxed() {
        Props props = new Props();
        props.put("feature.enabled-flag", "true");
        props.put("server.max-threads", "16");
        props.put("cache.ttl-ms", "1000");
        props.put("ratio.scale-factor", "1.5");

        assertTrue(props.getBool("feature.enabledFlag", false));
        assertTrue(props.getBool("feature.enabled-flag", false));
        assertFalse(props.getBool("feature.missing", false));

        assertEquals(16, props.getInt("server.maxThreads", 0));
        assertEquals(16, props.getInt("server.max-threads", 0));
        assertEquals(7, props.getInt("server.missing", 7));

        assertEquals(1000L, props.getLong("cache.ttlMs", 0L));
        assertEquals(1000L, props.getLong("cache.ttl-ms", 0L));
        assertEquals(9L, props.getLong("cache.missing", 9L));

        assertEquals(1.5d, props.getDouble("ratio.scaleFactor", 0d));
        assertEquals(1.5d, props.getDouble("ratio.scale-factor", 0d));
        assertEquals(2.2d, props.getDouble("ratio.missing", 2.2d));
    }

    @Test
    public void getOrDefault_emptyUsesDef() {
        Props props = new Props();
        props.put("blank-value", "");
        props.put("ok-value", "9");

        assertEquals(3, props.getOrDefault("blankValue", 3, Integer::parseInt));
        assertEquals(9, props.getOrDefault("okValue", 3, Integer::parseInt));
        assertEquals(3, props.getOrDefault("missing-key", 3, Integer::parseInt));
    }

    // ------------------------------------------------------------------
    // defaults 链
    // ------------------------------------------------------------------

    @Test
    public void defaults_relaxedGet() {
        Properties defaults = new Properties();
        defaults.setProperty("db.jdbc-url", "from-defaults");
        defaults.setProperty("plain", "p0");

        Props props = new Props(defaults);
        assertEquals("from-defaults", props.get("db.jdbc-url"));
        assertEquals("from-defaults", props.get("db.jdbcUrl"));
        assertEquals("p0", props.get("plain"));

        // 子覆盖后 exact/alias 都走子值
        props.put("db.jdbc-url", "from-child");
        assertEquals("from-child", props.get("db.jdbcUrl"));
        assertEquals("from-child", props.get("db.jdbc-url"));

        // size 含 defaults 中未覆盖键
        assertTrue(props.size() >= 2);
    }

    @Test
    public void defaults_forEachDoesNotDuplicateCoveredKeys() {
        Properties defaults = new Properties();
        defaults.setProperty("a", "1");
        defaults.setProperty("b-key", "2");

        Props props = new Props(defaults);
        props.put("a", "9");

        Map<String, String> seen = new LinkedHashMap<>();
        props.forEach((k, v) -> seen.put(String.valueOf(k), String.valueOf(v)));

        assertEquals("9", seen.get("a"));
        assertEquals("2", seen.get("b-key"));
        assertEquals(2, seen.size());
        // 宽松读 defaults 中的 kebab
        assertEquals("2", props.get("bKey"));
    }

    // ------------------------------------------------------------------
    // 子视图：getProp / getMap / getList / grouped / listed / find
    // ------------------------------------------------------------------

    @Test
    public void getProp_dedupAndCamelLogicalKey() {
        Props props = new Props();
        Properties src = new Properties();
        src.setProperty("db1.jdbc-url", "url-v");
        src.setProperty("db1.user-name", "u1");
        props.loadAdd(src);

        Props sub = props.getProp("db1");
        assertEquals(2, sub.size());
        assertEquals("url-v", sub.get("jdbcUrl"));
        assertEquals("url-v", sub.get("jdbc-url"));
        assertEquals("u1", sub.get("userName"));
        assertEquals("u1", sub.get("user-name"));

        // 子视图物理键为逻辑 camel
        assertTrue(sub.containsKey("jdbcUrl"));
        assertTrue(sub.containsKey("userName"));

        Map<String, String> map = props.getMap("db1");
        assertEquals(2, map.size());
        assertEquals("url-v", map.get("jdbcUrl"));
        assertEquals("u1", map.get("userName"));
    }

    @Test
    public void getProp_dedupWhenBothKebabAndCamelExist_prefersCamelPhysical() {
        Props props = new Props();
        // 同逻辑键双物理键：camel 物理键优先（与 Hashtable 遍历顺序无关）
        props.put("db1.jdbc-url", "a");
        props.put("db1.jdbcUrl", "b");

        Props sub = props.getProp("db1");
        assertEquals(1, sub.size());
        assertEquals("b", sub.get("jdbcUrl"));
        assertEquals("b", sub.get("jdbc-url"));

        Map<String, String> map = props.getMap("db1");
        assertEquals(1, map.size());
        assertEquals("b", map.get("jdbcUrl"));

        // 反向插入顺序：仍优先 camel 物理值
        Props props2 = new Props();
        props2.put("db1.jdbcUrl", "camel-v");
        props2.put("db1.jdbc-url", "kebab-v");
        assertEquals("camel-v", props2.getProp("db1").get("jdbcUrl"));
    }

    @Test
    public void getProp_emptyPrefixReturnsSelf() {
        Props props = new Props();
        props.put("a", "1");
        assertSame(props, props.getProp(""));
        assertSame(props, props.getProp(null));
    }

    @Test
    public void getPropByExpr() {
        Props props = new Props();
        props.put("demo.jdbc-url", "u");
        props.put("demo.user-name", "n");

        Props sub1 = props.getPropByExpr("demo");
        assertEquals("u", sub1.get("jdbcUrl"));

        Props sub2 = props.getPropByExpr("${demo}");
        assertEquals("n", sub2.get("userName"));
    }

    @Test
    public void getList_arrayKeys() {
        Props props = new Props();
        props.put("items[0]", "a");
        props.put("items[1]", "b");
        props.put("items[2]", "c");

        List<String> list = props.getList("items");
        assertEquals(3, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    public void getGroupedProp_andListedProp() {
        Props props = new Props();
        props.put("ds.db1.jdbc-url", "u1");
        props.put("ds.db1.user-name", "n1");
        props.put("ds.db2.jdbc-url", "u2");

        Map<String, Props> grouped = props.getGroupedProp("ds");
        assertEquals(2, grouped.size());
        assertEquals("u1", grouped.get("db1").get("jdbcUrl"));
        assertEquals("n1", grouped.get("db1").get("userName"));
        assertEquals("u2", grouped.get("db2").get("jdbc-url"));

        Collection<Props> listed = props.getListedProp("ds");
        assertEquals(2, listed.size());
    }

    @Test
    public void find_emitsLogicalKeys() {
        Props props = new Props();
        props.put("p.jdbc-url", "v");
        props.put("p.max-size", "10");

        Map<String, String> found = new LinkedHashMap<>();
        props.find("p", found::put);

        assertEquals(2, found.size());
        // doFind 输出带前导点的 logical suffix：.jdbcUrl
        assertTrue(found.containsKey(".jdbcUrl") || found.containsKey("jdbcUrl")
                || found.keySet().stream().anyMatch(k -> k.endsWith("jdbcUrl")));
        assertEquals("v", props.getProp("p").get("jdbcUrl"));
        assertEquals("10", props.getProp("p").get("maxSize"));
    }

    // ------------------------------------------------------------------
    // Bean 绑定
    // ------------------------------------------------------------------

    @Test
    public void toBean_bindsCamelFieldFromKebabConfig() {
        Props props = new Props();
        Properties src = new Properties();
        src.setProperty("demo.jdbc-url", "jdbc:x");
        src.setProperty("demo.user-name", "root");
        props.loadAdd(src);

        DemoCfg cfg = props.toBean("demo", DemoCfg.class);
        assertNotNull(cfg);
        assertEquals("jdbc:x", cfg.getJdbcUrl());
        assertEquals("root", cfg.getUserName());
    }

    @Test
    public void toBean_rootAndBindTo() {
        Props props = new Props();
        props.put("jdbc-url", "jdbc:y");
        props.put("user-name", "admin");

        DemoCfg cfg = props.toBean(DemoCfg.class);
        assertEquals("jdbc:y", cfg.getJdbcUrl());
        assertEquals("admin", cfg.getUserName());

        DemoCfg cfg2 = new DemoCfg();
        props.bindTo(cfg2);
        assertEquals("jdbc:y", cfg2.getJdbcUrl());
        assertEquals("admin", cfg2.getUserName());
    }

    // ------------------------------------------------------------------
    // 模板 / PropUtil
    // ------------------------------------------------------------------

    @Test
    public void template_mainProps_relaxed() {
        Props props = new Props();
        Properties src = new Properties();
        src.setProperty("db1.jdbc-url", "jdbc:mysql://h/db");
        props.loadAdd(src);

        assertEquals("jdbc:mysql://h/db", props.getByTmpl("${db1.jdbcUrl}"));
        assertEquals("jdbc:mysql://h/db", props.getByTmpl("${db1.jdbc-url}"));
        assertEquals("prefix-jdbc:mysql://h/db-suffix",
                props.getByTmpl("prefix-${db1.jdbcUrl}-suffix"));
    }

    @Test
    public void template_loadTime_rawProperties_relaxed() {
        Props props = new Props();
        Properties src = new Properties();
        src.setProperty("db1.jdbc-url", "jdbc:mysql://h/db");
        // 用 camel 引用源文件里的 kebab 键
        src.setProperty("db1.url", "${db1.jdbcUrl}");
        props.loadAdd(src);

        assertEquals("jdbc:mysql://h/db", props.get("db1.url"));
    }

    @Test
    public void template_defaultValue_andExpr() {
        Props props = new Props();
        props.put("app.name-space", "demo");

        assertEquals("demo", props.getByExpr("${app.nameSpace}"));
        assertEquals("demo", props.getByExpr("app.name-space"));
        assertEquals("fallback", props.getByExpr("${missing:fallback}"));
        assertEquals("demo", props.getByTmpl("${app.name-space:other}"));
        assertEquals("x", props.getByTmpl("${none:x}"));
    }

    @Test
    public void propUtil_rawProperties_relaxed() {
        Properties raw = new Properties();
        raw.setProperty("jdbc-url", "v1");

        assertEquals("v1", PropUtil.getPropertyRelaxed(raw, "jdbc-url"));
        assertEquals("v1", PropUtil.getPropertyRelaxed(raw, "jdbcUrl"));
        assertNull(PropUtil.getPropertyRelaxed(raw, "missing"));
        assertNull(PropUtil.getPropertyRelaxed(null, "jdbcUrl"));
        assertNull(PropUtil.getPropertyRelaxed(raw, null));
        assertNull(PropUtil.getPropertyRelaxed(null, null));
    }

    @Test
    public void propUtil_propsInstance_usesOverride() {
        Props props = new Props();
        props.put("server-port", "9000");

        assertEquals("9000", PropUtil.getPropertyRelaxed(props, "serverPort"));
        assertEquals("9000", PropUtil.getPropertyRelaxed(props, "server-port"));
    }

    @Test
    public void propUtil_getByExp_targetAndMain() {
        Properties target = new Properties();
        target.setProperty("local.jdbc-url", "t-val");

        Props main = new Props();
        main.put("global.user-name", "m-val");

        assertEquals("t-val", PropUtil.getByExp(main, target, "local.jdbcUrl", null));
        assertEquals("m-val", PropUtil.getByExp(main, target, "global.userName", null));
        assertEquals("def", PropUtil.getByExp(main, target, "none:def", null));
        assertNull(PropUtil.getByExp(main, target, "none", null, false));
        // 键存在时用实际值，默认值不生效；${} 包装同样走 expSplit
        assertEquals("m-val", PropUtil.getByExp(main, null, "${global.user-name:def}", null));
        assertEquals("m-val", PropUtil.getByExp(main, null, "${global.userName:def}", null));
        assertEquals("fallback", PropUtil.getByExp(main, null, "${missing.key:fallback}", null));
        // 仅 main
        assertEquals("m-val", PropUtil.getByExp(main, null, "global.userName", null));
        // expSplit 要求 defIdx > 0，":d" 不会被拆成空 name；"k:" 可拆出空默认值
        assertArrayEquals(new String[]{"k", ""}, PropUtil.expSplit("k:"));
        assertEquals("", PropUtil.getByExp(main, target, "none:", null));
    }

    @Test
    public void propUtil_getByExp_relativeRef() {
        Props main = new Props();
        main.put("db.jdbc-url", "jdbc:z");
        main.put("db.user", "u");

        // refKey=db.user 时，.jdbc-url / .jdbcUrl 解析为 db.jdbc-url
        assertEquals("jdbc:z", PropUtil.getByExp(main, null, ".jdbc-url", "db.user"));
        assertEquals("jdbc:z", PropUtil.getByExp(main, null, ".jdbcUrl", "db.user"));
    }

    @Test
    public void propUtil_expSplit() {
        assertArrayEquals(new String[]{"k", null}, PropUtil.expSplit("${k}"));
        assertArrayEquals(new String[]{"k", "d"}, PropUtil.expSplit("${k:d}"));
        assertArrayEquals(new String[]{"k", ""}, PropUtil.expSplit("${k:}"));
        assertArrayEquals(new String[]{"k", "d"}, PropUtil.expSplit("k:d"));
        assertArrayEquals(new String[]{"k", null}, PropUtil.expSplit("k"));
    }

    @Test
    public void propUtil_getByTml_nestedAndInvalid() {
        Props main = new Props();
        main.put("a", "1");
        main.put("b", "${a}");
        main.put("c-name", "x");

        assertEquals("1", PropUtil.getByTml(main, null, "${b}", null));
        assertEquals("x", PropUtil.getByTml(main, null, "${cName}", null));
        assertEquals("1-x", PropUtil.getByTml(main, null, "${a}-${c-name}", null));
        assertNull(PropUtil.getByTml(main, null, null, null));
        assertEquals("", PropUtil.getByTml(main, null, "", null));
        assertThrows(IllegalStateException.class,
                () -> PropUtil.getByTml(main, null, "${no-end", null));
    }

    // ------------------------------------------------------------------
    // 加载：无双写 / ifAbsent / toSystem / from / addAll
    // ------------------------------------------------------------------

    @Test
    public void loadAdd_noPhysicalDualWrite() {
        Props props = new Props();
        Properties src = new Properties();
        src.setProperty("plain.key", "p1");
        src.setProperty("kebab-key", "k1");
        props.loadAdd(src);

        assertTrue(props.containsKey("plain.key"));
        assertTrue(props.containsKey("kebab-key"));
        assertFalse(props.containsKey("kebabKey"));
        assertEquals("k1", props.get("kebabKey"));
        assertEquals(2, props.size());
    }

    @Test
    public void loadAddIfAbsent_keepsExistingPhysicalKey() {
        Props props = new Props();
        props.put("jdbc-url", "old");

        Properties src = new Properties();
        src.setProperty("jdbc-url", "new");
        src.setProperty("user-name", "u");
        props.loadAddIfAbsent(src);

        assertEquals("old", props.get("jdbc-url"));
        assertEquals("old", props.get("jdbcUrl"));
        assertEquals("u", props.get("userName"));
    }

    @Test
    public void loadAddDo_toSystem_skipsKebab() {
        // 通过子类暴露 protected loadAddDo
        class PropsEx extends Props {
            void load(Properties p, boolean toSystem) {
                loadAddDo(p, toSystem, false);
            }
        }

        // 使用稳定 kebab（避免尾段纯数字导致 camelToKebab 无法还原 hyphen）
        String plainKey = "solon.test.plain.prop." + System.nanoTime();
        String kebabKey = "solon-test-kebab-unique";
        String camelKey = "solonTestKebabUnique";
            
        try {
            Properties src = new Properties();
            src.setProperty(plainKey, "pv");
            src.setProperty(kebabKey, "kv");
            
            PropsEx props = new PropsEx();
            props.load(src, true);
            
            assertEquals("pv", System.getProperty(plainKey));
            assertNull(System.getProperty(kebabKey));
            // kebab 仍可在 Props 内宽松读取，且不生成 camel 物理键、不同步 System
            assertEquals(camelKey, PropNameMapper.logicalKey(kebabKey));
            assertEquals("kv", props.get(kebabKey));
            assertEquals("kv", props.get(camelKey));
            assertFalse(props.containsKey(camelKey));
            assertNull(System.getProperty(camelKey));
        } finally {
            System.clearProperty(plainKey);
            System.clearProperty(kebabKey);
            System.clearProperty(camelKey);
        }
    }

    @Test
    public void propsFrom_andAddAll_relaxedGet() {
        Properties raw = new Properties();
        raw.setProperty("jdbc-url", "u");
        Props p1 = Props.from(raw);
        assertNotNull(p1);
        assertEquals("u", p1.get("jdbcUrl"));
        assertSame(p1, Props.from(p1));
        assertNull(Props.from(null));

        Props p2 = new Props();
        Map<String, String> map = new HashMap<>();
        map.put("user-name", "n");
        p2.addAll(map);
        assertEquals("n", p2.get("userName"));

        Props p3 = new Props();
        Properties more = new Properties();
        more.setProperty("max-size", "3");
        p3.addAll(more);
        assertEquals("3", p3.get("maxSize"));
    }

    @Test
    public void putIfNotNull_andOnChange() {
        Props props = new Props();
        AtomicInteger changes = new AtomicInteger();
        props.onChange((k, v) -> changes.incrementAndGet());

        props.putIfNotNull(null, "a");
        props.putIfNotNull("k", null);
        assertEquals(0, changes.get());

        props.putIfNotNull("jdbc-url", "v");
        assertEquals(1, changes.get());
        assertEquals("v", props.get("jdbcUrl"));
    }

    @Test
    public void put_conflictWarn_doesNotMergeValues() {
        Props props = new Props();
        props.put("jdbc-url", "a");
        // 不同值触发 warn（仅 stderr），值仍各自保留
        props.put("jdbcUrl", "b");

        assertEquals("a", props.get("jdbc-url"));
        assertEquals("b", props.get("jdbcUrl"));
        assertEquals(2, props.size());

        // 相同值不破坏
        props.put("user-name", "u");
        props.put("userName", "u");
        assertEquals("u", props.get("user-name"));
        assertEquals("u", props.get("userName"));
    }

    // ------------------------------------------------------------------
    // 遍历语义：无别名膨胀
    // ------------------------------------------------------------------

    @Test
    public void forEach_onlyPhysicalKeys() {
        Props props = new Props();
        props.put("jdbc-url", "a");
        props.put("serverPort", "1");

        List<String> keys = new ArrayList<>();
        props.forEach((k, v) -> keys.add(String.valueOf(k)));

        assertEquals(2, keys.size());
        assertTrue(keys.contains("jdbc-url"));
        assertTrue(keys.contains("serverPort"));
        assertFalse(keys.contains("jdbcUrl"));
        assertFalse(keys.contains("server-port"));
    }

    @Test
    public void multiSegment_keys_relaxed() {
        Props props = new Props();
        props.put("spring.datasource.jdbc-url", "jdbc:s");
        props.put("my.app.server-port", "80");

        assertEquals("jdbc:s", props.get("spring.datasource.jdbcUrl"));
        assertEquals("80", props.get("my.app.serverPort"));
        assertEquals("80", props.getProp("my.app").get("serverPort"));
        assertEquals("jdbc:s", props.getMap("spring.datasource").get("jdbcUrl"));
    }

    @Test
    public void missingKeys_returnNullNotException() {
        Props props = new Props();
        // getProperty(null) 与 JDK Properties 一致会 NPE，不在此包装
        assertThrows(NullPointerException.class, () -> props.get(null));
        assertNull(props.get(""));
        assertNull(props.get("no-such-key"));
        assertNull(props.get("noSuchKey"));
        assertEquals("d", props.get("noSuchKey", "d"));
        assertEquals("d", props.getProperty("no-such-key", "d"));
    }
    
    /**
     * 文档化限制：尾段为纯数字的 kebab，camel 反向还原可能丢 hyphen。
     * 例如 prop-123 → prop123 → camelToKebab 无法变回 prop-123。
     */
    @Test
    public void trailingNumericSegment_camelRoundTrip_limitation() {
        Props props = new Props();
        props.put("item-prop-12", "v");

        assertEquals("v", props.get("item-prop-12"));
        // snakeToCamel: itemProp12
        assertEquals("itemProp12", PropNameMapper.logicalKey("item-prop-12"));
        // 宽松 get(camel) 依赖 camelToKebab 还原；数字段前 hyphen 可能丢失
        String camel = PropNameMapper.logicalKey("item-prop-12");
        String back = PropNameMapper.camelToKebab(camel);
        // 记录实际行为：若还原失败则 get(camel) 为 null（实现限制，非回归）
        if (back.equals("item-prop-12")) {
            assertEquals("v", props.get(camel));
        } else {
            // 当前算法：itemProp12 → item-prop12
            assertEquals("item-prop12", back);
            assertNull(props.get(camel));
            // exact 仍可用
            assertEquals("v", props.get("item-prop-12"));
        }
    }

    // ------------------------------------------------------------------
    // 物理 API vs 宽松 API 分界 / defaults 交叉 / 批量加载
    // ------------------------------------------------------------------

    @Test
    public void mapGet_isPhysicalOnly_getPropertyIsRelaxed() {
        Props props = new Props();
        props.put("jdbc-url", "a");

        // 宽松：get(String) / getProperty
        assertEquals("a", props.get("jdbcUrl"));
        assertEquals("a", props.getProperty("jdbcUrl"));
        assertEquals("a", props.getProperty("jdbc-url"));

        // 物理：Map#get / containsKey 不识别别名
        assertNull(props.get((Object) "jdbcUrl"));
        assertEquals("a", props.get((Object) "jdbc-url"));
        assertFalse(props.containsKey("jdbcUrl"));
        assertTrue(props.containsKey("jdbc-url"));
    }

    @Test
    public void getProperty_emptyString_doesNotUseDefault() {
        Props props = new Props();
        props.put("k", "");

        // JDK Properties 语义：键存在且值为空串 ≠ 缺键
        assertEquals("", props.getProperty("k", "def"));
        assertEquals("", props.get("k", "def"));
        assertEquals("def", props.getProperty("missing", "def"));

        // getOrDefault 转换路径：空串视为无值，走默认
        assertEquals(3, props.getOrDefault("k", 3, Integer::parseInt));
    }

    @Test
    public void containsKey_remove_putIfAbsent_physicalOnly() {
        Props props = new Props();
        props.put("jdbc-url", "a");

        assertTrue(props.containsKey("jdbc-url"));
        assertFalse(props.containsKey("jdbcUrl"));

        // 按别名 remove 无效
        assertNull(props.remove("jdbcUrl"));
        assertEquals("a", props.getProperty("jdbcUrl"));
        assertEquals("a", props.getProperty("jdbc-url"));

        // 按物理键 remove
        assertEquals("a", props.remove("jdbc-url"));
        assertNull(props.getProperty("jdbcUrl"));
        assertNull(props.getProperty("jdbc-url"));

        // putIfAbsent 只认物理键：别名已有值时，putIfAbsent(camel) 仍会写入
        props.put("user-name", "u1");
        Object prev = props.putIfAbsent("userName", "u2");
        assertNull(prev); // 物理上 userName 不存在
        assertEquals("u1", props.getProperty("user-name"));
        assertEquals("u2", props.getProperty("userName")); // exact 优先
        assertEquals(2, props.size());

        // 物理键已存在则 putIfAbsent 不覆盖
        assertEquals("u1", props.putIfAbsent("user-name", "u3"));
        assertEquals("u1", props.getProperty("user-name"));
    }

    @Test
    public void defaults_aliasCross_childOverrideAndFallback() {
        Properties defaults = new Properties();
        defaults.setProperty("db.jdbc-url", "from-parent-kebab");
        defaults.setProperty("db.userName", "parent-user");

        Props child = new Props(defaults);

        // 子无物理键：宽松回落 defaults（含别名）
        assertEquals("from-parent-kebab", child.getProperty("db.jdbc-url"));
        assertEquals("from-parent-kebab", child.getProperty("db.jdbcUrl"));
        assertEquals("parent-user", child.getProperty("db.userName"));
        assertEquals("parent-user", child.getProperty("db.user-name"));

        // 子用 camel 覆盖：exact camel 取子值；kebab 读仍可能命中 defaults 的物理 kebab
        // （精确键优先：get("db.jdbc-url") 先命中 defaults 的 jdbc-url）
        child.put("db.jdbcUrl", "from-child-camel");
        assertEquals("from-child-camel", child.getProperty("db.jdbcUrl"));
        assertEquals("from-parent-kebab", child.getProperty("db.jdbc-url"));

        // 子用同物理 kebab 覆盖后，两种读法都走子值
        child.put("db.jdbc-url", "from-child-kebab");
        assertEquals("from-child-kebab", child.getProperty("db.jdbc-url"));
        // jdbcUrl 物理键仍在子上，exact 优先为 from-child-camel
        assertEquals("from-child-camel", child.getProperty("db.jdbcUrl"));
    }

    @Test
    public void addAll_putAll_noDualWrite_noAliasMerge() {
        Props props = new Props();
        Properties src = new Properties();
        src.setProperty("jdbc-url", "a");
        src.setProperty("jdbcUrl", "b");
        props.addAll(src);

        assertEquals(2, props.size());
        assertTrue(props.containsKey("jdbc-url"));
        assertTrue(props.containsKey("jdbcUrl"));
        assertEquals("a", props.getProperty("jdbc-url"));
        assertEquals("b", props.getProperty("jdbcUrl"));

        Props props2 = new Props();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("user-name", "n1");
        map.put("max-size", "3");
        props2.addAll(map);
        assertEquals(2, props2.size());
        assertFalse(props2.containsKey("userName"));
        assertEquals("n1", props2.get("userName"));
        assertEquals("3", props2.get("maxSize"));
    }

    @Test
    public void loadAdd_template_mixedRelaxedRefs() {
        Props props = new Props();
        Properties src = new Properties();
        src.setProperty("db.jdbc-url", "jdbc:mysql://h/db");
        // camel 引用源内 kebab
        src.setProperty("db.url", "${db.jdbcUrl}");
        // kebab 引用
        src.setProperty("app.name", "${db.jdbc-url}/n");
        // 混拼：模板内同时出现 camel 与 kebab 引用
        src.setProperty("app.mix", "${db.jdbcUrl}|${db.jdbc-url}");
        props.loadAdd(src);

        assertEquals("jdbc:mysql://h/db", props.get("db.url"));
        assertEquals("jdbc:mysql://h/db/n", props.get("app.name"));
        assertEquals("jdbc:mysql://h/db|jdbc:mysql://h/db", props.get("app.mix"));
        assertEquals(4, props.size());
        assertFalse(props.containsKey("db.jdbcUrl"));

        // 运行期模板默认值（非 load 首遍 useDef=false 路径）
        assertEquals("def-val", props.getByTmpl("${missing.jdbcUrl:def-val}"));
    }

    @Test
    public void onChange_receivesPhysicalKeyOnly() {
        Props props = new Props();
        List<String> keys = new ArrayList<>();
        props.onChange((k, v) -> keys.add(k));

        props.put("jdbc-url", "v1");
        props.put("serverPort", "80");

        assertEquals(2, keys.size());
        assertEquals("jdbc-url", keys.get(0));
        assertEquals("serverPort", keys.get(1));
        // 不会额外回调别名键
        assertFalse(keys.contains("jdbcUrl"));
        assertFalse(keys.contains("server-port"));
    }

    @Test
    public void bulkLoad_kebab_sizeEqualsN_noDualWrite() {
        Props props = new Props();
        Properties src = new Properties();
        int n = 200;
        for (int i = 0; i < n; i++) {
            src.setProperty("item" + i + ".jdbc-url", "v" + i);
            src.setProperty("item" + i + ".user-name", "u" + i);
        }
        props.loadAdd(src);

        assertEquals(n * 2, props.size());
        for (int i = 0; i < n; i++) {
            assertEquals("v" + i, props.get("item" + i + ".jdbcUrl"));
            assertEquals("u" + i, props.get("item" + i + ".userName"));
            assertTrue(props.containsKey("item" + i + ".jdbc-url"));
            assertFalse(props.containsKey("item" + i + ".jdbcUrl"));
        }
    }

    // ------------------------------------------------------------------

    public static class DemoCfg {
        private String jdbcUrl;
        private String userName;

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
