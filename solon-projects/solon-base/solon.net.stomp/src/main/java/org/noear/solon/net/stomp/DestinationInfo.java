package org.noear.solon.net.stomp;

/**
 * 通道信息
 *
 * @author limliu
 * @since 2.7
 */
public class DestinationInfo {
    /**
     * 通道信息
     */
    private String destination;

    /**
     * 通道ID
     */
    private String subscription;

    /**
     * sessionId
     */
    private String sessionId;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DestinationInfo)) {
            return false;
        }
        DestinationInfo target = (DestinationInfo) obj;
        if (getSessionId().equals(target.getSessionId())
                && getDestination().equals(target.getDestination())) {
            return true;
        }
        return false;
    }
}
