package features.yaml;

import org.junit.jupiter.api.Test;
import org.noear.solon.SimpleSolonApp;
import org.noear.solon.core.Props;
import org.noear.solon.core.util.ConvertUtil;
import org.noear.solon.core.util.PropNameMapper;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 真实配置形态回归：bean-searcher 嵌套 kebab + Duration。
 * <pre>
 * bean-searcher:
 *   params:
 *     pagination:
 *       start: 1
 *   exporter:
 *     batch-delay: PT0.5S
 * </pre>
 *
 * 覆盖：YAML 加载、物理键保真、getProp 逻辑键、嵌套 toBean、Duration。
 * <p>
 * 说明：{@code getProperty} 的别名映射作用在<strong>整键</strong>上。
 * 前缀含 {@code -}（如 {@code bean-searcher}）时，
 * {@code bean-searcher.exporter.batchDelay} 不会映射到
 * {@code bean-searcher.exporter.batch-delay}。
 * 生产路径是 {@code toBean("bean-searcher", ...)} → {@code getProp} 截前缀后
 * 再对后缀做 logical/camel 化，因此嵌套绑定仍然正确。
 *
 * @author noear
 * @since 4.0
 */
public class BeanSearcherPropsTest {

    // ------------------------------------------------------------------
    // 模型（字段 camel，配置 kebab）
    // ------------------------------------------------------------------

    public static class BeanSearcherProps {
        private Params params;
        private Exporter exporter;

        public Params getParams() {
            return params;
        }

        public void setParams(Params params) {
            this.params = params;
        }

        public Exporter getExporter() {
            return exporter;
        }

        public void setExporter(Exporter exporter) {
            this.exporter = exporter;
        }
    }

    public static class Params {
        private Pagination pagination;

        public Pagination getPagination() {
            return pagination;
        }

        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }
    }

    public static class Pagination {
        private int start;

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }
    }

    public static class Exporter {
        private Duration batchDelay;

        public Duration getBatchDelay() {
            return batchDelay;
        }

        public void setBatchDelay(Duration batchDelay) {
            this.batchDelay = batchDelay;
        }
    }

    // ------------------------------------------------------------------
    // 手工扁平键（不依赖 YAML 启动）
    // ------------------------------------------------------------------

    private static Props loadFlatProps() {
        Props props = new Props();
        Properties src = new Properties();
        src.setProperty("bean-searcher.params.pagination.start", "1");
        src.setProperty("bean-searcher.exporter.batch-delay", "PT0.5S");
        props.loadAdd(src);
        return props;
    }

    @Test
    public void flatKeys_physicalFidelity_andExactGet() {
        Props props = loadFlatProps();

        // 物理存储：仅 kebab 原文，无双写
        assertEquals(2, props.size());
        assertTrue(props.containsKey("bean-searcher.params.pagination.start"));
        assertTrue(props.containsKey("bean-searcher.exporter.batch-delay"));
        assertFalse(props.containsKey("bean-searcher.exporter.batchDelay"));
        assertFalse(props.containsKey("beanSearcher.exporter.batchDelay"));

        // exact 物理键可读
        assertEquals("1", props.get("bean-searcher.params.pagination.start"));
        assertEquals("PT0.5S", props.get("bean-searcher.exporter.batch-delay"));

        // 整键别名：前缀含 '-' 时，尾段 camel 不会命中（文档化限制）
        assertNull(props.get("bean-searcher.exporter.batchDelay"));
        // alternate 整键会把 bean-searcher 也转成 beanSearcher
        assertEquals("beanSearcher.exporter.batchDelay",
                PropNameMapper.alternate("bean-searcher.exporter.batch-delay"));

        // 正确用法：先 getProp 截前缀，再读逻辑键
        assertEquals("1", props.getProp("bean-searcher").get("params.pagination.start"));
        assertEquals("PT0.5S", props.getProp("bean-searcher").get("exporter.batchDelay"));
    }

    @Test
    public void getProp_emitsCamelLogicalSuffix_forKebabSegments() {
        Props props = loadFlatProps();
        Props sub = props.getProp("bean-searcher");

        // doFind 输出 logical 后缀：batch-delay → batchDelay
        assertEquals("1", sub.get("params.pagination.start"));
        assertEquals("PT0.5S", sub.get("exporter.batchDelay"));
        // 子视图物理键已是 camel；再以 kebab 宽松读也能命中
        assertEquals("PT0.5S", sub.get("exporter.batch-delay"));

        // 无双写膨胀
        assertEquals(2, sub.size());
        assertTrue(sub.containsKey("exporter.batchDelay"));
        assertFalse(sub.containsKey("exporter.batch-delay"));

        Map<String, String> map = props.getMap("bean-searcher");
        assertEquals(2, map.size());
        assertEquals("1", map.get("params.pagination.start"));
        assertEquals("PT0.5S", map.get("exporter.batchDelay"));
    }

    @Test
    public void nestedGetProp_paginationAndExporter() {
        Props props = loadFlatProps();

        Props pagination = props.getProp("bean-searcher.params.pagination");
        assertEquals("1", pagination.get("start"));
        assertEquals(1, pagination.getInt("start", -1));

        Props exporter = props.getProp("bean-searcher.exporter");
        // 截前缀后，后缀 batch-delay → batchDelay
        assertEquals("PT0.5S", exporter.get("batchDelay"));
        assertEquals("PT0.5S", exporter.get("batch-delay"));
        assertTrue(exporter.containsKey("batchDelay"));
        assertFalse(exporter.containsKey("batch-delay"));
    }

    @Test
    public void duration_convertUtil_andGetOrDefault() {
        Props props = loadFlatProps();

        // 经 getProp 拿到 logical 键后再转 Duration
        String delay = props.getProp("bean-searcher.exporter").get("batchDelay");
        assertEquals("PT0.5S", delay);
        assertEquals(Duration.ofMillis(500), ConvertUtil.durationOf(delay));

        Duration d2 = props.getOrDefault(
                "bean-searcher.exporter.batch-delay",
                Duration.ZERO,
                ConvertUtil::durationOf);
        assertEquals(Duration.ofMillis(500), d2);

        // 简写形式也支持（ConvertUtil 补 PT 前缀）
        assertEquals(Duration.ofMillis(500), ConvertUtil.durationOf("0.5S"));
        assertEquals(Duration.ofSeconds(30), ConvertUtil.durationOf("30s"));
    }

    @Test
    public void toBean_nestedKebabAndDuration() {
        Props props = loadFlatProps();

        BeanSearcherProps cfg = props.toBean("bean-searcher", BeanSearcherProps.class);
        assertNotNull(cfg);
        assertNotNull(cfg.getParams());
        assertNotNull(cfg.getParams().getPagination());
        assertEquals(1, cfg.getParams().getPagination().getStart());

        assertNotNull(cfg.getExporter());
        assertEquals(Duration.ofMillis(500), cfg.getExporter().getBatchDelay());
    }

    @Test
    public void toBean_fromSubProp_bindTo() {
        Props props = loadFlatProps();
        Props sub = props.getProp("bean-searcher");

        BeanSearcherProps cfg = sub.toBean(BeanSearcherProps.class);
        assertEquals(1, cfg.getParams().getPagination().getStart());
        assertEquals(Duration.ofMillis(500), cfg.getExporter().getBatchDelay());

        BeanSearcherProps cfg2 = new BeanSearcherProps();
        sub.bindTo(cfg2);
        assertEquals(1, cfg2.getParams().getPagination().getStart());
        assertEquals(Duration.ofMillis(500), cfg2.getExporter().getBatchDelay());
    }

    @Test
    public void mapper_batchDelay_roundTrip() {
        assertEquals("batchDelay", PropNameMapper.logicalKey("batch-delay"));
        assertEquals("batch-delay", PropNameMapper.alternate("batchDelay"));
        assertEquals("batchDelay", PropNameMapper.alternate("batch-delay"));
        assertEquals("batch-delay", PropNameMapper.camelToKebab("batchDelay"));
    }

    // ------------------------------------------------------------------
    // YAML 加载全链路
    // ------------------------------------------------------------------

    @Test
    public void yamlLoad_thenToBean() throws Throwable {
        SimpleSolonApp app = new SimpleSolonApp(BeanSearcherPropsTest.class,
                "-cfg=app-bean-searcher.yml",
                "-testing=1");
        app.start(x -> x.enableScanning(false));

        Props cfgRoot = app.cfg();

        // YAML 扁平化后的物理键（exact）
        assertEquals("1", cfgRoot.get("bean-searcher.params.pagination.start"));
        assertEquals("PT0.5S", cfgRoot.get("bean-searcher.exporter.batch-delay"));

        // 生产路径：toBean 前缀截取 + 嵌套绑定
        BeanSearcherProps cfg = cfgRoot.toBean("bean-searcher", BeanSearcherProps.class);
        assertNotNull(cfg);
        assertEquals(1, cfg.getParams().getPagination().getStart());
        assertEquals(Duration.ofMillis(500), cfg.getExporter().getBatchDelay());

        // 子视图 logical 键
        Props exporter = cfgRoot.getProp("bean-searcher.exporter");
        assertEquals("PT0.5S", exporter.get("batchDelay"));
        assertEquals(Duration.ofMillis(500),
                ConvertUtil.durationOf(exporter.get("batchDelay")));
    }
}
