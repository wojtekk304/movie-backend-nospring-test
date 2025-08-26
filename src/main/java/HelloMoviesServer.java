import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HelloMoviesServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new RootHandler());
        server.setExecutor(null); // default executor
        System.out.println("HTTP server running on http://localhost:" + port);
        server.start();
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            // CORS headers for frontend on :5500
            Headers h = exchange.getResponseHeaders();
            h.add("Access-Control-Allow-Origin", "*");
            h.add("Access-Control-Allow-Methods", "GET, OPTIONS");
            h.add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(method)) {
                // Preflight response
                exchange.sendResponseHeaders(204, -1); // No Content
                exchange.close();
                return;
            }

            if ("GET".equalsIgnoreCase(method) && "/".equals(exchange.getRequestURI().getPath())) {
                String body = "Hello Movies";
                h.add("Content-Type", "text/plain; charset=utf-8");
                byte[] bytes = body.getBytes("UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                String body = "Not Found";
                h.add("Content-Type", "text/plain; charset=utf-8");
                byte[] bytes = body.getBytes("UTF-8");
                exchange.sendResponseHeaders(404, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
        }
    }
}
