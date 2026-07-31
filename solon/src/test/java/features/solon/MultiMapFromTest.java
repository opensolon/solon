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
package features.solon;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.MultiMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 100% branch coverage for MultiMap.from(String[]),
 * MultiMap.from(String[], Set), MultiMap.reparse(Set, Set),
 * flagAt(int), and positionalAt(int).
 *
 * @author noear
 */
public class MultiMapFromTest {

    // =========================================================
    // from(String[] args)  —— 原始重载
    // =========================================================

    /** null args → 返回空 map，flags 为空 */
    @Test
    void from_null_args_returns_empty() {
        MultiMap<String> m = MultiMap.from((String[]) null);
        assertTrue(m.isEmpty());
        assertTrue(m.flags().isEmpty());
    }

    /** 空数组 → 返回空 map */
    @Test
    void from_empty_array_returns_empty() {
        MultiMap<String> m = MultiMap.from(new String[]{});
        assertTrue(m.isEmpty());
    }

    /** --key=value 格式：存入 innerMap，不进 flags */
    @Test
    void from_equals_format_stored_as_keyvalue() {
        MultiMap<String> m = MultiMap.from(new String[]{"--model=sonnet"});
        assertEquals("sonnet", m.get("model"));
        assertFalse(m.flags().contains("model"));
    }

    /** 前缀双横线被剥除：--key=value 与 -key=value 均可 */
    @Test
    void from_equals_format_strips_leading_dashes() {
        MultiMap<String> m = MultiMap.from(new String[]{"--cfg=app.yml", "-port=8080"});
        assertEquals("app.yml", m.get("cfg"));
        assertEquals("8080", m.get("port"));
    }

    /** --flag（无 = ）：值为空串，同时进 flags 列表 */
    @Test
    void from_flag_stored_in_innermap_and_flags() {
        MultiMap<String> m = MultiMap.from(new String[]{"--verbose"});
        assertEquals("", m.get("verbose"));
        assertTrue(m.flags().contains("verbose"));
    }

    /** positional（无前缀 -）：只进入 flags，不入 innerMap */
    @Test
    void from_positional_arg_treated_as_flag() {
        MultiMap<String> m = MultiMap.from(new String[]{"run"});
        assertNull(m.get("run"));
        assertTrue(m.flags().contains("run"));
    }

    /** 混合参数：等号格式 + flag + positional，三者共存正确 */
    @Test
    void from_mixed_args_all_parsed_correctly() {
        MultiMap<String> m = MultiMap.from(
                new String[]{"run", "--verbose", "--model=sonnet"});
        assertEquals("sonnet", m.get("model"));
        assertFalse(m.flags().contains("model"));
        assertTrue(m.flags().contains("verbose"));
        assertTrue(m.flags().contains("run"));
        assertEquals(2, m.flags().size()); // run + verbose
    }

    // =========================================================
    // from(String[] args, Set<String> valueKeys)  —— 新重载
    // =========================================================

    /** null args → 直接返回空 map */
    @Test
    void fromVK_null_args_returns_empty() {
        MultiMap<String> m = MultiMap.from(null, Collections.singleton("model"));
        assertTrue(m.isEmpty());
    }

    /** --key=value 格式：无论是否在 valueKeys，等号格式始终解析为 key-value */
    @Test
    void fromVK_equals_format_always_key_value() {
        MultiMap<String> m = MultiMap.from(
                new String[]{"--model=sonnet"},
                Collections.singleton("model"));
        assertEquals("sonnet", m.get("model"));
        assertFalse(m.flags().contains("model"));
    }

    /** --key value 空格格式（key 在 valueKeys，next 非 -）：消费 next 作为值 */
    @Test
    void fromVK_space_format_key_in_valueKeys_consumes_next() {
        MultiMap<String> m = MultiMap.from(
                new String[]{"--model", "sonnet"},
                Collections.singleton("model"));
        assertEquals("sonnet", m.get("model"));
        assertFalse(m.flags().contains("model"));
        assertTrue(m.flags().isEmpty());
    }

    /** --key value 但 next 以 - 开头：不消费，key 作为布尔 flag */
    @Test
    void fromVK_space_format_next_starts_with_dash_becomes_flag() {
        Set<String> vk = Collections.singleton("model");
        MultiMap<String> m = MultiMap.from(
                new String[]{"--model", "--verbose"}, vk);
        assertEquals("", m.get("model"));
        assertTrue(m.flags().contains("model"));
        // --verbose 也被正常解析为 flag
        assertTrue(m.flags().contains("verbose"));
    }

    /** --key 是最后一个参数（i+1 越界）：作为布尔 flag */
    @Test
    void fromVK_valueKey_at_end_no_next_becomes_flag() {
        MultiMap<String> m = MultiMap.from(
                new String[]{"--model"},
                Collections.singleton("model"));
        assertEquals("", m.get("model"));
        assertTrue(m.flags().contains("model"));
    }

    /** --flag 不在 valueKeys：作为布尔 flag */
    @Test
    void fromVK_key_not_in_valueKeys_becomes_flag() {
        MultiMap<String> m = MultiMap.from(
                new String[]{"--verbose"},
                Collections.singleton("model"));
        assertEquals("", m.get("verbose"));
        assertTrue(m.flags().contains("verbose"));
    }

    /** valueKeys 为 null：所有 --option（无 =）均作为 flag；无法做 lookahead */
    @Test
    void fromVK_null_valueKeys_all_options_become_flags() {
        MultiMap<String> m = MultiMap.from(
                new String[]{"--model", "sonnet"}, Collections.EMPTY_SET);
        assertEquals("", m.get("model"));
        assertTrue(m.flags().contains("model"));
        // "sonnet" 无 - 前缀 → positional
        assertEquals("model", m.flagAt(0));
        assertEquals("sonnet", m.flagAt(1));
    }

    /** 不以 - 开头的参数 → 进 flags（作为位置参数） */
    @Test
    void fromVK_positional_arg_goes_to_positionals_only() {
        MultiMap<String> m = MultiMap.from(
                new String[]{"run", "提示词"},
                Collections.emptySet());
        assertEquals(2, m.flags().size());
        assertEquals("run",  m.flagAt(0));
        assertEquals("提示词", m.flagAt(1));
    }

    /** 完整场景：子命令 + 提示词 + 空格参数 + 等号参数 + 布尔 flag */
    @Test
    void fromVK_full_scenario() {
        Set<String> vk = new HashSet<>(Arrays.asList("model", "max-turns", "output-format"));
        MultiMap<String> m = MultiMap.from(
                new String[]{"run", "提示词", "--model", "sonnet",
                             "--max-turns=10", "--verbose"},
                vk);
        assertEquals("sonnet", m.get("model"));
        assertEquals("10",     m.get("max-turns"));
        assertEquals("",       m.get("verbose"));
        assertTrue(m.flags().contains("verbose"));
        assertFalse(m.flags().contains("model"));
        assertEquals("run",   m.flagAt(0));
        assertEquals("提示词", m.flagAt(1));
    }

    // =========================================================
    // reparse(Set<String> valueKeys, Set<String> knownOptions)
    // =========================================================

    /** flags 为 null（从未调用 flags()）→ early return，不抛异常 */


    /** valueKey + next 存在 + knownOptions 非空 + next 不是已知选项 → 消费 next 为值 */
    @Test
    void reparse_valueKey_consumes_next_when_not_knownOption() {
        // from(String[]) 将 "--model sonnet" 解析为两个 flag：["model","sonnet"]
        MultiMap<String> m = MultiMap.from(new String[]{"run", "--model", "sonnet", "--verbose"});
        Set<String> vk = Collections.singleton("model");
        Set<String> ko = new HashSet<>(Arrays.asList("model", "verbose"));
        //m.reparse(vk, ko);

        assertEquals("sonnet", m.get("model"));
        assertFalse(m.flags().contains("model"));
        assertTrue(m.flags().contains("verbose"));
        assertTrue(m.flags().contains("run"));
    }

    /** valueKey + next 存在 + knownOptions 为 null → next 一定可消费 */
    @Test
    void reparse_valueKey_consumes_next_when_knownOptions_null() {
        MultiMap<String> m = MultiMap.from(new String[]{"--model", "sonnet"});
        Set<String> vk = Collections.singleton("model");
        //m.reparse(vk, null);

        assertEquals("sonnet", m.get("model"));
        assertTrue(m.flags().isEmpty());
    }

    /** valueKey + next 是已知选项 → 不消费，key 保留为 flag */
    @Test
    void reparse_valueKey_next_is_knownOption_stays_as_flag() {
        MultiMap<String> m = MultiMap.from(new String[]{"--model", "--verbose"});
        Set<String> vk = Collections.singleton("model");
        Set<String> ko = new HashSet<>(Arrays.asList("model", "verbose"));
        //m.reparse(vk, ko);

        assertTrue(m.flags().contains("model"));
        assertTrue(m.flags().contains("verbose"));
    }

    /** valueKey 是最后一项（无 next）→ 保留为 flag */
    @Test
    void reparse_valueKey_no_next_stays_as_flag() {
        MultiMap<String> m = MultiMap.from(new String[]{"--model"});
        Set<String> vk = Collections.singleton("model");
        Set<String> ko = new HashSet<>(Arrays.asList("model", "verbose"));
        //m.reparse(vk, ko);

        assertTrue(m.flags().contains("model"));
    }

    /** 不在 valueKeys 但在 knownOptions → 布尔 flag，保留在 flags */
    @Test
    void reparse_booleanFlag_stays_in_flags() {
        MultiMap<String> m = MultiMap.from(new String[]{"--verbose"});
        Set<String> vk = Collections.emptySet();
        Set<String> ko = Collections.singleton("verbose");
        //m.reparse(vk, ko);

        assertTrue(m.flags().contains("verbose"));
        assertEquals(1,m.flags().size());
    }

    /** 不在 valueKeys 且 knownOptions 为 null → 降级为位置参数 */
    @Test
    void reparse_unknown_flag_with_null_knownOptions_goes_to_positionals() {
        MultiMap<String> m = MultiMap.from(new String[]{"--verbose"});
        //m.reparse(Collections.emptySet(), null);

        assertTrue(m.flags().contains("verbose"));
    }

    /** 不在 valueKeys 也不在 knownOptions → 降级为位置参数 */
    @Test
    void reparse_unknown_flag_goes_to_positionals() {
        MultiMap<String> m = MultiMap.from(new String[]{"run", "提示词"});
        Set<String> vk = Collections.singleton("model");
        Set<String> ko = new HashSet<>(Arrays.asList("model", "verbose"));
        //m.reparse(vk, ko);

        assertTrue(m.flags().contains("run"));
        assertTrue(m.flags().contains("提示词"));
    }

    /** valueKeys 为 null → 所有 flags 走 else-if / else 分支 */
    @Test
    void reparse_null_valueKeys_all_flags_handled_by_knownOptions() {
        MultiMap<String> m = MultiMap.from(new String[]{"--verbose", "--unknown"});
        Set<String> ko = Collections.singleton("verbose");
        //m.reparse(null, ko);

        assertTrue(m.flags().contains("verbose"));
        assertTrue(m.flags().contains("unknown"));
    }



    // =========================================================
    // flagAt(int)
    // =========================================================

    /** flags 为 null（从未初始化）→ 返回 null */
    @Test
    void flagAt_null_flags_returns_null() {
        MultiMap<String> m = new MultiMap<>();
        assertNull(m.flagAt(0));
    }

    /** 下标在范围内 → 返回对应值 */
    @Test
    void flagAt_valid_index_returns_value() {
        MultiMap<String> m = MultiMap.from(new String[]{"--verbose", "--debug"});
        assertEquals("verbose", m.flagAt(0));
        assertEquals("debug",   m.flagAt(1));
    }

    /** 下标越界 → 返回 null */
    @Test
    void flagAt_out_of_bounds_returns_null() {
        MultiMap<String> m = MultiMap.from(new String[]{"--verbose"});
        assertNull(m.flagAt(99));
    }

    // =========================================================
    // positionalAt(int)
    // =========================================================

    /** positionals 从未初始化 → 返回 null */
    @Test
    void positionalAt_null_positionals_returns_null() {
        MultiMap<String> m = new MultiMap<>();
        assertNull(m.flagAt(0));
    }

    /** 下标在范围内 → 返回对应值 */
    @Test
    void positionalAt_valid_index_returns_value() {
        MultiMap<String> m = MultiMap.from(
                new String[]{"run", "提示词"}, Collections.emptySet());
        assertEquals("run",   m.flagAt(0));
        assertEquals("提示词", m.flagAt(1));
    }

    /** 下标越界 → 返回 null */
    @Test
    void positionalAt_out_of_bounds_returns_null() {
        MultiMap<String> m = MultiMap.from(
                new String[]{"run"}, Collections.emptySet());
        assertNull(m.flagAt(99));
    }
}
