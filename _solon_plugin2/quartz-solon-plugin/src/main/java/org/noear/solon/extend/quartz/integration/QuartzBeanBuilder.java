package org.noear.solon.extend.quartz.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.quartz.AbstractJob;
import org.noear.solon.extend.quartz.JobManager;
import org.noear.solon.extend.quartz.Quartz;
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
        if (!(bw.raw() instanceof Job) && !(bw.raw() instanceof Runnable)) {
            throw new IllegalStateException("Quartz job only supports Runnable or Job types!");
        }

        String cronx = anno.cron7x();
        String nameOfAnno = anno.name();
        boolean enable = anno.enable();

        if (Utils.isNotEmpty(nameOfAnno)) {
            Properties prop = Solon.cfg().getProp("solon.quartz." + nameOfAnno);

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

        AbstractJob job = new BeanJob(bw.raw());
        String name = Utils.annoAlias(nameOfAnno, job.getJobId());

        JobManager.addJob(name, cronx, enable, job);
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
        String nameOfAnno = anno.name();
        boolean enable = anno.enable();

        if (Utils.isNotEmpty(nameOfAnno)) {
            Properties prop = Solon.cfg().getProp("solon.quartz." + nameOfAnno);

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

        AbstractJob job = new MethodJob(bw.raw(), method);
        String name = Utils.annoAlias(nameOfAnno, job.getJobId());

        JobManager.addJob(name, cronx, enable, job);
    }
}
