package org.lyc122.dev.mcless.sessionserver.http;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ApiResponseWriter {

    /**
     * 向输出流写入HTTP响应
     * @param outputStream 输出流对象
     * @param statusCode HTTP状态码（如200/404/500）
     * @param statusMessage 状态码描述信息（如"OK"/"Not Found"）
     * @param jsonNode Jackson的JSON节点对象
     * @throws IOException 发生I/O错误时抛出
     */
    public static void writeHttpResponse(
            OutputStream outputStream,
            int statusCode,
            String statusMessage,
            JsonNode jsonNode
    ) throws IOException {
        // 构建完整的HTTP响应报文
        String response = buildHttpResponse(statusCode, statusMessage, jsonNode);

        // 写入输出流
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private static String buildHttpResponse(
            int statusCode,
            String statusMessage,
            JsonNode jsonNode
    ) {
        // 创建响应头
        String headers = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" +
                "Content-Type: application/json; charset=UTF-8\r\n" +
                "Content-Length: " + jsonNode.toString().getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "Cache-Control: no-cache, no-store, must-revalidate\r\n" +
                "Pragma: no-cache\r\n" +
                "Expires: 0\r\n" +
                "\r\n";

        // 组合完整响应报文
        return headers + jsonNode;
    }
}