package org.noear.solon.extend.health.detector;

import org.noear.solon.core.event.EventBus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Cpu 探测器
 *
 * @author 夜の孤城
 * @since 1.2
 * */
public class CpuDetector extends AbstractDetector {
    private static final Pattern wmicPattern = Pattern.compile("(.*)\\s+([\\d]+)\\s+([\\d]+)", 42);
    private static final Pattern topPattern = Pattern.compile("([\\d]+.[\\d]+)[%?|\\s?]id,", 42);

    @Override
    public String getName() {
        return "cpu";
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String,Object> info=new LinkedHashMap<>();
        try {

            if (osName.indexOf("windows")!=-1) {
                readCpuRatioForWindows(info);
            } else {
                readCpuRatioForLinux(info);
            }
        } catch (Exception ex) {
            EventBus.push(ex);
            info.put("err","Get CPU Info Failed: " + ex.getMessage() + "");

        }
        return info;
    }

    private long[] readCpuTime(String text) throws Exception {
        long[] retn = null;
        List<String[]> lines = matcher(wmicPattern, text);
        long idletime = 0L;
        long kneltime = 0L;
        long usertime = 0L;
        for (String[] line : lines) {

            String caption = line[1];
            long kernelModeTime = Long.valueOf(line[2]);
            long userModeTime = Long.valueOf(line[3]);
            if (caption.indexOf("WMIC.exe") >= 0) {
                continue;
            }
            if (caption.equals("System Idle Process") || caption.equals("System")) {
                idletime += kernelModeTime;
                idletime += userModeTime;

                continue;
            }
            kneltime += kernelModeTime;
            usertime += userModeTime;
        }
        retn = new long[2];
        retn[0] = idletime;
        retn[1] = kneltime + usertime;
        return retn;
    }

    private void readCpuRatioForWindows(Map<String,Object> detectorInfo) throws Exception {
        String[] cmd = { "wmic", "process", "get", "Caption,KernelModeTime,UserModeTime" };

        long[] c0 = readCpuTime(execute(cmd));
        Thread.sleep(30L);
        long[] c1 = readCpuTime(execute(cmd));
        if (c0 != null && c1 != null) {
            long idletime = c1[0] - c0[0];
            long busytime = c1[1] - c0[1];

            detectorInfo.put("ratio",(float)busytime * 100.0F / (float)(busytime + idletime));
        }
    }

    private void readCpuRatioForLinux(Map<String,Object> detectorInfo) throws Exception {
       /** String text = this.execute(new String[]{"/bin/sh", "-c", "top -b -n 2 -d 0.1 | grep 'Cpu(s)'"});
        List<String[]> ratios = this.matcher(topPattern, text);
        float idle = 0.0F;
        for(String []ratio:ratios) {
            idle+=Float.valueOf(ratio[1]);
        }
        detectorInfo.put("ratio",100.0F - idle);
        **/
       String text=this.execute("/bin/sh","-c","ps -A -o %mem | awk '{s+=$1} END {print s}'");
        detectorInfo.put("ratio",Float.valueOf(text));
    }
}
