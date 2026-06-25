package com.drivingschool.server;

import com.drivingschool.model.Appointment;
import com.drivingschool.singleton.AppointmentManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class AppointmentHandler implements HttpHandler {
    private final AppointmentManager manager = AppointmentManager.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        int statusCode;

        setCorsHeaders(exchange);

        if ("OPTIONS".equals(method)) {
            statusCode = 204;
            response = "";
        } else if ("GET".equals(method)) {
            statusCode = 200;
            response = getAppointmentsJson();
        } else {
            statusCode = 405;
            response = "{\"error\": \"Method not allowed\"}";
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }

    private String getAppointmentsJson() {
        StringBuilder json = new StringBuilder("{\"appointments\": [");
        var appointments = manager.getAllAppointments();
        json.append(appointments.stream()
                .map(this::appointmentToJson)
                .collect(Collectors.joining(",")));
        json.append("], \"count\": ").append(appointments.size()).append("}");
        return json.toString();
    }

    private String appointmentToJson(Appointment a) {
        return String.format(
            "{\"id\": \"%s\", \"studentName\": \"%s\", \"courseName\": \"%s\", " +
            "\"classTime\": \"%s\", \"finalPrice\": %.2f, \"status\": \"%s\"}",
            a.getId(), a.getStudent().getName(), a.getCourse().getName(),
            a.getFormattedClassTime(), a.getFinalPrice(), a.getStatus().name()
        );
    }

    private void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
