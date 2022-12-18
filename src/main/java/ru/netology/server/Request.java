package ru.netology.server;

import org.apache.http.NameValuePair;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Request {
    private final String path;
    private final String method;

    private List<NameValuePair> listQuery;
    public Request(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public Request(String method, String path, List<NameValuePair> listQuery) {
        this.method = method;
        this.path = path;
        this.listQuery = listQuery;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public List<NameValuePair> getQueryParam() {
        return listQuery;
    }

    public List<NameValuePair> getQueryParam(String name) {
        return listQuery.stream()
                .filter(str -> Objects.equals(str.getName(),name))
                .collect(Collectors.toList());
    }
}
