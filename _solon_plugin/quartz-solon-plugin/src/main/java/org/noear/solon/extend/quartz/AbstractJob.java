package org.noear.solon.extend.quartz;

import org.quartz.Job;

/**
 * @author noear
 * @since 1.11
 */
public abstract class AbstractJob implements Job {
    public abstract String getJobId();
}