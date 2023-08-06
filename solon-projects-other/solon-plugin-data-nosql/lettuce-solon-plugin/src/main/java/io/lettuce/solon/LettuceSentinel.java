package io.lettuce.solon;


/**
 * Lettuce哨兵
 *
 * @author Sorghum
 * @since 2.3.8
 */
public class LettuceSentinel {

    String host;

    int port;

    String password;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
