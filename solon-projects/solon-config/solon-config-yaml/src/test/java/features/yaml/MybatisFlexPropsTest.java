package features.yaml;

import org.junit.jupiter.api.Test;
import org.noear.solon.SimpleSolonApp;
import org.noear.solon.core.Props;
import org.noear.solon.core.util.PropNameMapper;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * mybatis-flex 配置多命名风格变体交叉验证。
 *
 * <p>三份变体：
 * <pre>
 * V1: 顶层 kebab + 尾段 camel  （mybatis-flex.cacheEnabled）
 * V2: 顶层 kebab + 尾段 kebab  （mybatis-flex.cache-enabled）
 * V3: 顶层 camel + 尾段 kebab  （mybatisFlex.cache-enabled）
 * </pre>
 *
 * <p>覆盖点：
 * <ul>
 *   <li>YAML 加载 → 物理键保真（无双写）</li>
 *   <li>getProp 宽松前缀匹配（camel↔kebab）</li>
 *   <li>toBean 嵌套绑定（List + Boolean + 嵌套对象）</li>
 *   <li>三份变体产出相同 Bean 结果</li>
 * </ul>
 *
 * @author noear
 * @since 4.0
 */
public class MybatisFlexPropsTest {

    // ------------------------------------------------------------------
    // 扁平键手工构建（不依赖 YAML 加载）
    // ------------------------------------------------------------------

    /**
     * V1 扁平键：mybatis-flex.cacheEnabled（顶层 kebab + 尾段 camel）
     */
    private static Props loadFlatV1() {
        Props props = new Props();
        Properties src = new Properties();
        src.setProperty("mybatis-flex.defaultDatasourceKey", "db1");
        src.setProperty("mybatis-flex.typeAliasesPackage[0]", "demo4035.model");
        src.setProperty("mybatis-flex.mapperLocations[0]", "demo4035.dso.mapper");
        src.setProperty("mybatis-flex.configuration.cacheEnabled", "false");
        src.setProperty("mybatis-flex.configuration.mapUnderscoreToCamelCase", "true");
        src.setProperty("mybatis-flex.globalConfig.printBanner", "false");
        props.loadAdd(src);
        return props;
    }

    /**
     * V2 扁平键：mybatis-flex.cache-enabled（顶层 kebab + 尾段 kebab）
     */
    private static Props loadFlatV2() {
        Props props = new Props();
        Properties src = new Properties();
        src.setProperty("mybatis-flex.defaultDatasourceKey", "db1");
        src.setProperty("mybatis-flex.typeAliasesPackage[0]", "demo4035.model");
        src.setProperty("mybatis-flex.mapperLocations[0]", "demo4035.dso.mapper");
        src.setProperty("mybatis-flex.configuration.cache-enabled", "false");
        src.setProperty("mybatis-flex.configuration.mapUnderscoreToCamelCase", "true");
        src.setProperty("mybatis-flex.globalConfig.printBanner", "false");
        props.loadAdd(src);
        return props;
    }

    /**
     * V3 扁平键：mybatisFlex.cache-enabled（顶层 camel + 尾段 kebab）
     */
    private static Props loadFlatV3() {
        Props props = new Props();
        Properties src = new Properties();
        src.setProperty("mybatisFlex.default-datasource-key", "db1");
        src.setProperty("mybatisFlex.typeAliasesPackage[0]", "demo4035.model");
        src.setProperty("mybatisFlex.mapperLocations[0]", "demo4035.dso.mapper");
        src.setProperty("mybatisFlex.configuration.cache-enabled", "false");
        src.setProperty("mybatisFlex.configuration.map-underscore-to-camelCase", "true");
        src.setProperty("mybatisFlex.globalConfig.printBanner", "false");
        props.loadAdd(src);
        return props;
    }

    // ------------------------------------------------------------------
    // 物理键保真：无双写
    // ------------------------------------------------------------------

    @Test
    public void v1_physicalFidelity_noDualWrite() {
        Props props = loadFlatV1();

        assertEquals(6, props.size());
        assertTrue(props.containsKey("mybatis-flex.defaultDatasourceKey"));
        assertTrue(props.containsKey("mybatis-flex.configuration.cacheEnabled"));
        assertFalse(props.containsKey("mybatisFlex.defaultDatasourceKey"));
        assertFalse(props.containsKey("mybatis-flex.configuration.cache-enabled"));
    }

    @Test
    public void v2_physicalFidelity_noDualWrite() {
        Props props = loadFlatV2();

        assertEquals(6, props.size());
        assertTrue(props.containsKey("mybatis-flex.defaultDatasourceKey"));
        assertTrue(props.containsKey("mybatis-flex.configuration.cache-enabled"));
        assertFalse(props.containsKey("mybatis-flex.configuration.cacheEnabled"));
        assertFalse(props.containsKey("mybatisFlex.configuration.cache-enabled"));
    }

    @Test
    public void v3_physicalFidelity_noDualWrite() {
        Props props = loadFlatV3();

        assertEquals(6, props.size());
        assertTrue(props.containsKey("mybatisFlex.default-datasource-key"));
        assertTrue(props.containsKey("mybatisFlex.configuration.cache-enabled"));
        assertFalse(props.containsKey("mybatis-flex.default-datasource-key"));
        assertFalse(props.containsKey("mybatisFlex.configuration.cacheEnabled"));
    }

    // ------------------------------------------------------------------
    // getProp 宽松前缀 + 尾段宽松读
    // ------------------------------------------------------------------

    @Test
    public void v1_getProp_camelPrefix_matchesKebabPhysical() {
        // 顶层 kebab 物理键，用 camel 前缀读
        Props props = loadFlatV1();
        Props sub = props.getProp("mybatisFlex");

        assertTrue(sub.size() >= 6);
        assertEquals("db1", sub.get("defaultDatasourceKey"));
        assertEquals("false", sub.get("configuration.cacheEnabled"));
        assertEquals("true", sub.get("configuration.mapUnderscoreToCamelCase"));
        assertEquals("false", sub.get("globalConfig.printBanner"));

        // 尾段 kebab 读也能命中 camel 物理键
        assertEquals("false", sub.get("configuration.cache-enabled"));
    }

    @Test
    public void v2_getProp_camelPrefix_andKebabSuffix() {
        // 顶层 kebab + 尾段 kebab
        Props props = loadFlatV2();
        Props sub = props.getProp("mybatisFlex");

        assertTrue(sub.size() >= 6);
        assertEquals("db1", sub.get("defaultDatasourceKey"));
        // 尾段 kebab → logicalKey = camel
        assertEquals("false", sub.get("configuration.cacheEnabled"));
        assertEquals("false", sub.get("configuration.cache-enabled"));
        assertEquals("true", sub.get("configuration.mapUnderscoreToCamelCase"));
        assertEquals("false", sub.get("globalConfig.printBanner"));
    }

    @Test
    public void v3_getProp_kebabPrefix_matchesCamelPhysical() {
        // 顶层 camel 物理键，用 kebab 前缀读
        Props props = loadFlatV3();
        Props sub = props.getProp("mybatis-flex");

        assertTrue(sub.size() >= 6);
        assertEquals("db1", sub.get("defaultDatasourceKey"));
        assertEquals("db1", sub.get("default-datasource-key"));
        assertEquals("false", sub.get("configuration.cacheEnabled"));
        assertEquals("false", sub.get("configuration.cache-enabled"));
    }

    @Test
    public void v3_getProp_camelPrefix_exactMatch() {
        // 顶层 camel 物理键，用 camel 前缀读（同形）
        Props props = loadFlatV3();
        Props sub = props.getProp("mybatisFlex");

        assertTrue(sub.size() >= 6);
        assertEquals("db1", sub.get("defaultDatasourceKey"));
        assertEquals("false", sub.get("configuration.cache-enabled"));
    }

    // ------------------------------------------------------------------
    // 嵌套 getProp 多段前缀
    // ------------------------------------------------------------------

    @Test
    public void v1_nestedGetProp_configuration() {
        Props props = loadFlatV1();
        Props cfg = props.getProp("mybatisFlex.configuration");

        assertTrue(cfg.size() >= 2);
        assertEquals("false", cfg.get("cacheEnabled"));
        assertEquals("true", cfg.get("mapUnderscoreToCamelCase"));
    }

    @Test
    public void v2_nestedGetProp_configuration() {
        Props props = loadFlatV2();
        // 多段前缀：mybatisFlex.configuration → 匹配 mybatis-flex.configuration.*
        Props cfg = props.getProp("mybatisFlex.configuration");

        assertTrue(cfg.size() >= 2);
        assertEquals("false", cfg.get("cacheEnabled"));
        assertEquals("true", cfg.get("mapUnderscoreToCamelCase"));
    }

    @Test
    public void v3_nestedGetProp_configuration() {
        Props props = loadFlatV3();
        // 多段前缀：mybatis-flex.configuration → 匹配 mybatisFlex.configuration.*
        Props cfg = props.getProp("mybatis-flex.configuration");

        assertTrue(cfg.size() >= 2);
        assertEquals("false", cfg.get("cacheEnabled"));
        assertEquals("false", cfg.get("cache-enabled"));
    }

    // ------------------------------------------------------------------
    // toBean 嵌套绑定
    // ------------------------------------------------------------------

    @Test
    public void v1_toBean_fullBind() {
        Props props = loadFlatV1();
        MybatisFlexProps cfg = props.toBean("mybatisFlex", MybatisFlexProps.class);

        assertNotNull(cfg);
        assertEquals("db1", cfg.getDefaultDatasourceKey());

        assertNotNull(cfg.getTypeAliasesPackage());
        assertEquals(1, cfg.getTypeAliasesPackage().size());
        assertEquals("demo4035.model", cfg.getTypeAliasesPackage().get(0));

        assertNotNull(cfg.getMapperLocations());
        assertEquals(1, cfg.getMapperLocations().size());
        assertEquals("demo4035.dso.mapper", cfg.getMapperLocations().get(0));

        assertNotNull(cfg.getConfiguration());
        assertEquals(false, cfg.getConfiguration().getCacheEnabled());
        assertEquals(true, cfg.getConfiguration().getMapUnderscoreToCamelCase());

        assertNotNull(cfg.getGlobalConfig());
        assertEquals(false, cfg.getGlobalConfig().getPrintBanner());
    }

    @Test
    public void v2_toBean_fullBind() {
        Props props = loadFlatV2();
        MybatisFlexProps cfg = props.toBean("mybatisFlex", MybatisFlexProps.class);

        assertNotNull(cfg);
        assertEquals("db1", cfg.getDefaultDatasourceKey());

        assertNotNull(cfg.getTypeAliasesPackage());
        assertEquals("demo4035.model", cfg.getTypeAliasesPackage().get(0));

        assertNotNull(cfg.getMapperLocations());
        assertEquals("demo4035.dso.mapper", cfg.getMapperLocations().get(0));

        assertNotNull(cfg.getConfiguration());
        assertEquals(false, cfg.getConfiguration().getCacheEnabled());
        assertEquals(true, cfg.getConfiguration().getMapUnderscoreToCamelCase());

        assertNotNull(cfg.getGlobalConfig());
        assertEquals(false, cfg.getGlobalConfig().getPrintBanner());
    }

    @Test
    public void v3_toBean_fullBind() {
        Props props = loadFlatV3();
        // 顶层 camel 物理键，用 kebab 前缀读
        MybatisFlexProps cfg = props.toBean("mybatis-flex", MybatisFlexProps.class);

        assertNotNull(cfg);
        assertEquals("db1", cfg.getDefaultDatasourceKey());

        assertNotNull(cfg.getTypeAliasesPackage());
        assertEquals("demo4035.model", cfg.getTypeAliasesPackage().get(0));

        assertNotNull(cfg.getMapperLocations());
        assertEquals("demo4035.dso.mapper", cfg.getMapperLocations().get(0));

        assertNotNull(cfg.getConfiguration());
        assertEquals(false, cfg.getConfiguration().getCacheEnabled());
        assertEquals(true, cfg.getConfiguration().getMapUnderscoreToCamelCase());

        assertNotNull(cfg.getGlobalConfig());
        assertEquals(false, cfg.getGlobalConfig().getPrintBanner());
    }

    // ------------------------------------------------------------------
    // 三变体交叉一致性：toBean 结果相同
    // ------------------------------------------------------------------

    @Test
    public void allVariants_produceSameBean() {
        MybatisFlexProps v1 = loadFlatV1().toBean("mybatisFlex", MybatisFlexProps.class);
        MybatisFlexProps v2 = loadFlatV2().toBean("mybatisFlex", MybatisFlexProps.class);
        MybatisFlexProps v3 = loadFlatV3().toBean("mybatis-flex", MybatisFlexProps.class);

        // defaultDatasourceKey
        assertEquals(v1.getDefaultDatasourceKey(), v2.getDefaultDatasourceKey());
        assertEquals(v2.getDefaultDatasourceKey(), v3.getDefaultDatasourceKey());

        // typeAliasesPackage
        assertEquals(v1.getTypeAliasesPackage(), v2.getTypeAliasesPackage());
        assertEquals(v2.getTypeAliasesPackage(), v3.getTypeAliasesPackage());

        // mapperLocations
        assertEquals(v1.getMapperLocations(), v2.getMapperLocations());
        assertEquals(v2.getMapperLocations(), v3.getMapperLocations());

        // configuration.cacheEnabled
        assertEquals(v1.getConfiguration().getCacheEnabled(), v2.getConfiguration().getCacheEnabled());
        assertEquals(v2.getConfiguration().getCacheEnabled(), v3.getConfiguration().getCacheEnabled());

        // configuration.mapUnderscoreToCamelCase
        assertEquals(v1.getConfiguration().getMapUnderscoreToCamelCase(),
                v2.getConfiguration().getMapUnderscoreToCamelCase());
        assertEquals(v2.getConfiguration().getMapUnderscoreToCamelCase(),
                v3.getConfiguration().getMapUnderscoreToCamelCase());

        // globalConfig.printBanner
        assertEquals(v1.getGlobalConfig().getPrintBanner(), v2.getGlobalConfig().getPrintBanner());
        assertEquals(v2.getGlobalConfig().getPrintBanner(), v3.getGlobalConfig().getPrintBanner());
    }

    // ------------------------------------------------------------------
    // YAML 全链路加载 → toBean
    // ------------------------------------------------------------------

    @Test
    public void yamlV1_load_thenToBean() throws Throwable {
        SimpleSolonApp app = new SimpleSolonApp(MybatisFlexPropsTest.class,
                "-cfg=app-mybatis-flex-v1.yml",
                "-testing=1");
        app.start(x -> x.enableScanning(false));

        Props cfgRoot = app.cfg();

        // 物理键保真
        assertEquals("db1", cfgRoot.get("mybatis-flex.defaultDatasourceKey"));
        assertEquals("false", cfgRoot.get("mybatis-flex.configuration.cacheEnabled"));

        // toBean
        MybatisFlexProps cfg = cfgRoot.toBean("mybatisFlex", MybatisFlexProps.class);
        assertNotNull(cfg);
        assertEquals("db1", cfg.getDefaultDatasourceKey());
        assertEquals("demo4035.model", cfg.getTypeAliasesPackage().get(0));
        assertEquals(false, cfg.getConfiguration().getCacheEnabled());
        assertEquals(true, cfg.getConfiguration().getMapUnderscoreToCamelCase());
        assertEquals(false, cfg.getGlobalConfig().getPrintBanner());
    }

    @Test
    public void yamlV2_load_thenToBean() throws Throwable {
        SimpleSolonApp app = new SimpleSolonApp(MybatisFlexPropsTest.class,
                "-cfg=app-mybatis-flex-v2.yml",
                "-testing=1");
        app.start(x -> x.enableScanning(false));

        Props cfgRoot = app.cfg();

        // 物理键保真：尾段 kebab
        assertEquals("db1", cfgRoot.get("mybatis-flex.defaultDatasourceKey"));
        assertEquals("false", cfgRoot.get("mybatis-flex.configuration.cache-enabled"));

        // toBean：kebab 尾段 → camel 字段
        MybatisFlexProps cfg = cfgRoot.toBean("mybatisFlex", MybatisFlexProps.class);
        assertNotNull(cfg);
        assertEquals("db1", cfg.getDefaultDatasourceKey());
        assertEquals(false, cfg.getConfiguration().getCacheEnabled());
        assertEquals(true, cfg.getConfiguration().getMapUnderscoreToCamelCase());
    }

    @Test
    public void yamlV3_load_thenToBean() throws Throwable {
        SimpleSolonApp app = new SimpleSolonApp(MybatisFlexPropsTest.class,
                "-cfg=app-mybatis-flex-v3.yml",
                "-testing=1");
        app.start(x -> x.enableScanning(false));

        Props cfgRoot = app.cfg();

        // 物理键保真：顶层 camel + 尾段 kebab
        assertEquals("db1", cfgRoot.get("mybatisFlex.default-datasource-key"));
        assertEquals("false", cfgRoot.get("mybatisFlex.configuration.cache-enabled"));

        // toBean：camel 前缀匹配 + kebab 尾段 → camel 字段
        MybatisFlexProps cfg = cfgRoot.toBean("mybatisFlex", MybatisFlexProps.class);
        assertNotNull(cfg);
        assertEquals("db1", cfg.getDefaultDatasourceKey());
        assertEquals(false, cfg.getConfiguration().getCacheEnabled());
    }

    // ------------------------------------------------------------------
    // Mapper 往返
    // ------------------------------------------------------------------

    @Test
    public void mapper_roundTrip_mybatisFlex() {
        // mybatis-flex ↔ mybatisFlex
        assertEquals("mybatisFlex", PropNameMapper.alternate("mybatis-flex"));
        assertEquals("mybatis-flex", PropNameMapper.alternate("mybatisFlex"));

        // cache-enabled ↔ cacheEnabled
        assertEquals("cacheEnabled", PropNameMapper.alternate("cache-enabled"));
        assertEquals("cache-enabled", PropNameMapper.alternate("cacheEnabled"));

        // default-datasource-key ↔ defaultDatasourceKey
        assertEquals("defaultDatasourceKey", PropNameMapper.alternate("default-datasource-key"));
        assertEquals("default-datasource-key", PropNameMapper.alternate("defaultDatasourceKey"));

        // map-underscore-to-camelCase ↔ ?
        // 注意：map-underscore-to-camelCase 的 snakeToCamel 会产生 mapUnderscoreToCamelCase
        assertEquals("mapUnderscoreToCamelCase",
                PropNameMapper.logicalKey("map-underscore-to-camelCase"));
    }

    // ------------------------------------------------------------------
    // getMap / getList / find 宽松
    // ------------------------------------------------------------------

    @Test
    public void v1_getMap_camelPrefix() {
        Props props = loadFlatV1();
        java.util.Map<String, String> map = props.getMap("mybatisFlex");
        assertTrue(map.size() >= 6);
    }

    @Test
    public void v1_getList_typeAliases() {
        Props props = loadFlatV1();
        Props sub = props.getProp("mybatisFlex");
        List<String> list = sub.getList("typeAliasesPackage");
        assertEquals(1, list.size());
        assertEquals("demo4035.model", list.get(0));
    }

    @Test
    public void v2_find_camelPrefix() {
        Props props = loadFlatV2();
        java.util.Map<String, String> found = new java.util.LinkedHashMap<>();
        props.find("mybatisFlex", found::put);
        assertFalse(found.isEmpty());
    }
}
