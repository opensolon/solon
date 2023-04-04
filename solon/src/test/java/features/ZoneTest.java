package features;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author noear 2023/1/31 created
 */
public class ZoneTest {
    public static void main(String[] args){
        TimeZone zone = TimeZone.getTimeZone(ZoneId.of("+07"));
        System.out.println(zone);
        //sun.util.calendar.ZoneInfo[id="GMT+07:00",offset=25200000,dstSavings=0,useDaylight=false,transitions=0,lastRule=null]
    }
}
