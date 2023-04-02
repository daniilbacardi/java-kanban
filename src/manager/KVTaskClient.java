package manager;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final URL url;
    private final HttpClient client;
    private final String apiToken;

    public KVTaskClient(URL url) {
        this.url = url;
        client = HttpClient.newHttpClient();
        apiToken = register();
    }

    private String register() {
        URI uri = URI.create(url + "/register");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> httpResponse = null;
        try {
            httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при регистрации - метод register()" + e.getMessage());
        }
        assert httpResponse != null;
        if (httpResponse.body() == null) {
            throw new RuntimeException("Отсутствует тело ответа в методе register()");
        }
        return httpResponse.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка в методе put()" + e.getMessage());
        }
    }

    public String load(String key) {
        URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Accept", "text/html").GET().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка в методе load()" + e.getMessage());
        }
        assert response != null;
        if (response.body() == null) {
            throw new RuntimeException("Отсутствует тело ответа в методе load()");
        }
        return response.body();
    }
}
