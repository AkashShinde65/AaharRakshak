package com.aaharrakshak.intelligence;

import com.aaharrakshak.investigation.Action;

public interface ExternalAccountEventPublisher {

    void publish(Action action);
}
