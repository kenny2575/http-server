package ru.netology.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SingleThreadRunner implements Runnable {
    private final Socket socket;
    private final String INTERNAL_SERVER_PAGE = "/internalError.html";
    private final String NO_FOUND_PAGE = "/noFoundPage.html";
    private final String NO_ALLOWED_REQUEST = "/wrongMethod.html";

    SingleThreadRunner(Socket socket) {
        this.socket = socket;
    }

    void badRequest(BufferedOutputStream out) throws IOException {
        Server.processFile(new Request(null, INTERNAL_SERVER_PAGE), out);
    }

    void notFoundRequest(BufferedOutputStream out) throws IOException {
        Server.processFile(new Request(null, NO_FOUND_PAGE), out);
    }

    void notAllowedRequest(Request request, BufferedOutputStream out) throws IOException {
        Server.processNotAllowed(new Request(request.getMethod(), NO_ALLOWED_REQUEST), out);
    }

    @Override
    public void run() {

        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {

            var requestLine = in.readLine();
            var requestData = requestLine.split(" ");

            System.out.println(requestData[0] + " " + requestData[1]);

            if (requestData.length != 3) {
                badRequest(out);
            } else {

                Request request = new Request(requestData[0], requestData[1]);

                if (Server.handlers.containsKey(request.getPath())) {
                    if (Server.handlers.get(request.getPath()).containsKey(request.getMethod())) {
                        Server.handlers.get(request.getPath()).get(request.getMethod()).handle(request, out);
                    } else {
                        notAllowedRequest(request, out);
                    }
                } else {
                    notFoundRequest(out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
