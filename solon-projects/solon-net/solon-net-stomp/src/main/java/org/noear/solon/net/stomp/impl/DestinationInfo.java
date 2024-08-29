/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.net.stomp.impl;

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
        if(obj == null){
            return false;
        }
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
