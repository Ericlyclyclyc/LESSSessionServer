package org.lyc122.dev.mcless.sessionserver.http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HexFormat;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class EndpointServer {
    private final int port;
    private final HttpHandler handler;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private volatile boolean isRunning = false;

    public EndpointServer(int port, HttpHandler handler) {
        this.port = port;
        this.handler = handler;
        ThreadFactory exceptionHandlingFactory = r -> {
            Random random = new Random(new Date().hashCode());
            byte[] bytes = new byte[4];
            random.nextBytes(bytes);
            String id = HexFormat.of().formatHex(bytes);
            Thread t = new Thread(r, "ApiRequestHandling-thread-" + id);
            t.setUncaughtExceptionHandler((thread, throwable) -> {
                StringBuilder em = new StringBuilder();
                em.append("Error in Api handling subthread:\n");
                em.append("=== Thread id: ").append(thread.getName()).append(" === State: ").append(thread.getState()).append(" ===\n");
                em.append("Error type: ").append(throwable.getClass().getName()).append("\n");
                em.append("Stack trace:\n");
                for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
                    em.append(stackTraceElement).append("\n");
                }
                System.out.println(em);
            });
            return t;
        };
        this.threadPool = Executors.newFixedThreadPool(10, exceptionHandlingFactory);
    }

    public void start() throws IOException {
        if (isRunning) {
            return;
        }

        serverSocket = new ServerSocket(port);
        isRunning = true;
        System.out.println("HTTP Server started on port " + port);

        while (isRunning) {
            try {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> {
                    try {
                        handleClient(clientSocket);
                    } catch (IOException e) {
                        System.err.println("Error handling client: " + e.getMessage());
                    }
                });
            } catch (IOException e) {
                if (isRunning) {
                    System.err.println("Accept error: " + e.getMessage());
                }
            }
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        try (clientSocket; BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             OutputStream outputStream = clientSocket.getOutputStream()) {

            // Read HTTP request line
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                sendErrorResponse(outputStream, 400, "Bad Request");
                return;
            }

            // Parse request line
            String[] parts = requestLine.split(" ", 3);
            if (parts.length < 2) {
                sendErrorResponse(outputStream, 400, "Bad Request");
                return;
            }

            String method = parts[0];
            String path = parts[1];

            if (!"GET".equalsIgnoreCase(method) && !"POST".equalsIgnoreCase(method)) {
                sendErrorResponse(outputStream, 405, "Method Not Allowed");
                return;
            }

            // Read headers
            String contentType = null;
            Integer contentLength = null;

            String headerLine;
            while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                if (headerLine.toLowerCase().startsWith("content-type:")) {
                    contentType = headerLine.substring("content-type:".length()).trim();
                } else if (headerLine.toLowerCase().startsWith("content-length:")) {
                    try {
                        contentLength = Integer.parseInt(headerLine.substring("content-length:".length()).trim());
                    } catch (NumberFormatException ignored) {
                        // Ignore invalid content-length
                    }
                }
            }

            // Read body - handle case where there's no Content-Length
            String requestBody = "";
            if (contentLength != null && contentLength > 0) {
                // Read exactly contentLength characters
                char[] buffer = new char[contentLength];
                int totalRead = 0;
                while (totalRead < contentLength) {
                    int read = reader.read(buffer, totalRead, contentLength - totalRead);
                    if (read == -1) break;
                    totalRead += read;
                }
                requestBody = new String(buffer, 0, totalRead);
            } else if (contentLength == null) {
                // No Content-Length header - read until EOF for POST requests
                if ("POST".equalsIgnoreCase(method)) {
                    StringBuilder bodyBuilder = new StringBuilder();
                    char[] buffer = new char[1024];
                    int read;
                    while ((read = reader.read(buffer)) != -1) {
                        bodyBuilder.append(buffer, 0, read);
                    }
                    requestBody = bodyBuilder.toString();
                }
            }

            // Handle request
            handler.handle(path, contentType, requestBody, outputStream);

        } catch (Exception e) {
            System.err.println("Unexpected error in handling client: " + e.getMessage());
        }
    }

    public void stop() throws IOException {
        isRunning = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        if (threadPool != null) {
            threadPool.shutdown();
        }
    }

    private void sendErrorResponse(OutputStream outputStream, int statusCode, String message) throws IOException {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        String response = "HTTP/1.1 " + statusCode + " " + message + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + messageBytes.length + "\r\n" +
                "\r\n" +
                message;
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

}
