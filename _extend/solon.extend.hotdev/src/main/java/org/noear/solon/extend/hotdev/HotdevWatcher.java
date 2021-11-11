package org.noear.solon.extend.hotdev;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 热开发 - 监视器
 *
 * @author 夜の孤城
 * @since 1.5
 * */
public class HotdevWatcher extends TimerTask {
    File dir;
    private Timer timer;
    private Runnable reloader;
    private long last_time;
    private String classPath;
    private String classPathDbg;

    public HotdevWatcher(File base, Runnable reloader) {
        this.dir = base;
        timer = new Timer();
        this.reloader = reloader;
        last_time = System.currentTimeMillis();
        //获取当前的classes path
        String cp = HotdevWatcher.class.getClassLoader().getResource("./").getPath();

        if (cp.endsWith("/target/classes/")) {
            classPath = cp;
            classPathDbg = cp.replaceFirst("/target/classes/$", "/target/test-classes/");
        } else if (cp.endsWith("/target/test-classes/")) {
            classPath = cp.replaceFirst("/target/test-classes/$", "/target/classes/");
            classPathDbg = cp;
        }
    }

    public void start() {
        timer.schedule(this, 0, 1000);
    }

    public void stop() {
        timer.cancel();
    }

    @Override
    public void run() {
        boolean needReload = false;

        if (dir != null && check(dir)) {
            needReload = true;
        }
        if (classPath != null) {
            if (check(new File(classPath))) {
                needReload = true;
            }
        }
        if (classPath != null) {
            if (check(new File(classPathDbg))) {
                needReload = true;
            }
        }
        if (needReload) {
            reloader.run();
        }
    }

    private boolean check(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (check(f)) {
                    return true;
                }
            }
        } else {
            String path = file.getAbsolutePath();
            if (path.endsWith(".jar") || path.endsWith(".class")) {
                if (file.lastModified() > last_time) {
                    last_time = System.currentTimeMillis();
                    return true;
                }
            }
        }
        return false;
    }
}
