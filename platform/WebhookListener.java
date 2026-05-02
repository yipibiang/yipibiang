import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class WebhookListener {
    private static final String DOCKER_USER = System.getenv().getOrDefault("DOCKERHUB_USERNAME", "yipibiang");
    private static final String IMAGE_NAME = DOCKER_USER + "/user-service:latest";
    private static final String JWT_SECRET = System.getenv().getOrDefault("WEBHOOK_JWT_SECRET", "default_jwt_secret_change_me_32chars");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "mysql_db_pw");
    private static final String NETWORK = "platform_default";

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8888;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(5));
        server.createContext("/", new RootHandler());
        server.createContext("/deploy", new DeployHandler());
        server.start();

        System.out.println("========================================");
        System.out.println("  Webhook Listener Started");
        System.out.println("  Port: " + port);
        System.out.println("  Image: " + IMAGE_NAME);
        System.out.println("========================================");
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String resp = "{\"status\":\"ok\",\"endpoints\":[\"/deploy\"]}";
            sendJson(exchange, 200, resp);
        }
    }

    static class DeployHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String body = readRequestBody(exchange);
            System.out.println("\n[Deploy] Received request: " + body);

            String resp = "{\"code\":200,\"msg\":\"Deployment started\"}";
            sendJson(exchange, 200, resp);

            new Thread(new DeployTask(body)).start();
        }
    }

    static class DeployTask implements Runnable {
        private final String payload;

        DeployTask(String payload) {
            this.payload = payload;
        }

        @Override
        public void run() {
            try {
                System.out.println("\n========================================");
                System.out.println("  Starting Deployment");
                System.out.println("========================================");

                ensureNetwork();
                pullImage();
                stopContainer();
                removeContainer();
                startContainer();

                System.out.println("\n========================================");
                System.out.println("  Deployment Complete!");
                System.out.println("========================================\n");
            } catch (Exception e) {
                System.err.println("[Deploy] ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void ensureNetwork() throws Exception {
            exec(Arrays.asList("docker", "network", "inspect", NETWORK));
            if (exec(Arrays.asList("docker", "network", "create", NETWORK)) != 0) {
                System.out.println("[Deploy] Network already exists or created");
            }
        }

        private void pullImage() throws Exception {
            System.out.println("[Deploy] Pulling image: " + IMAGE_NAME);
            int code = exec(Arrays.asList("docker", "pull", IMAGE_NAME));
            if (code != 0) {
                throw new RuntimeException("Failed to pull image");
            }
            System.out.println("[Deploy] Image pulled successfully");
        }

        private void stopContainer() throws Exception {
            System.out.println("[Deploy] Stopping container...");
            exec(Arrays.asList("docker", "stop", "user-service"));
            System.out.println("[Deploy] Container stopped");
        }

        private void removeContainer() throws Exception {
            System.out.println("[Deploy] Removing container...");
            exec(Arrays.asList("docker", "rm", "user-service"));
            System.out.println("[Deploy] Container removed");
        }

        private void startContainer() throws Exception {
            System.out.println("[Deploy] Starting new container...");

            List<String> cmd = new ArrayList<>(Arrays.asList(
                "docker", "run", "-d",
                "--name", "user-service",
                "--network", NETWORK,
                "-p", "8081:8081",
                "-e", "JWT_SECRET=" + JWT_SECRET,
                "-e", "DB_HOST=mysql",
                "-e", "DB_USERNAME=root",
                "-e", "DB_PASSWORD=" + DB_PASSWORD,
                "-e", "DB_NAME=monorepo"
            ));
            cmd.add(IMAGE_NAME);

            int code = exec(cmd);
            if (code != 0) {
                throw new RuntimeException("Failed to start container");
            }
            System.out.println("[Deploy] Container started successfully");
        }

        private int exec(List<String> cmd) throws Exception {
            System.out.println("[Exec] " + String.join(" ", cmd));

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("  | " + line);
            }

            int exitCode = process.waitFor();
            System.out.println("[Exec] Exit code: " + exitCode);
            return exitCode;
        }
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    private static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = json.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}