package org.noear.solon.cloud.extend.health;

import java.util.ArrayList;
import java.util.List;

/**
 * @author iYarnFog
 * @since 1.5
 * @date 2021/10/01 19:37
 */
public class HealthStatus {

    public enum Code {
        // 运行中
        UP,
        // 离线
        DOWN,
        // 异常
        ERROR;
    }

    public static class CheckResult {
        private String name;
        private Code status;
        private Object data;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Code getStatus() {
            return this.status;
        }

        public void setStatus(Code status) {
            this.status = status;
        }

        public Object getData() {
            return this.data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    private List<CheckResult> checks = new ArrayList<>();
    private Code outcome;

    public List<CheckResult> getChecks() {
        return checks;
    }

    public void setChecks(List<CheckResult> checks) {
        this.checks = checks;
    }

    public Code getOutcome() {
        return this.outcome;
    }

    public void setOutcome(Code outcome) {
        this.outcome = outcome;
    }
}
