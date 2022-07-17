package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client;
    private final URI UrlOfKvServer;
    private final String API_TOKEN;

    public KVTaskClient() throws IOException, InterruptedException {
        this.client = HttpClient.newHttpClient();
        this.UrlOfKvServer = URI.create("http://localhost:8078");
        this.API_TOKEN = makeRegistration();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI url = URI.create(UrlOfKvServer + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String load(String key) throws IOException, InterruptedException {
        URI url = URI.create(UrlOfKvServer + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String makeRegistration() throws IOException, InterruptedException {
        URI url = URI.create(UrlOfKvServer + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
