package org.noear.solon.cloud.model.log;

/**
 * @author noear
 * @since 1.2
 */
public enum Level {
    TRACE(1),
    DEBUG(2),
    INFO(3),
    WARN(4),
    ERROR(5);

    public final int code;

    public static Level of(int code){
        for(Level v : values()){
            if(v.code == code){
                return v;
            }
        }

        return INFO;
    }

    Level(int code) {
        this.code = code;
    }
}
