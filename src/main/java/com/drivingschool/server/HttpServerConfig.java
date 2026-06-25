package com.drivingschool.server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpServerConfig {
    private static HttpServerConfig instance;
    private HttpServer server;
    private static final int DEFAULT_PORT = 8080;

    private HttpServerConfig() {}

    public static HttpServerConfig getInstance() {
        if (instance == null) {
            synchronized (HttpServerConfig.class) {
                if (instance == null) {
                    instance = new HttpServerConfig();
                }
            }
        }
        return instance;
    }

    public void startServer(int port) throws IOException {
        if (server != null) {
            System.out.println("服务器已在运行中");
            return;
        }

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(10));

        registerHandlers();

        server.start();
        System.out.println("========================================");
        System.out.println("  驾校预约系统 Web 服务器已启动");
        System.out.println("  访问地址: http://localhost:" + port);
        System.out.println("========================================");
    }

    public void startServer() throws IOException {
        startServer(DEFAULT_PORT);
    }

    public void stopServer() {
        if (server != null) {
            server.stop(0);
            server = null;
            System.out.println("服务器已停止");
        }
    }

    private void registerHandlers() {
        server.createContext("/api/appointments", new AppointmentHandler());
        server.createContext("/api/courses", new CourseHandler());
        server.createContext("/api/students", new StudentHandler());
        server.createContext("/api/statistics", new StatisticsHandler());
        server.createContext("/", new StaticFileHandler());
    }

    public HttpServer getServer() {
        return server;
    }
}
