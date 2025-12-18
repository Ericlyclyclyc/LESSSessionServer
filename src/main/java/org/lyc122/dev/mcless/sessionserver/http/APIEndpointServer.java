package org.lyc122.dev.mcless.sessionserver.http;

import java.io.OutputStream;
import java.util.Objects;

public class APIEndpointServer implements HttpHandler{

    @Override
    public void handle(String path, String contentType, String requestBody, OutputStream outputStream) {
        if(!Objects.equals(contentType, "application/json")){

        }
    }

    @Override
    public void run() {

    }

    private String generateResponse(int code, String message, String response){
        String resp = "";
        resp += "HTTP/1.2 "+ code + " "+ message + "\r\n";

        return resp;
    }
}
