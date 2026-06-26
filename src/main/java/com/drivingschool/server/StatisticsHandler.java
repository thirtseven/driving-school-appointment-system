package com.drivingschool.server;

import com.drivingschool.model.Appointment;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsHandler implements HttpHandler {
    private final DataStore dataStore = DataStore.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        
        setCorsHeaders(exchange);

        if ("OPTIONS".equals(method)) {
            sendResponse(exchange, 204, "");
            return;
        }

        if ("GET".equals(method)) {
            sendResponse(exchange, 200, getStatisticsJson());
        } else {
            sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
        }
    }

    private String getStatisticsJson() {
        var appointments = dataStore.getAllAppointments();

        int totalAppointments = appointments.size();
        double totalRevenue = appointments.stream().mapToDouble(Appointment::getFinalPrice).sum();

        Map<String, Long> courseStats = appointments.stream()
            .collect(Collectors.groupingBy(a -> a.getCourse().getName(), Collectors.counting()));

        Map<String, Long> statusStats = appointments.stream()
            .collect(Collectors.groupingBy(a -> a.getStatus().name(), Collectors.counting()));

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"totalAppointments\": ").append(totalAppointments).append(",");
        json.append("\"totalStudents\": ").append(dataStore.getStudentCount()).append(",");
        json.append("\"totalRevenue\": ").append(String.format("%.2f", totalRevenue)).append(",");
        json.append("\"courseStats\": {");
        int i = 0;
        for (var entry : courseStats.entrySet()) {
            if (i > 0) json.append(",");
            json.append("\"").append(entry.getKey()).append("\": ").append(entry.getValue());
            i++;
        }
        json.append("},");
        json.append("\"statusStats\": {");
        i = 0;
        for (var entry : statusStats.entrySet()) {
            if (i > 0) json.append(",");
            json.append("\"").append(entry.getKey()).append("\": ").append(entry.getValue());
            i++;
        }
        json.append("}");
        json.append("}");
        return json.toString();
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
