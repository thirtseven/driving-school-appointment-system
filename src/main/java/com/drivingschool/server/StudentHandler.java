package com.drivingschool.server;

import com.drivingschool.model.Student;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StudentHandler implements HttpHandler {
    private static final List<Student> students = new ArrayList<>();

    static {
        students.add(new Student("STU001", "张三", "13800138001", Student.StudentType.STUDENT));
        students.add(new Student("STU002", "李四", "13800138002", Student.StudentType.REGULAR));
        students.add(new Student("STU003", "王五", "13800138003", Student.StudentType.GROUP));
        students.add(new Student("STU004", "赵六", "13800138004", Student.StudentType.STUDENT));
    }

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
            response = getStudentsJson();
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

    private String getStudentsJson() {
        StringBuilder json = new StringBuilder("{\"students\": [");
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            if (i > 0) json.append(",");
            json.append(String.format(
                "{\"id\": \"%s\", \"name\": \"%s\", \"phone\": \"%s\", \"type\": \"%s\"}",
                s.getId(), s.getName(), s.getPhone(), s.getType().name()
            ));
        }
        json.append("], \"count\": ").append(students.size()).append("}");
        return json.toString();
    }

    private void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
