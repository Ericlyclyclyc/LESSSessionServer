package org.lyc122.dev.mcless.sessionserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EndpointServer {
    private final int port;
    private final HttpHandler handler;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private volatile boolean isRunning = false;

    public EndpointServer(int port, HttpHandler handler) {
        this.port = port;
        this.handler = handler;
        this.threadPool = Executors.newFixedThreadPool(10); // Fixed thread pool
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
                // Create a thread for each connection
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
    // Method to handle a single client. Will run as an independent thread.
    private void handleClient(Socket clientSocket) throws IOException {
        try (clientSocket; BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             OutputStream outputStream = clientSocket.getOutputStream()) {

            // Read HTTP request header (first line)
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            // Parse request lines
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                sendErrorResponse(outputStream, 400, "Bad Request");
                return;
            }

            String method = requestParts[0];
            String path = requestParts[1];

            // Pass POST and GET only
            if (!"POST".equals(method) && !"GET".equals(method)) {
                sendErrorResponse(outputStream, 405, "Method Not Allowed");
                return;
            }

            // Read request headers
            String contentType = null;
            String contentLengthStr = null;

            String headerLine;
            while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                if (headerLine.toLowerCase().startsWith("content-type:")) {
                    contentType = headerLine.substring("content-type:".length()).trim();
                } else if (headerLine.toLowerCase().startsWith("content-length:")) {
                    contentLengthStr = headerLine.substring("content-length:".length()).trim();
                }
            }

            // Read request body
            String requestBody = "";
            if (contentLengthStr != null) {
                int contentLength = Integer.parseInt(contentLengthStr);
                char[] buffer = new char[contentLength];
                int totalRead = 0;
                while (totalRead < contentLength) {
                    int read = reader.read(buffer, totalRead, contentLength - totalRead);
                    if (read == -1) break;
                    totalRead += read;
                }
                requestBody = new String(buffer, 0, totalRead);
            }

            // Create handler and execute
            handler.handle(path, contentType, requestBody, outputStream);

        }
    }
    // Method to stop the server
    public void stop() throws IOException {
        isRunning = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        if (threadPool != null) {
            threadPool.shutdown();
        }
    }
    // Method to send error response
    private void sendErrorResponse(OutputStream outputStream, int statusCode, String message) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + message + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + message.length() + "\r\n" +
                "\r\n" +
                message;
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}