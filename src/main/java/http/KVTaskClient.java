package http;

import exceptions.KVTaskClientException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client;
    private final URI urlOfKvServer;
    private final String apiToken;

    public KVTaskClient(String url) throws KVTaskClientException {
        this.client = HttpClient.newHttpClient();
        this.urlOfKvServer = URI.create(url);
        this.apiToken = makeRegistration();
    }

    public HttpResponse<String> put(String keyForSave, String json) throws KVTaskClientException {
        URI url = URI.create(urlOfKvServer + "/save/" + keyForSave + "?API_TOKEN=" + apiToken);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new KVTaskClientException("IOException в ходе работы метода put()");
        } catch (InterruptedException e) {
            throw new KVTaskClientException("InterruptedException в ходе работы метода put()");
        }
    }

    public HttpResponse<String> load(String keyForSave) throws KVTaskClientException {
        URI url = URI.create(urlOfKvServer + "/load/" + keyForSave + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new KVTaskClientException("IOException в ходе работы метода load()");
        } catch (InterruptedException e) {
            throw new KVTaskClientException("InterruptedException в ходе работы метода load()");
        }
    }

    public String makeRegistration() throws KVTaskClientException {
        URI url = URI.create(urlOfKvServer + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new KVTaskClientException("IOException в ходе работы метода makeRegistration()");
        } catch (InterruptedException e) {
            throw new KVTaskClientException("InterruptedException в ходе работы метода makeRegistration()");
        }
        return response.body();
    }
}
