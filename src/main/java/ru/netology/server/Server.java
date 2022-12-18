package ru.netology.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int PORT = 9999;
    private final int MAX_THREAD_POOL = 64;
    private final ExecutorService executeIt;
    public static final String SC_OK = "200 OK";
    public static final String NOT_ALLOWED = "405 Not allowed";
    static ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<>();

    public Server() {
        this.executeIt = Executors.newFixedThreadPool(MAX_THREAD_POOL);
    }

    public void run() {
        try (var serverSocket = new ServerSocket(PORT)) {
            while (true) {
                var socket = serverSocket.accept();
                this.executeIt.execute(new SingleThreadRunner(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sc_ok(BufferedOutputStream out, String mimeType, long length) throws IOException {
        sendResponse(out, mimeType, length, "", SC_OK);
    }

    public static void sc_ok(BufferedOutputStream out, String mimeType, long length, String message) throws IOException {
        sendResponse(out, mimeType, length, message, SC_OK);
    }

    public static void sendNotAllowed(BufferedOutputStream out, String mimeType, long length, String message) throws IOException {
        sendResponse(out, mimeType, length, message, NOT_ALLOWED);
    }

    public static void sendResponse(BufferedOutputStream out, String mimeType, long length, String message, String status) throws IOException {
        out.write((
                "HTTP/1.1 " + status + "\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n" + message
        ).getBytes());
    }

    public static void processFile(Request request, BufferedOutputStream out) throws IOException {
        final var filePath = Path.of(".", "public", request.getPath());
        final var mimeType = Files.probeContentType(filePath);
        final var length = Files.size(filePath);
        sc_ok(out, mimeType, length);
        Files.copy(filePath, out);
        out.flush();
    }

    public static void processFileClassic(Request request, BufferedOutputStream out) {
        try {
            final var filePath = Path.of(".", "public", request.getPath());
            final var mimeType = Files.probeContentType(filePath);
            final var length = Files.size(filePath);
            final var template = Files.readString(filePath);
            final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString())
                    .getBytes();
            sc_ok(out, mimeType, length);
            out.write(content);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void processNotAllowed(Request request, BufferedOutputStream out) {
        try {
            final var filePath = Path.of(".", "public", request.getPath());
            final var mimeType = Files.probeContentType(filePath);
            final var template = Files.readString(filePath);
            CharSequence charSequence = request.getMethod();
            try {
                final var content = template.replace(
                                "{method}",
                                "'"+charSequence+"'")
                        .getBytes();
                sendNotAllowed(out, mimeType, content.length, "");
                out.write(content);
            }catch (Exception e) {
                e.printStackTrace();
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void addHandler(String method, String path, Handler handler) {
        handlers.putIfAbsent(path, new ConcurrentHashMap<>());
        handlers.get(path).put(method, handler);
    }
}
