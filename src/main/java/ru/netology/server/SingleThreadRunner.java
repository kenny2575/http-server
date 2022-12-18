package ru.netology.server;

import org.apache.http.NameValuePair;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

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
            assert requestLine != null;
            var requestData = requestLine.split(" ");

            if (requestData.length != 3) {
                badRequest(out);
            } else {

                Request request = new RequestBuilder()
                        .addMethod(requestData[0])
                        .addPath(requestData[1])
                        .build();

                printParams(request.getQueryParam());
                printParams(request.getQueryParam("test"));

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

    public void printParams(List<NameValuePair> list){
        list.forEach(System.out::println);
    }
}
