package org.elas.momentum.user.domain.port.out;

import org.elas.momentum.shared.domain.DomainEvent;

import java.util.List;

public interface UserEventPublisher {
    void publish(List<DomainEvent> events);
}
