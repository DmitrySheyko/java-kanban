package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client;
    private final URI urlOfKvServer;
    private final String apiToken;

    public KVTaskClient(String url) throws IOException, InterruptedException {
        this.client = HttpClient.newHttpClient();
        this.urlOfKvServer = URI.create(url);
        this.apiToken = makeRegistration();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI url = URI.create(urlOfKvServer + "/save/" + key + "?API_TOKEN=" + apiToken);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String load(String key) throws IOException, InterruptedException {
        URI url = URI.create(urlOfKvServer + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String makeRegistration() throws IOException, InterruptedException {
        URI url = URI.create(urlOfKvServer + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
