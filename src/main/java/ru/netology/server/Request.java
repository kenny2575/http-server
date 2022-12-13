package ru.netology.server;

public class Request {
    private final String path;
    private final String method;

    public Request(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }
}
