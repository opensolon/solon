package org.noear.solon.extend.schedule;


import java.util.Date;

/**
 * 任务运行工具
 * */
public class JobRunner implements IJobRunner {
    public static final JobRunner g = new JobRunner();

    private JobRunner(){

    }

    public  void run(JobEntity task) {
        System.out.print("run::" + task.getName() + "\r\n");

        new Thread(() -> {
            doRun(task);
        }).start();
    }

    private  void doRun(JobEntity task){
        while (true) {
            try {


                Date time_start = new Date();
                System.out.print(task.getName() + "::time_start::" + time_start.toString() + "\r\n");

                task.getJob().exec();

                Date time_end = new Date();
                System.out.print(task.getName() + "::time_end::" + time_end.toString() + "\r\n");

                if(task.getJob().getInterval() == 0){
                    return;
                }

                if (time_end.getTime() - time_start.getTime() < task.getJob().getInterval()) {
                    Thread.sleep(task.getJob().getInterval());//0.5s
                }

            } catch (Throwable ex) {
                ex.printStackTrace();

                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }
}
