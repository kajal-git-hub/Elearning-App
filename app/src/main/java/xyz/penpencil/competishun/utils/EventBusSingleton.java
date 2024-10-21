package xyz.penpencil.competishun.utils;

import com.google.common.eventbus.EventBus;

public class EventBusSingleton {
    private static EventBus eventBus = new EventBus();

    public static EventBus getInstance() {
        return eventBus;
    }
}