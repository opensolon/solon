package com.drools.solon.listener;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:hongwen0928@outlook.com">Karas</a>
 * @date 2020/9/9
 * @since 7.37.0.Final
 */
public class DefaultProcessEventListener implements ProcessEventListener {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {

    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {

    }

    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent event) {

    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {

    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {

    }

    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {

    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {

    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {

    }

    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent event) {

    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {

    }
}
