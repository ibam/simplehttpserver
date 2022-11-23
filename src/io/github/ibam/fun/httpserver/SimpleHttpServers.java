package io.github.ibam.fun.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHttpServers {

    private static final int MAX_SERVERS = 10;
    private static final int MAX_GLOBAL_CONCURRENCY = 100;
    private static final Map<Integer, SimpleHttpServer> servers = new HashMap<>();
    private static final ExecutorService serverExecutor = Executors.newFixedThreadPool(MAX_SERVERS);
    private static final ExecutorService requestExecutor = Executors.newFixedThreadPool(MAX_GLOBAL_CONCURRENCY);

    public static SimpleHttpServer listen(final int port) {
        final SimpleHttpServer server = servers.computeIfAbsent(port, (s) -> new SimpleHttpServer(port, requestExecutor));
        serverExecutor.submit(server);
        return server;
    }

    static class SimpleHttpServer implements Runnable {

        final ServerSocket listeningSocket;
        final ExecutorService requestExecutor;
        private SimpleHttpServer(final int port, final ExecutorService requestExecutor) {
            try {
                this.requestExecutor = requestExecutor;
                this.listeningSocket = new ServerSocket(port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    final Socket incomingSocket = listeningSocket.accept();
                    System.out.println("Accepting new incoming socket " + incomingSocket);
                    requestExecutor.submit(new SimpleHttpHandler(requestExecutor, incomingSocket));
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }
        }
    }
}
