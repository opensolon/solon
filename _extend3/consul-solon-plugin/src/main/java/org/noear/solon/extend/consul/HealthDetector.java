package org.noear.solon.extend.consul;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.extend.consul.detector.*;

import java.util.*;

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
       String detectorNamesStr= app.cfg().get(Constants.DISCOVERY_HEALTH_DETECTOR);

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

}
