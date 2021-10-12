package org.noear.solon.cloud.extend.consul.detector;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.consul.ConsulProps;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.health.HealthChecker;

import java.util.*;

/**
 * 健康探测器
 *
 * @author noear
 */
public class HealthDetector {
    private static final Detector[] allDetectors=new Detector[]{
            new CpuDetector(),
            new JvmMemoryDetector(),
            new OsDetector(),
            new QpsDetector(),
            new MemoryDetector(),
            new DiskDetector()
    };
    Set<Detector> detectors = new HashSet<>();

    public HealthDetector(){

    }

    public void startDetect(SolonApp app){
        String detectorNamesStr= ConsulProps.instance.getDiscoveryHealthDetector();

        if(Utils.isEmpty(detectorNamesStr)){
            return;
        }

        Set<String> detectorNames=new HashSet<>(Arrays.asList(detectorNamesStr.split(",")));

        for(Detector detector:allDetectors){
            if(detectorNames.contains(detector.getName())){
                detectors.add(detector);
                if(detector instanceof QpsDetector){
                    ((QpsDetector) detector).toDetect(app);
                }
            }
        }
    }

    public Map<String,Object> getInfo(){
        Map<String,Object> info=new HashMap<>();
        for(Detector detector:detectors){
            info.put(detector.getName(),detector.getInfo());
        }
        return info;
    }

    static HealthDetector detector;
    public static void start(){
        if(detector != null){
            return;
        }

        //1.添加Solon服务，提供检测用
        //
        detector = new HealthDetector();
        detector.startDetect(Solon.global());

        HealthChecker.addIndicator("consul",()->{
            return Result.succeed(detector.getInfo());
        });

//        Solon.global().get(healthCheckPath, ctx -> {
//            Map<String, Object> info = new HashMap<>();
//            info.put("status", "OK");
//            info.putAll(detector.getInfo());
//            ctx.outputAsJson(GsonFactory.getGson().toJson(info));
//        });
    }
}
