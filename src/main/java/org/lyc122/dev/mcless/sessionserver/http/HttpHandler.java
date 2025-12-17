package org.lyc122.dev.mcless.sessionserver.http;

import java.io.OutputStream;

/**
 * HTTP Request parsing interface
 */
public interface HttpHandler extends Runnable {
    /**
     * Handle http requests
     * @param path request path
     * @param contentType request content type
     * @param requestBody request body string
     * @param outputStream output stream(response)
     */
    void handle(String path,String contentType, String requestBody, OutputStream outputStream);
}