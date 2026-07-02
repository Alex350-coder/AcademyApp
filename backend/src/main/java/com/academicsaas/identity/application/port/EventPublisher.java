package com.academicsaas.identity.application.port;

public interface EventPublisher {

    void publish(Object event);
}
