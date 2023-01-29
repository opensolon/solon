package org.noear.solon.cloud.extend.powerjob.impl;


import lombok.Data;
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudProps;
import tech.powerjob.common.RemoteConstant;
import tech.powerjob.common.enums.Protocol;
import tech.powerjob.common.utils.NetUtils;
import tech.powerjob.worker.common.PowerJobWorkerConfig;
import tech.powerjob.worker.common.constants.StoreStrategy;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.WorkflowContext;

import java.util.Arrays;

/**
 * Powerjob worker properties
 *
 * @author fzdwx
 * @since 2.0
 */
@Data
public class PowerJobProperties {
    /**
     * Akka port of Powerjob-worker, optional value. Default value of this property is 27777.
     * If multiple PowerJob-worker nodes were deployed, different, unique ports should be assigned.
     */
    private Integer port = RemoteConstant.DEFAULT_WORKER_PORT;

    /**
     * Protocol for communication between WORKER and server
     */
    private Protocol protocol = Protocol.AKKA;

    /**
     * Local store strategy for H2 database. {@code disk} or {@code memory}.
     */
    private StoreStrategy storeStrategy = StoreStrategy.DISK;

    /**
     * Max length of response result. Result that is longer than the value will be truncated.
     * {@link ProcessResult} max length for #msg
     */
    private int maxResultLength = 8192;

    /**
     * If test mode is set as true, Powerjob-worker no longer connects to the server or validates appName.
     * Test mode is used for conditions that your have no powerjob-server in your develop env, so you can't start up the application
     */
    private boolean enableTestMode = false;

    /**
     * Max length of appended workflow context value length. Appended workflow context value that is longer than the value will be ignored.
     * {@link WorkflowContext} max length for #appendedContextData
     */
    private int maxAppendedWfContextLength = 8192;

    private String tag;

    /**
     * Max numbers of LightTaskTacker
     */
    private Integer maxLightweightTaskNum = 1024;

    /**
     * Max numbers of HeavyTaskTacker
     */
    private Integer maxHeavyweightTaskNum = 64;

    /**
     * Interval(s) of worker health report
     */
    private Integer healthReportInterval = 10;

    public PowerJobWorkerConfig toConfig(CloudProps cloudProps) {
        /*
         * Create OhMyConfig object for setting properties.
         */
        PowerJobWorkerConfig config = new PowerJobWorkerConfig();
        /*
         * Configuration of worker port. Random port is enabled when port is set with non-positive number.
         */
        if (this.getPort() != null) {
            config.setPort(this.getPort());
        } else {
            config.setPort(NetUtils.getRandomPort());
        }
        /*
         * appName, name of the application. Applications should be registered in advance to prevent
         * error. This property should be the same with what you entered for appName when getting
         * registered.
         */
        config.setAppName(Solon.cfg().appName());
        config.setServerAddress(Arrays.asList(cloudProps.getJobServer().split(",")));
        config.setProtocol(this.getProtocol());
        /*
         * For non-Map/MapReduce tasks, {@code memory} is recommended for speeding up calculation.
         * Map/MapReduce tasks may produce batches of subtasks, which could lead to OutOfMemory
         * exception or error, {@code disk} should be applied.
         */
        config.setStoreStrategy(this.getStoreStrategy());
        /*
         * When enabledTestMode is set as true, PowerJob-this no longer connects to PowerJob-server
         * or validate appName.
         */
        config.setEnableTestMode(this.isEnableTestMode());
        /*
         * Max length of appended workflow context . Appended workflow context value that is longer than the value will be ignored.
         */
        config.setMaxAppendedWfContextLength(this.getMaxAppendedWfContextLength());

        config.setTag(this.getTag());

        config.setMaxHeavyweightTaskNum(this.getMaxHeavyweightTaskNum());

        config.setMaxLightweightTaskNum(this.getMaxLightweightTaskNum());

        config.setHealthReportInterval(this.getHealthReportInterval());

        return config;
    }
}
