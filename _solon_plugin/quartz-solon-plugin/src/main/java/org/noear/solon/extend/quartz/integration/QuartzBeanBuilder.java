package org.noear.solon.extend.quartz.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.quartz.JobManager;
import org.noear.solon.extend.quartz.Quartz;
import org.noear.solon.extend.quartz.wrap.BeanJob;
import org.noear.solon.extend.quartz.wrap.MethodJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author noear
 * @since 1.11
 */
public class QuartzBeanBuilder implements BeanBuilder<Quartz>, BeanExtractor<Quartz> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, Quartz anno) throws Throwable {
        if (bw.raw() instanceof Job || bw.raw() instanceof Runnable) {
            String cronx = anno.cron7x();
            String name = anno.name();
            boolean enable = anno.enable();

            if (Utils.isNotEmpty(name)) {
                Properties prop = Solon.cfg().getProp("solon.quartz." + name);

                if (prop.size() > 0) {
                    String cronxTmp = prop.getProperty("cron7x");
                    String enableTmp = prop.getProperty("enable");

                    if ("false".equals(enableTmp)) {
                        enable = false;
                    }

                    if (Utils.isNotEmpty(cronxTmp)) {
                        cronx = cronxTmp;
                    }
                }
            }

            JobManager.addJob(name, cronx, enable, new BeanJob(bw.raw()));
        }
    }

    @Override
    public void doExtract(BeanWrap bw, Method method, Quartz anno) throws Throwable {
        if (method.getParameterCount() > 1) {
            throw new IllegalStateException("Quartz job supports only one JobExecutionContext parameter!");
        }

        if (method.getParameterCount() == 1) {
            Class<?> tmp = method.getParameterTypes()[0];
            if (tmp != JobExecutionContext.class) {
                throw new IllegalStateException("Quartz job supports only one JobExecutionContext parameter!");
            }
        }

        String cronx = anno.cron7x();
        String name = anno.name();
        boolean enable = anno.enable();

        if (Utils.isNotEmpty(name)) {
            Properties prop = Solon.cfg().getProp("solon.quartz." + name);

            if (prop.size() > 0) {
                String cronxTmp = prop.getProperty("cron7x");
                String enableTmp = prop.getProperty("enable");

                if ("false".equals(enableTmp)) {
                    enable = false;
                }

                if (Utils.isNotEmpty(cronxTmp)) {
                    cronx = cronxTmp;
                }
            }
        }

        JobManager.addJob(name, cronx, enable, new MethodJob(bw.raw(), method));
    }
}
