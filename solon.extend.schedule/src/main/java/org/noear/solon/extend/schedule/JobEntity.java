package org.noear.solon.extend.schedule;

public class JobEntity {
    private String name;
    private IJob job;

    public JobEntity(String name, IJob job) {
        this.name = name;
        this.job = job;

        if (name == null) {
            this.name = job.getClass().getSimpleName();
        }
    }

    public String getName() {
        return name;
    }

    public IJob getJob() {
        return job;
    }
}
