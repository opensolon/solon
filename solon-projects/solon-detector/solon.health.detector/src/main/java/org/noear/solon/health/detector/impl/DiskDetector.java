package org.noear.solon.health.detector.impl;

import org.noear.solon.health.detector.AbstractDetector;
import org.noear.solon.health.detector.util.CmdUtil;
import org.noear.solon.health.detector.util.SizeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 磁盘 探测器
 *
 * @author 夜の孤城
 * @since 1.2
 * */
public class DiskDetector extends AbstractDetector {
    static final Logger log = LoggerFactory.getLogger(DiskDetector.class);

    //Linux 查看硬盘数据命令,备用
    // df -m
    // lsblk -d -b -l -n -o SIZE,TYPE
    private static final Pattern linuxDiskPattern = Pattern.compile("\\s+([\\d]+)\\s+([\\d]+)\\s+([\\d]+)\\s+([\\d]+%)\\s+(/.*)", 42);
    private static final Pattern linuxTotalDistPattern = Pattern.compile("\\s+([\\d]+)\\s+(\\s+)",42);
    private static final Pattern macDiskPattern = Pattern.compile("\\s+([\\d]+)\\s+([\\d]+)\\s+([\\d]+)\\s+([\\d]+%)\\s+\\d+\\s+\\d+\\s+\\d+%\\s+(/.*)", 42);

    @Override
    public String getName() {
        return "disk";
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String, Object> details = new LinkedHashMap<>();


        long totals = 0;
        long totalUsed = 0;
        boolean isMac=osName.indexOf("mac os") > -1;
        File[] rootFiles= File.listRoots();

        if (isMac) {
            rootFiles=new File("/Volumes").listFiles();
        }
        for (File file : rootFiles) {
            Map<String, Object> disk = new HashMap<>();
            long total = file.getTotalSpace();
            long free = file.getFreeSpace();
            long used = total - free;
            totals += total;
            totalUsed += used;
            String ratio = (total > 0L ? used * 100L / total : 0L) + "%";

            disk.put("total", SizeUtil.formatByteSize(total));
            disk.put("free", SizeUtil.formatByteSize(free));
            disk.put("used", SizeUtil.formatByteSize(used));
            disk.put("ratio", ratio);
            String path=isMac?file.getPath().replace("/Volumes/",""):file.getPath();
            details.put(path, disk);
        }
        Map<String, Object> info = new LinkedHashMap<>();

        info.put("total", SizeUtil.formatByteSize(totals));
        info.put("used", SizeUtil.formatByteSize(totalUsed));
        info.put("details", details);

        return info;
    }
}
