package com.drivingschool.server;

import com.drivingschool.model.*;
import com.drivingschool.model.Appointment.AppointmentStatus;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AppointmentHandler implements HttpHandler {
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
        if (path.equals("/api/appointments")) {
            sendResponse(exchange, 200, getAllAppointmentsJson());
        } else if (path.startsWith("/api/appointments/")) {
            String id = path.substring("/api/appointments/".length());
            Optional<Appointment> apt = dataStore.getAppointment(id);
            if (apt.isPresent()) {
                sendResponse(exchange, 200, appointmentToJson(apt.get()));
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Appointment not found\"}");
            }
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        
        try {
            String studentId = extractJsonValue(body, "studentId");
            String courseType = extractJsonValue(body, "courseType");
            String classTimeStr = extractJsonValue(body, "classTime");
            String withSimulation = extractJsonValue(body, "withSimulation");
            String withCoaching = extractJsonValue(body, "withCoaching");

            Optional<Student> student = dataStore.getStudent(studentId);
            if (!student.isPresent()) {
                sendResponse(exchange, 400, "{\"error\": \"Student not found\"}");
                return;
            }

            Course course = createCourse(courseType);
            if ("true".equals(withSimulation)) {
                course = new com.drivingschool.decorator.ExamVenueSimulation(course);
            }
            if ("true".equals(withCoaching)) {
                course = new com.drivingschool.decorator.SprintCoaching(course);
            }

            LocalDateTime classTime = LocalDateTime.parse(classTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            Appointment appointment = dataStore.addAppointment(student.get(), course, classTime);

            sendResponse(exchange, 201, appointmentToJson(appointment));
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handlePut(HttpExchange exchange, String path) throws IOException {
        if (!path.startsWith("/api/appointments/")) {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
            return;
        }

        String id = path.substring("/api/appointments/".length());
        String body = readRequestBody(exchange);
        
        try {
            String statusStr = extractJsonValue(body, "status");
            AppointmentStatus status = AppointmentStatus.valueOf(statusStr);
            
            Optional<Appointment> updated = dataStore.updateAppointmentStatus(id, status);
            if (updated.isPresent()) {
                sendResponse(exchange, 200, appointmentToJson(updated.get()));
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Appointment not found\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (!path.startsWith("/api/appointments/")) {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
            return;
        }

        String id = path.substring("/api/appointments/".length());
        if (dataStore.deleteAppointment(id)) {
            sendResponse(exchange, 200, "{\"message\": \"Appointment deleted\"}");
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Appointment not found\"}");
        }
    }

    private Course createCourse(String type) {
        switch (type) {
            case "SUBJECT_2":
                return new com.drivingschool.model.SubjectTwoCourse();
            case "SUBJECT_3":
                return new com.drivingschool.model.SubjectThreeCourse();
            case "MOCK_EXAM":
                return new com.drivingschool.model.MockExamCourse();
            default:
                throw new IllegalArgumentException("Unknown course type: " + type);
        }
    }

    private String getAllAppointmentsJson() {
        List<Appointment> appointments = dataStore.getAllAppointments();
        String json = appointments.stream()
            .map(this::appointmentToJson)
            .collect(Collectors.joining(","));
        return "{\"appointments\": [" + json + "], \"count\": " + appointments.size() + "}";
    }

    private String appointmentToJson(Appointment a) {
        return String.format(
            "{\"id\": \"%s\", \"studentId\": \"%s\", \"studentName\": \"%s\", \"courseName\": \"%s\", " +
            "\"classTime\": \"%s\", \"finalPrice\": %.2f, \"status\": \"%s\", \"appointmentTime\": \"%s\"}",
            a.getId(), a.getStudent().getId(), a.getStudent().getName(), a.getCourse().getName(),
            a.getFormattedClassTime(), a.getFinalPrice(), a.getStatus().name(),
            a.getFormattedAppointmentTime()
        );
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
        pattern = "\"" + key + "\"\\s*:\\s*(true|false)";
        matcher = java.util.regex.Pattern.compile(pattern).matcher(json);
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
