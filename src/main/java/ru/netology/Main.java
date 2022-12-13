package ru.netology;

import ru.netology.server.Server;

import java.io.IOException;

public class Main {
    public static final String GET = "GET";
    public static final String POST = "POST";

    public static void main(String[] args) {
        Server.addHandler(GET, "/index.html", Server::processFile);
        Server.addHandler(GET, "/spring.svg", Server::processFile);
        Server.addHandler(GET, "/spring.png", Server::processFile);
        Server.addHandler(GET, "/resources.html", Server::processFile);
        Server.addHandler(GET, "/styles.css", Server::processFile);
        Server.addHandler(GET, "/app.js", Server::processFile);
        Server.addHandler(GET, "/links.html", Server::processFile);
        Server.addHandler(GET, "/forms.html", Server::processFile);
        Server.addHandler(GET, "/events.html", Server::processFile);
        Server.addHandler(GET, "/events.js", Server::processFile);
        Server.addHandler(GET, "/internalError.html", Server::processFile);
        Server.addHandler(GET, "/styles_internal.css", Server::processFile);
        Server.addHandler(GET, "/classic.html", Server::processFileClassic);
        Server.addHandler(GET, "/wrongMethod.html", Server::processFileClassic);
        Server.addHandler(GET, "/messages", (request, out) -> {
            var message = String.format("This is %s message", request.getMethod());
            try {
                Server.sc_ok(out, "text/plain", message.length(), message);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Server.addHandler(POST, "/messages", (request, out) -> {
            var message = String.format("This is %s message", request.getMethod());
            try {
                Server.sc_ok(out, "text/plain", message.length(), message);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        var server = new Server();
        server.run();

    }
}


