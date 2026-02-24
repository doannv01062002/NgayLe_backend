package org.example.backend.utils;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

    public static Map<String, Object> createResponse(boolean success, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    public static Map<String, Object> createSuccessResponse(String message, Object data) {
        return createResponse(true, message, data);
    }

    public static Map<String, Object> createErrorResponse(String message) {
        return createResponse(false, message, null);
    }
}
