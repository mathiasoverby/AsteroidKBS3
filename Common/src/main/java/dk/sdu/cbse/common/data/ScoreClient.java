package dk.sdu.cbse.common.data;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ScoreClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String url = "http://localhost:8081/score";


    public int updateScore(int increment) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "?increment=" + increment))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body().isEmpty() ? 0 : Integer.parseInt(response.body());
    }

}
