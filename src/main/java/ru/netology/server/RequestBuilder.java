package ru.netology.server;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

public class RequestBuilder {
    private String path;
    private String method;

    public RequestBuilder addMethod(String method) {
        this.method = method;
        return this;
    }

    public RequestBuilder addPath(String path) {
        this.path = path;
        return this;
    }

    public Request build() {
        try {
            URI uri = new URI(this.path);
            this.path = uri.getPath();
            List<NameValuePair> queryList = URLEncodedUtils.parse(uri, Charset.defaultCharset());
            return new Request(this.method, this.path, queryList);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}