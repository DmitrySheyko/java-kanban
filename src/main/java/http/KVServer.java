package http;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class KVServer {
    public static final int PORT = 8078;
    private final String apiToken;
    private HttpServer server = null;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() {
        apiToken = generateApiToken();
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        } catch (IOException e) {
            System.out.println("Ошибка при создании InetSocketAddress и открытии порта \n" + e.getMessage());
        }
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);

    }

    private void load(HttpExchange httpExchange) {
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            if (!hasAuth(httpExchange)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                httpExchange.sendResponseHeaders(401, 0);
                return;
            }
            if ("GET".equals(httpExchange.getRequestMethod())) {
                String key = httpExchange.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для получения пустой. key указывается в пути: /load/{key}");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                if (data.containsKey(key)) {
                    String valueForResponse = data.get(key);
                    httpExchange.sendResponseHeaders(200, 0);
                    outputStream.write(valueForResponse.getBytes());
                } else {
                    httpExchange.sendResponseHeaders(404, 0);
                }
            } else {
                System.out.println("/load ждёт GET-запрос, а получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            System.out.println("Ошибка открытия OutPutStream в ходе работы метода load() \n" + e.getMessage());
            try {
                httpExchange.sendResponseHeaders(500, 0);
            } catch (IOException ex) {
                System.out.println("Ошибка отправки данных в методе load()\n" + e.getMessage());
            }
        } finally {
            httpExchange.close();
        }
    }

    private void save(HttpExchange httpExchange) {
        try {
            if (!hasAuth(httpExchange)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                httpExchange.sendResponseHeaders(401, 0);
                return;
            }
            if ("POST".equals(httpExchange.getRequestMethod())) {
                String key = httpExchange.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(httpExchange);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            System.out.println("Ошибка в ходе отправки статуса запроса в методе save() \n" + e.getMessage());
            try {
                httpExchange.sendResponseHeaders(500, 0);
            } catch (IOException ex) {
                System.out.println("Ошибка отправки данных в методе save()\n" + e.getMessage());
            }
        } finally {
            httpExchange.close();
        }
    }

    private void register(HttpExchange httpExchange) {
        try {
            if ("GET".equals(httpExchange.getRequestMethod())) {
                sendText(httpExchange, apiToken);
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            System.out.println("Ошибка в ходе работы метода register() \n" + e.getMessage());
            try {
                httpExchange.sendResponseHeaders(500, 0);
            } catch (IOException ex) {
                System.out.println("Ошибка отправки данных в методе register() \n" + e.getMessage());
            }
        } finally {
            httpExchange.close();
        }
    }

    public void start() {
        if (server != null) {
            System.out.println("Запущен сервер на порту " + PORT);
            System.out.println("API_TOKEN: " + apiToken);
            server.start();
        }
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange httpExchange) {
        String rawQuery = httpExchange.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, resp.length);
        httpExchange.getResponseBody().write(resp);
    }

    public void stop() {
        server.stop(0);
    }
}
