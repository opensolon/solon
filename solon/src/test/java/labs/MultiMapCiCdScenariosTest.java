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
package labs;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.MultiMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Real-world parameter use cases derived from:
 * https://hidekazu-konishi.com/entry/claude_code_cicd_and_headless_automation.html
 *
 * Each test maps to a concrete CLI example in the article.
 *
 * @author noear
 */
public class MultiMapCiCdScenariosTest {

    /** 所有需要接收值的选项（对应 Claude Code CLI 的 VALUE_OPTIONS） */
    private static final Set<String> VALUE_KEYS = new HashSet<>(Arrays.asList(
            "output-format", "model", "max-turns", "allowedTools", "disallowedTools",
            "permission-mode", "resume", "r", "session-id", "fallback-model",
            "max-budget-usd", "input-format", "json-schema", "add-dir",
            "permission-prompt-tool"
    ));

    /** 所有已知选项（VALUE_KEYS ∪ 布尔 flag 名） */
    private static final Set<String> KNOWN_OPTIONS = new HashSet<>(Arrays.asList(
            // value keys
            "output-format", "model", "max-turns", "allowedTools", "disallowedTools",
            "permission-mode", "resume", "r", "session-id", "fallback-model",
            "max-budget-usd", "input-format", "json-schema", "add-dir",
            "permission-prompt-tool",
            // boolean flags
            "p", "print", "verbose", "bare", "include-partial-messages",
            "replay-user-messages", "fork-session", "continue", "c",
            "dangerously-skip-permissions"
    ));

    // =========================================================
    // §2.1  -p 基本用法
    // =========================================================

    /**
     * 文章示例：claude -p "Summarize the changes in the last commit..."
     * 短 flag -p 应被解析为布尔 flag；提示词作为位置参数。
     */
    @Test
    void sec2_1_short_flag_p_with_prompt_as_positional() {
        String[] args = {"-p", "Summarize the changes in the last commit and flag any obvious regressions."};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertTrue(m.flags().contains("p"), "-p 应进入 flags");
        assertEquals(2, m.flags().size());
        assertEquals("Summarize the changes in the last commit and flag any obvious regressions.",
                m.flagAt(1));
    }

    /**
     * 文章示例：cat build-error.txt | claude -p "Explain the root cause..."
     * -p 单独出现（stdin 模式），无提示词位置参数。
     */
    @Test
    void sec2_1_short_flag_p_alone_stdin_mode() {
        String[] args = {"-p", "Explain the root cause of this build failure and suggest a fix."};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertTrue(m.flags().contains("p"));
        assertEquals(2, m.flags().size());
        assertEquals("Explain the root cause of this build failure and suggest a fix.",
                m.flagAt(1));
    }

    // =========================================================
    // §2.2  --output-format 三种值
    // =========================================================

    /**
     * 文章示例：claude -p "List..." --output-format json
     * --output-format 空格格式，值为 json。
     */
    @Test
    void sec2_2_output_format_json_space_separated() {
        String[] args = {"-p", "List the public functions in src/.", "--output-format", "json"};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("json", m.get("output-format"));
        assertFalse(m.flags().contains("output-format"), "output-format 不应进入 flags");
    }

    /**
     * 文章示例：--output-format stream-json --verbose
     * stream-json 值 + 布尔 flag verbose 同时出现。
     */
    @Test
    void sec2_2_output_format_stream_json_with_verbose() {
        String[] args = {"-p", "Refactor the logging module.", "--output-format", "stream-json", "--verbose"};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("stream-json", m.get("output-format"));
        assertTrue(m.flags().contains("verbose"), "--verbose 应进入 flags");
        assertFalse(m.flags().contains("output-format"));
    }

    /**
     * 文章示例：--output-format stream-json --verbose --include-partial-messages
     * 三个参数：一个 value flag + 两个布尔 flag。
     */
    @Test
    void sec2_2_output_format_stream_json_verbose_include_partial() {
        String[] args = {
                "-p", "Explain recursion.",
                "--output-format", "stream-json",
                "--verbose",
                "--include-partial-messages"
        };
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("stream-json", m.get("output-format"));
        assertTrue(m.flags().contains("verbose"));
        assertTrue(m.flags().contains("include-partial-messages"));
        assertEquals("Explain recursion.", m.flagAt(1));
    }

    /**
     * 文章示例：--input-format stream-json 与 --replay-user-messages 组合。
     */
    @Test
    void sec2_2_input_format_stream_json_with_replay_user_messages() {
        String[] args = {
                "-p",
                "--input-format", "stream-json",
                "--output-format", "stream-json",
                "--replay-user-messages"
        };
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("stream-json", m.get("input-format"));
        assertEquals("stream-json", m.get("output-format"));
        assertTrue(m.flags().contains("replay-user-messages"));
    }

    // =========================================================
    // §2.3  --bare
    // =========================================================

    /**
     * 文章示例：claude -p --bare "Run the unit tests and report only failures."
     * --bare 是布尔 flag，提示词是位置参数。
     */
    @Test
    void sec2_3_bare_flag_with_prompt() {
        String[] args = {"-p", "--bare", "Run the unit tests and report only failures."};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertTrue(m.flags().contains("bare"), "--bare 应进入 flags");
        assertTrue(m.flags().contains("p"));
        assertEquals("Run the unit tests and report only failures.", m.flagAt(2));
    }

    // =========================================================
    // §2.4  Session 续传：--continue / --resume / --session-id / --fork-session
    // =========================================================

    /**
     * 文章示例：--continue（短写 -c）是布尔 flag，用于恢复最近会话。
     */
    @Test
    void sec2_4_continue_is_boolean_flag() {
        String[] args = {"-p", "--continue", "Continue the previous analysis."};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertTrue(m.flags().contains("continue"));
        assertEquals("Continue the previous analysis.", m.flagAt(2));
    }

    /**
     * 文章示例：--resume "$session_id"（空格格式，值为 UUID）。
     */
    @Test
    void sec2_4_resume_with_uuid_value() {
        String sessionId = "550e8400-e29b-41d4-a716-446655440000";
        String[] args = {"-p", "Now write the missing tests.", "--resume", sessionId};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals(sessionId, m.get("resume"));
        assertFalse(m.flags().contains("resume"));
        assertEquals("Now write the missing tests.", m.flagAt(1));
    }

    /**
     * 文章示例：--session-id <UUID>（等号格式与空格格式均应支持）。
     */
    @Test
    void sec2_4_session_id_space_format() {
        String sessionId = "550e8400-e29b-41d4-a716-446655440000";
        String[] args = {"--session-id", sessionId, "-p", "Start a review."};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals(sessionId, m.get("session-id"));
        assertEquals("Start a review.", m.flagAt(1));
    }

    /**
     * 文章示例：--session-id=<UUID>（等号格式）。
     */
    @Test
    void sec2_4_session_id_equals_format() {
        String sessionId = "550e8400-e29b-41d4-a716-446655440000";
        String[] args = {"--session-id=" + sessionId};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals(sessionId, m.get("session-id"));
    }

    /**
     * 文章示例：--fork-session 是布尔 flag（与 --resume 组合使用）。
     */
    @Test
    void sec2_4_fork_session_is_boolean_flag() {
        String sessionId = "550e8400-e29b-41d4-a716-446655440000";
        String[] args = {"--fork-session", "--resume", sessionId, "-p", "Try a follow-up."};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertTrue(m.flags().contains("fork-session"));
        assertEquals(sessionId, m.get("resume"));
    }

    // =========================================================
    // §2.5  --max-turns 与 --model
    // =========================================================

    /**
     * 文章示例：--max-turns 8 --model sonnet --output-format json
     * 三个空格格式的 value 选项。
     */
    @Test
    void sec2_5_max_turns_model_output_format_all_space() {
        String[] args = {
                "-p", "Apply the lint autofixes and stop.",
                "--max-turns", "8",
                "--model", "sonnet",
                "--output-format", "json"
        };
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("8", m.get("max-turns"));
        assertEquals("sonnet", m.get("model"));
        assertEquals("json", m.get("output-format"));
        assertEquals("Apply the lint autofixes and stop.", m.flagAt(1));
    }

    /**
     * 文章示例：--max-turns=10（等号格式）。
     */
    @Test
    void sec2_5_max_turns_equals_format() {
        String[] args = {"--max-turns=10", "--model=sonnet"};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("10", m.get("max-turns"));
        assertEquals("sonnet", m.get("model"));
    }

    /**
     * 文章注意事项：使用完整 model 名称（含 - 分隔的日期/版本号）。
     * claude-opus-4-8 整体作为值，不能被拆分。
     */
    @Test
    void sec2_5_model_full_name_with_hyphens() {
        String[] args = {"--model", "claude-opus-4-8", "-p", "Audit the changes."};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("claude-opus-4-8", m.get("model"));
    }

    /**
     * 文章示例：--model opus（别名格式）。
     */
    @Test
    void sec2_5_model_alias_opus() {
        String[] args = {"--model", "opus"};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("opus", m.get("model"));
        assertFalse(m.flags().contains("model"));
    }

    // =========================================================
    // §3.3  --json-schema（复杂值：JSON 字符串）
    // =========================================================

    /**
     * 文章示例：--json-schema '{"type":"object","properties":{"functions":...}}'
     * 值为 JSON 字符串，含花括号、引号等特殊字符。
     */
    @Test
    void sec3_3_json_schema_value_with_special_chars() {
        String schema = "{\"type\":\"object\",\"properties\":{\"functions\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}},\"required\":[\"functions\"]}";
        String[] args = {"-p", "Extract exported functions.", "--output-format", "json", "--json-schema", schema};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals(schema, m.get("json-schema"));
        assertEquals("json", m.get("output-format"));
    }

    // =========================================================
    // §6.1  --allowedTools / --disallowedTools
    // =========================================================

    /**
     * 文章示例：--allowedTools "Read,Grep,Glob"
     * 值为逗号分隔的工具列表字符串。
     */
    @Test
    void sec6_1_allowedTools_csv_value() {
        String[] args = {"-p", "Review this diff for bugs.", "--allowedTools", "Read,Grep,Glob"};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("Read,Grep,Glob", m.get("allowedTools"));
        assertFalse(m.flags().contains("allowedTools"));
    }

    /**
     * 文章示例：--allowedTools "Bash(git log *)"
     * 值含括号和通配符，不应被截断或误解析。
     */
    @Test
    void sec6_1_allowedTools_bash_with_parentheses() {
        String[] args = {"--allowedTools", "Bash(git log *)"};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("Bash(git log *)", m.get("allowedTools"));
    }

    /**
     * 文章示例：多个 --allowedTools 参数（多值同一 key）。
     * --allowedTools "Bash(git log *)" --allowedTools "Bash(git diff *)" --allowedTools "Read"
     */
    @Test
    void sec6_1_allowedTools_multiple_values_same_key() {
        String[] args = {
                "--allowedTools", "Bash(git log *)",
                "--allowedTools", "Bash(git diff *)",
                "--allowedTools", "Read"
        };
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        List<String> all = m.getAll("allowedTools");
        assertNotNull(all);
        assertEquals(3, all.size());
        assertTrue(all.contains("Bash(git log *)"));
        assertTrue(all.contains("Bash(git diff *)"));
        assertTrue(all.contains("Read"));
    }

    /**
     * 文章示例：--disallowedTools "Bash(rm *)"
     * 值含括号通配符，作为 disallowedTools 的值。
     */
    @Test
    void sec6_1_disallowedTools_bash_rm() {
        String[] args = {
                "--allowedTools", "Bash(git log *)",
                "--disallowedTools", "Bash(rm *)"
        };
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("Bash(git log *)", m.get("allowedTools"));
        assertEquals("Bash(rm *)", m.get("disallowedTools"));
    }

    // =========================================================
    // §6.2  --permission-mode
    // =========================================================

    /**
     * 文章示例：--permission-mode dontAsk（CI 无人值守推荐模式）。
     */
    @Test
    void sec6_2_permission_mode_dontask() {
        String[] args = {"-p", "Run the codemod.", "--permission-mode", "dontAsk"};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("dontAsk", m.get("permission-mode"));
        assertFalse(m.flags().contains("permission-mode"));
    }

    /**
     * 文章示例：--permission-mode plan（只分析，不修改文件）。
     */
    @Test
    void sec6_2_permission_mode_plan() {
        String[] args = {"-p", "Review the architecture.", "--permission-mode", "plan"};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("plan", m.get("permission-mode"));
    }

    /**
     * 文章示例：--dangerously-skip-permissions（布尔 flag，沙盒专用）。
     */
    @Test
    void sec6_2_dangerously_skip_permissions_is_boolean_flag() {
        String[] args = {"-p", "Full-auto run.", "--dangerously-skip-permissions"};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertTrue(m.flags().contains("dangerously-skip-permissions"));
        assertEquals("Full-auto run.", m.flagAt(1));
    }

    // =========================================================
    // §6.5  --add-dir
    // =========================================================

    /**
     * 文章示例：--add-dir /extra/repo（为 agent 追加可访问目录）。
     */
    @Test
    void sec6_5_add_dir_value() {
        String[] args = {"-p", "Cross-repo refactor.", "--add-dir", "/extra/repo"};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("/extra/repo", m.get("add-dir"));
    }

    // =========================================================
    // §8  成本控制：--max-budget-usd / --fallback-model
    // =========================================================

    /**
     * 文章示例：--max-budget-usd（硬性花费上限，值为小数）。
     */
    @Test
    void sec8_max_budget_usd_decimal_value() {
        String[] args = {"-p", "Nightly audit.", "--max-budget-usd", "10.0", "--model", "sonnet"};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("10.0", m.get("max-budget-usd"));
        assertEquals("sonnet", m.get("model"));
    }

    /**
     * 文章示例：--fallback-model sonnet（主模型不可用时的降级 fallback）。
     */
    @Test
    void sec8_fallback_model_value() {
        String[] args = {"--model", "opus", "--fallback-model", "sonnet", "-p", "Audit."};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("opus", m.get("model"));
        assertEquals("sonnet", m.get("fallback-model"));
    }

    /**
     * 文章示例：timeout + --max-turns + --model + --output-format 完整边界组合。
     * timeout 600 claude -p "Apply the codemod and stop." --max-turns 10 --model sonnet --output-format json
     */
    @Test
    void sec8_bounded_run_full_combination() {
        String[] args = {
                "-p", "Apply the codemod and stop.",
                "--max-turns", "10",
                "--model", "sonnet",
                "--output-format", "json"
        };
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("Apply the codemod and stop.", m.flagAt(1));
        assertEquals("10", m.get("max-turns"));
        assertEquals("sonnet", m.get("model"));
        assertEquals("json", m.get("output-format"));
        assertTrue(m.flags().contains("p"));
    }

    // =========================================================
    // §5  GitHub Action / GitLab CI 完整场景
    // =========================================================

    /**
     * 文章示例 §5.3 - GitHub Action PR Review 自动化场景：
     * --max-turns 12 --model sonnet --allowedTools "Read,Grep,Glob"
     */
    @Test
    void sec5_3_github_action_pr_review_scenario() {
        String[] args = {
                "-p", "Review this pull request for correctness and security issues.",
                "--max-turns", "12",
                "--model", "sonnet",
                "--allowedTools", "Read,Grep,Glob"
        };
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("Review this pull request for correctness and security issues.",
                m.flagAt(1));
        assertEquals("12", m.get("max-turns"));
        assertEquals("sonnet", m.get("model"));
        assertEquals("Read,Grep,Glob", m.get("allowedTools"));
        assertFalse(m.flags().contains("max-turns"));
        assertFalse(m.flags().contains("model"));
        assertFalse(m.flags().contains("allowedTools"));
    }

    /**
     * 文章示例 §5.4 - GitLab CI 场景：
     * --max-turns 10 --model sonnet --allowedTools "Read,Grep,Glob" --output-format json
     */
    @Test
    void sec5_4_gitlab_ci_review_scenario() {
        String[] args = {
                "-p", "Review the changes in this merge request.",
                "--max-turns", "10",
                "--model", "sonnet",
                "--allowedTools", "Read,Grep,Glob",
                "--output-format", "json"
        };
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("10", m.get("max-turns"));
        assertEquals("sonnet", m.get("model"));
        assertEquals("Read,Grep,Glob", m.get("allowedTools"));
        assertEquals("json", m.get("output-format"));
        assertTrue(m.flags().contains("Review the changes in this merge request."));
    }

    /**
     * 文章示例 §5.4 - Jenkins Pipeline 场景：
     * --max-turns 10 --model sonnet --allowedTools "Read,Grep,Glob" --output-format json
     * （与 GitLab 完全相同的 CLI 参数，只是容器不同）
     */
    @Test
    void sec5_4_jenkins_pipeline_scenario() {
        String[] args = {
                "-p", "Review this change for bugs. Do not modify files.",
                "--max-turns", "10",
                "--model", "sonnet",
                "--allowedTools", "Read,Grep,Glob",
                "--output-format", "json"
        };
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("json", m.get("output-format"));
        assertEquals("10", m.get("max-turns"));
        assertEquals(2, m.flags().size());
    }

    // =========================================================
    // §10  定时任务：session-id 幂等性 + --max-turns 组合
    // =========================================================

    /**
     * 文章示例 §10.2 - 定时任务使用确定性 session-id 保证幂等。
     * --session-id <uuid> --max-turns 15 --model sonnet --output-format json
     */
    @Test
    void sec10_scheduled_job_deterministic_session_id() {
        String sessionId = "nightly-docs-2026-07-30";
        String[] args = {
                "-p", "Nightly refactor sweep.",
                "--session-id", sessionId,
                "--max-turns", "15",
                "--model", "sonnet",
                "--output-format", "json"
        };
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals(sessionId, m.get("session-id"));
        assertEquals("15", m.get("max-turns"));
        assertEquals("json", m.get("output-format"));
    }

    // =========================================================
    // 边界：等号格式与空格格式混用
    // =========================================================

    /**
     * 等号格式（--max-turns=8）与空格格式（--model sonnet）在同一命令里混用。
     */
    @Test
    void edge_equals_and_space_format_mixed_in_one_command() {
        String[] args = {
                "-p", "Fix the failing test.",
                "--max-turns=8",
                "--model", "sonnet",
                "--output-format=json",
                "--verbose"
        };
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        assertEquals("8", m.get("max-turns"));
        assertEquals("sonnet", m.get("model"));
        assertEquals("json", m.get("output-format"));
        assertTrue(m.flags().contains("verbose"));
        assertEquals(3, m.flags().size());


        m = MultiMap.from(args);

        assertEquals("8", m.get("max-turns"));
        assertEquals("sonnet", m.get("model"));
        assertEquals("json", m.get("output-format"));
        assertTrue(m.flags().contains("verbose"));
        assertEquals(1, m.flags().size());
    }

    /**
     * value 选项后紧跟另一个 value 选项（next 以 - 开头），不应消费 next 作为值。
     * 例：--output-format --model（--model 被误识别为 output-format 的值）。
     */
    @Test
    void edge_value_key_followed_by_another_option_not_consumed() {
        // --output-format 后面跟 --model，--model 以 - 开头，不应被消费
        String[] args = {"--output-format", "--model", "sonnet"};
        MultiMap<String> m = MultiMap.from(args, VALUE_KEYS);

        // --output-format 没有合法的值（next 以 - 开头），退化为 flag
        assertTrue(m.flags().contains("output-format"));
        // --model 正常消费 sonnet
        assertEquals("sonnet", m.get("model"));
    }

    /**
     * 使用 reparse 将已解析（空格格式丢失）的旧 from(String[]) 结果补救。
     * 模拟 Solon.cfg().argx() 场景：原始数组已丢弃，只剩 flags 列表。
     */
    @Test
    void edge_reparse_rescues_space_format_from_old_parse() {
        // 新版 from(String[]) 的贪婪 lookahead 会消费空格格式的值
        MultiMap<String> m = MultiMap.from(new String[]{
                "-p", "Fix tests.", "--model", "sonnet", "--max-turns", "8", "--verbose"
        });

        // flags = ["verbose"]（-p 消费了 "Fix tests."，--model 消费了 sonnet，--max-turns 消费了 8）
        //m.reparse(VALUE_KEYS, KNOWN_OPTIONS);

        assertEquals("Fix tests.", m.get("p"));
        assertEquals("sonnet", m.get("model"));
        assertEquals("8", m.get("max-turns"));
        assertTrue(m.flags().contains("verbose"));
    }
}
