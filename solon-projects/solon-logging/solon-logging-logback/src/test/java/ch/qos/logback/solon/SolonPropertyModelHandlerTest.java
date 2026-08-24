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
package ch.qos.logback.solon;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.status.Status;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SolonPropertyModelHandler 场景测试：
 * name/source 缺失报错、取值（配置命中 / 默认值 / 无点号 source / 回退重查）
 *
 * @since 3.9.2
 */
public class SolonPropertyModelHandlerTest {

    static final String KEY_SET = "solon.test.prop.a.b.c";
    static final String KEY_UNSET = "solon.test.prop.never.set.key";

    LoggerContext loggerContext;
    SolonPropertyModelHandler handler;

    @BeforeAll
    public static void bootSolon() {
        if (Solon.app() == null) {
            Solon.start(SolonPropertyModelHandlerTest.class, new String[]{});
        }
    }

    @AfterAll
    public static void cleanProps() {
        // Props 无卸载能力；KEY_SET 留存不影响（后续用例改用独立 KEY）
    }

    @BeforeEach
    public void setUp() {
        loggerContext = new LoggerContext();
        handler = new SolonPropertyModelHandler(loggerContext);
    }

    private ModelInterpretationContext intercon() {
        return new ModelInterpretationContext(loggerContext);
    }

    private SolonPropertyModel model(String name, String source, String defaultValue) {
        SolonPropertyModel m = new SolonPropertyModel();
        m.setName(name);
        m.setSource(source);
        m.setDefaultValue(defaultValue);
        return m;
    }

    private List<String> errors() {
        return loggerContext.getStatusManager().getCopyOfStatusList().stream()
                .filter(s -> s.getLevel() == Status.ERROR)
                .map(Status::getMessage)
                .collect(Collectors.toList());
    }

    @Test
    public void test_value_from_solon_config() throws Exception {
        Solon.cfg().put(KEY_SET, "value-from-cfg");

        ModelInterpretationContext ic = intercon();
        handler.handle(ic, model("MY_PROP", KEY_SET, "fallback-default"));

        assertEquals("value-from-cfg", ic.getProperty("MY_PROP"));
        assertTrue(errors().isEmpty());
    }

    @Test
    public void test_default_value_when_source_missing() throws Exception {
        ModelInterpretationContext ic = intercon();
        handler.handle(ic, model("MY_PROP", KEY_UNSET, "fallback-default"));

        // 配置缺失 → 走 lastDot 分支重查（结果仍为 null）→ 默认值
        assertEquals("fallback-default", ic.getProperty("MY_PROP"));
    }

    @Test
    public void test_source_without_dot_returns_default_directly() throws Exception {
        ModelInterpretationContext ic = intercon();
        handler.handle(ic, model("MY_PROP", "solontestnodot", "plain-default"));

        // lastDot <= 0 分支：直接返回默认值
        assertEquals("plain-default", ic.getProperty("MY_PROP"));
    }

    @Test
    public void test_missing_name_reports_error() throws Exception {
        ModelInterpretationContext ic = intercon();
        handler.handle(ic, model(null, KEY_UNSET, "v"));

        assertTrue(errors().stream().anyMatch(m -> m.contains("must be set")));
    }

    @Test
    public void test_missing_source_reports_error() throws Exception {
        ModelInterpretationContext ic = intercon();
        // 现状行为：source 为 null 时 getValue 内 getProperty(null) 直接 NPE（记录现状）
        assertThrows(NullPointerException.class,
                () -> handler.handle(ic, model("MY_PROP", null, "v")));
    }

    @Test
    public void test_scope_local_vs_context() throws Exception {
        Solon.cfg().put("solon.test.prop.scoped", "scoped-value");

        ModelInterpretationContext ic = intercon();
        SolonPropertyModel m = model("MY_PROP", "solon.test.prop.scoped", "dft");
        m.setScope("context");
        handler.handle(ic, m);

        // context 作用域写入 logback Context 属性
        assertEquals("scoped-value", loggerContext.getProperty("MY_PROP"));
    }
}
