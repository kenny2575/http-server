package ru.netology.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int PORT = 9999;
    private final int MAX_THREAD_POOL = 64;
    private ExecutorService executeIt;

    public Server() {
        this.executeIt = Executors.newFixedThreadPool(MAX_THREAD_POOL);
    }

    public void run(){
        try(var serverSocket = new ServerSocket(PORT)) {
            while(true){
                var socket = serverSocket.accept();
                this.executeIt.execute(new SingleThreadRunner(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
