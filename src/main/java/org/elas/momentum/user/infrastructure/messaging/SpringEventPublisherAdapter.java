package org.elas.momentum.user.infrastructure.messaging;

import org.elas.momentum.shared.domain.DomainEvent;
import org.elas.momentum.user.domain.port.out.UserEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class SpringEventPublisherAdapter implements UserEventPublisher {

    private final ApplicationEventPublisher publisher;

    SpringEventPublisherAdapter(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(List<DomainEvent> events) {
        events.forEach(publisher::publishEvent);
    }
}
