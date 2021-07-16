package org.noear.solon.extend.schedule;


/**
 * 任务实体
 *
 * @author noear
 * @since 1.0
 * */
public class JobEntity {
    private String name;
    private IJob job;

    public JobEntity(String name, IJob job) {
        this(name, job, 0);
    }

    public JobEntity(String name, IJob job, int index) {
        this.job = job;
        this.name = job.getName();

        if (this.name == null) {
            this.name = name;
        }

        if (this.name == null) {
            this.name = job.getClass().getSimpleName();
        }

        if (index > 0) {
            this.name = this.name + index;
        }
    }

    public String getName() {
        return name;
    }

    public IJob getJob() {
        return job;
    }
}