package com.drivingschool.server;

import com.drivingschool.model.Student;
import com.drivingschool.model.Student.StudentType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class StudentHandler implements HttpHandler {
    private final DataStore dataStore = DataStore.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        setCorsHeaders(exchange);

        if ("OPTIONS".equals(method)) {
            sendResponse(exchange, 204, "");
            return;
        }

        switch (method) {
            case "GET":
                handleGet(exchange, path);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "PUT":
                handlePut(exchange, path);
                break;
            case "DELETE":
                handleDelete(exchange, path);
                break;
            default:
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/api/students")) {
            sendResponse(exchange, 200, getAllStudentsJson());
        } else if (path.startsWith("/api/students/")) {
            String id = path.substring("/api/students/".length());
            Optional<Student> student = dataStore.getStudent(id);
            if (student.isPresent()) {
                sendResponse(exchange, 200, studentToJson(student.get()));
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Student not found\"}");
            }
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        
        try {
            String name = extractJsonValue(body, "name");
            String phone = extractJsonValue(body, "phone");
            String typeStr = extractJsonValue(body, "type");
            
            if (name == null || phone == null || typeStr == null) {
                sendResponse(exchange, 400, "{\"error\": \"Missing required fields: name, phone, type\"}");
                return;
            }

            StudentType type = StudentType.valueOf(typeStr);
            Student student = dataStore.addStudent(name, phone, type);
            sendResponse(exchange, 201, studentToJson(student));
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "{\"error\": \"Invalid student type. Use: REGULAR, STUDENT, GROUP\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handlePut(HttpExchange exchange, String path) throws IOException {
        if (!path.startsWith("/api/students/")) {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
            return;
        }

        String id = path.substring("/api/students/".length());
        String body = readRequestBody(exchange);
        
        try {
            String name = extractJsonValue(body, "name");
            String phone = extractJsonValue(body, "phone");
            String typeStr = extractJsonValue(body, "type");
            
            if (name == null || phone == null || typeStr == null) {
                sendResponse(exchange, 400, "{\"error\": \"Missing required fields: name, phone, type\"}");
                return;
            }

            StudentType type = StudentType.valueOf(typeStr);
            Optional<Student> updated = dataStore.updateStudent(id, name, phone, type);
            
            if (updated.isPresent()) {
                sendResponse(exchange, 200, studentToJson(updated.get()));
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Student not found\"}");
            }
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "{\"error\": \"Invalid student type. Use: REGULAR, STUDENT, GROUP\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (!path.startsWith("/api/students/")) {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
            return;
        }

        String id = path.substring("/api/students/".length());
        if (dataStore.deleteStudent(id)) {
            sendResponse(exchange, 200, "{\"message\": \"Student deleted\"}");
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Student not found\"}");
        }
    }

    private String getAllStudentsJson() {
        List<Student> students = dataStore.getAllStudents();
        String json = students.stream()
            .map(this::studentToJson)
            .collect(Collectors.joining(","));
        return "{\"students\": [" + json + "], \"count\": " + students.size() + "}";
    }

    private String studentToJson(Student s) {
        return String.format(
            "{\"id\": \"%s\", \"name\": \"%s\", \"phone\": \"%s\", \"type\": \"%s\", \"discount\": \"%.0f%%\"}",
            s.getId(), s.getName(), s.getPhone(), s.getType().name(), getDiscountRate(s.getType()) * 100
        );
    }

    private double getDiscountRate(StudentType type) {
        switch (type) {
            case STUDENT: return 0.8;
            case GROUP: return 0.7;
            default: return 1.0;
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        byte[] bytes = is.readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }
}
