package webapp.dso;

import org.noear.snack.ONode;

import java.util.HashMap;
import java.util.Map;

public class DbMap extends HashMap<String,Object> {
    public DbMap add(String key, Object value){
        put(key,value);
        return this;
    }

    public DbMap(){
        super();
    }

    public DbMap(Object entity){
        super();
        Map<String,Object> tmp = ONode.loadObj(entity).toObject(null);
        this.putAll(tmp);
    }
}
