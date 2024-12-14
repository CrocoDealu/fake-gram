package org.example.utils;

import java.util.List;

public class Event {

    private final Events eventName;
    private final List<String> parameters;

    public Event(Events eventName, List<String> parameters) {
        this.eventName = eventName;
        this.parameters = parameters;
    }

    public Events getEventName() {
        return eventName;
    }

    public List<String> getParameters() {
        return parameters;
    }
}
