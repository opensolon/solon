/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.health.detector.impl;

import org.noear.solon.health.detector.AbstractDetector;
import org.noear.solon.health.detector.util.CmdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    static final Logger log = LoggerFactory.getLogger(CpuDetector.class);

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
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            info.put("err","Get CPU Info Failed: " + e.getMessage() + "");

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
        //巨硬在Windows 11 24H2 开始移除了WMIC,建议换用powershell,只要系统内置的powershell版本大于3.0,也就是Windows 8+ 应该都可以用
        String[] cmd = { "powershell", "$totalMem=(Get-CimInstance -ClassName Win32_ComputerSystem).TotalPhysicalMemory;Get-Process|ForEach-Object{$totalMemoryPercentage+=(($_.WorkingSet64 / $totalMem) * 100)};$totalMemoryPercentage" };

        long[] c0 = readCpuTime(CmdUtil.execute(cmd));
        Thread.sleep(30L);
        long[] c1 = readCpuTime(CmdUtil.execute(cmd));
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
        String text=CmdUtil.execute("/bin/sh","-c","ps -A -o %mem | awk '{s+=$1} END {print s}'");
        detectorInfo.put("ratio",Float.valueOf(text));
    }
}
