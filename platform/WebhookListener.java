import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class WebhookListener {

    private static final String DOCKERHUB_USERNAME = System.getenv().getOrDefault("DOCKERHUB_USERNAME", "yipibiang");
    private static final String IMAGE_NAME = DOCKERHUB_USERNAME + "/user-service:latest";

    public static void main(String[] args) throws IOException {
        int port = 8888;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        server.createContext("/deploy", new DeployHandler());
        server.createContext("/", new RootHandler());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[Webhook] Shutting down...");
            server.stop(0);
        }));

        server.setExecutor(null);
        server.start();
        System.out.println("[Webhook] Listening on localhost:" + port);
        System.out.println("[Webhook] Deploy endpoint: POST http://localhost:" + port + "/deploy");
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"status\":\"ok\",\"endpoints\":[\"/deploy\"]}";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class DeployHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            try {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes());
                is.close();

                System.out.println("[Deploy] Received deploy request");
                System.out.println("[Deploy] Body: " + body);

                System.out.println("[Deploy] Pulling image: " + IMAGE_NAME);
                ProcessBuilder pullPB = new ProcessBuilder("docker", "pull", IMAGE_NAME);
                pullPB.inheritIO();
                Process pullProcess = pullPB.start();
                int pullExitCode = pullProcess.waitFor();

                if (pullExitCode != 0) {
                    System.out.println("[Deploy] Warning: docker pull failed with exit code " + pullExitCode);
                }

                System.out.println("[Deploy] Stopping old container...");
                ProcessBuilder stopPB = new ProcessBuilder("docker", "stop", "user-service");
                stopPB.redirectErrorStream(true);
                Process stopProcess = stopPB.start();
                stopProcess.waitFor();

                System.out.println("[Deploy] Removing old container...");
                ProcessBuilder rmPB = new ProcessBuilder("docker", "rm", "user-service");
                rmPB.redirectErrorStream(true);
                Process rmProcess = rmPB.start();
                rmProcess.waitFor();

                System.out.println("[Deploy] Starting new container...");
                ProcessBuilder runPB = new ProcessBuilder(
                    "docker", "run", "-d", "--name", "user-service",
                    "--network", "platform_default",
                    "-p", "8081:8081",
                    "-e", "JWT_SECRET=your_jwt_secret_here_min_32_chars",
                    "-e", "DB_HOST=mysql",
                    "-e", "DB_USERNAME=root",
                    "-e", "DB_PASSWORD=mysql_db_pw",
                    "-e", "DB_NAME=monorepo",
                    IMAGE_NAME
                );
                runPB.inheritIO();
                Process runProcess = runPB.start();
                runProcess.waitFor();

                System.out.println("[Deploy] Done!");

                String response = "{\"status\":\"deployed\"}";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

            } catch (Exception e) {
                System.out.println("[Error] " + e.getMessage());
                e.printStackTrace();

                String response = "{\"error\":\"internal\"}";
                exchange.sendResponseHeaders(500, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}
