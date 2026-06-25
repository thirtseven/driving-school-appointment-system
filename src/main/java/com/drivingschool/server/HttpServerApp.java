package com.drivingschool.server;

import java.io.IOException;

public class HttpServerApp {
    public static void main(String[] args) {
        int port = 8080;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("无效的端口号，使用默认端口: 8080");
            }
        }

        try {
            HttpServerConfig serverConfig = HttpServerConfig.getInstance();
            serverConfig.startServer(port);

            System.out.println("\n按 Ctrl+C 停止服务器\n");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n正在停止服务器...");
                serverConfig.stopServer();
                System.out.println("服务器已停止");
            }));

            Thread.currentThread().join();
        } catch (IOException e) {
            System.err.println("服务器启动失败: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("服务器被中断");
        }
    }
}
