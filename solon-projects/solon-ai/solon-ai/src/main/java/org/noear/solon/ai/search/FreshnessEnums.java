package org.noear.solon.ai.search;

/**
 * @author noear 2025/2/14 created
 */
public enum FreshnessEnums {
    ONE_DAY("oneDay"), ONE_WEEK("oneWeek"), ONE_MONTH("oneMonth"), ONE_YEAR("oneYear"), NO_LIMIT("noLimit");

    private final String value;

    FreshnessEnums(String value) {
        this.value = value;
    }
}
