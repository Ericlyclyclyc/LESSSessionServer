package org.lyc122.dev.mcless.sessionserver.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class ApiEndpointServerHandler implements HttpHandler{

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(String path, String contentType, String requestBody, OutputStream outputStream) {
        if(!Objects.equals(contentType, "application/json")){
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("error", "Invalid Content type");
            try {
                ApiResponseWriter.writeHttpResponse(outputStream,415, "Unsupported Media Type", errorNode);
            } catch (IOException e) {
                handleException(e);
            }
        }
    }

    @Override
    public void run() {

    }

    public void handleException(Exception e){
        System.out.println("Error handling client: "+e.getLocalizedMessage());
        System.out.println("Stack trace:");
        for(StackTraceElement el : e.getStackTrace() ){
            System.out.println(el);
        }
    }

    public static ApiEndpointServerHandler getInstance(){
        return new ApiEndpointServerHandler();
    }

    private ApiEndpointServerHandler(){

    }
}
