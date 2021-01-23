package org.noear.mlog;

/**
 * 日志等级
 *
 * @author noear
 * @since 1.2
 */
public enum Level {
    TRACE(10),
    DEBUG(20),
    INFO(30),
    WARN(40),
    ERROR(50);

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
