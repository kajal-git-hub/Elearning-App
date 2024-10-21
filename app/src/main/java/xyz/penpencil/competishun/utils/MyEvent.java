package xyz.penpencil.competishun.utils;

public class MyEvent {
    private final String message;

    public MyEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}