package org.elas.momentum.highlight.domain.port.in;

import org.elas.momentum.highlight.domain.model.MediaType;

public interface PublishHighlightUseCase {

    record Command(String publisherId, String mediaUrl, MediaType mediaType, String caption, String sport) {}

    String publish(Command command);
}
