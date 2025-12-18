package org.lyc122.dev.mcless.sessionserver;

import org.lyc122.dev.mcless.sessionserver.http.ApiEndpointServerHandler;
import org.lyc122.dev.mcless.sessionserver.http.EndpointServer;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static final String versionId = "v0.1";
    private static final EndpointServer apiEndpointServer = new EndpointServer(8859,ApiEndpointServerHandler.getInstance());

    private static void startServer(){
        try {
            apiEndpointServer.start();
        } catch (IOException e) {
            System.out.println("Error occurred starting the server: "+e.getMessage());
            for(StackTraceElement element : e.getStackTrace()){
                System.out.println(element);
            }
        }
    }
    public static void main(String[] args) {
        System.out.printf("Starting LESS Session Server "+versionId);
        startServer();
        while(true){
            String s = new Scanner(System.in).nextLine();
            if(Objects.equals(s, "stop")){
                try {
                    apiEndpointServer.stop();
                } catch (IOException e) {
                    System.out.println("Error closing the server...");
                }
                System.exit(0);
            }
        }
    }
}