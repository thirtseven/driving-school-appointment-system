package com.drivingschool.server;

import com.drivingschool.factory.*;
import com.drivingschool.model.Course;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CourseHandler implements HttpHandler {
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
            response = getCoursesJson();
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

    private String getCoursesJson() {
        CourseFactory subject2Factory = new SubjectTwoFactory();
        CourseFactory subject3Factory = new SubjectThreeFactory();
        CourseFactory mockFactory = new MockExamFactory();

        Course subject2 = subject2Factory.createCourse();
        Course subject3 = subject3Factory.createCourse();
        Course mock = mockFactory.createCourse();

        return String.format("{\"courses\": [" +
            "{\"name\": \"%s\", \"basePrice\": %.2f, \"duration\": %d, \"description\": \"%s\"}," +
            "{\"name\": \"%s\", \"basePrice\": %.2f, \"duration\": %d, \"description\": \"%s\"}," +
            "{\"name\": \"%s\", \"basePrice\": %.2f, \"duration\": %d, \"description\": \"%s\"}" +
            "]}",
            subject2.getName(), subject2.getBasePrice(), subject2.getDuration(), subject2.getDescription(),
            subject3.getName(), subject3.getBasePrice(), subject3.getDuration(), subject3.getDescription(),
            mock.getName(), mock.getBasePrice(), mock.getDuration(), mock.getDescription()
        );
    }

    private void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
