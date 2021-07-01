package org.noear.solon.logging.event;

/**
 * @author noear
 * @since 1.3
 */
public abstract class AppenderBase implements Appender{
    private String name;
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public boolean getEnable(){
        return true;
    }

    @Override
    public void start() {

    }
}
