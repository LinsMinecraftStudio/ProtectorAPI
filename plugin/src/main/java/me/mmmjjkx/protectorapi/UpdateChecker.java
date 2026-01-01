package me.mmmjjkx.protectorapi;

import com.google.gson.Gson;
import io.github.lijinhong11.protector.api.ProtectionAPIPlugin;
import io.github.lijinhong11.protector.api.ProtectorAPI;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void check() {
        ProtectionAPIPlugin plugin = ProtectorAPI.getPluginHost();
        String ver = plugin.getDescription().getVersion();
        if (ver.contains("-SNAPSHOT")) {
            return;
        }

        try {
            HttpRequest get = HttpRequest.newBuilder()
                    .uri(new URI("https://api.spiget.org/v2/resources/126828/versions/latest"))
                    .GET()
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    client.sendAsync(get, HttpResponse.BodyHandlers.ofString());
            response.thenAcceptAsync(res -> {
                CheckResult result = new Gson().fromJson(res.body(), CheckResult.class);
                if (result != null) {
                    if (!ver.equals(result.name)) {
                        plugin.getLogger()
                                .warning("There is a new version of ProtectorAPI available! (Current: "
                                        + ver + ", New: " + result.name + ")");
                    }
                }
            });
        } catch (URISyntaxException ignored) {
        }
    }

    public static class CheckResult {
        public int downloads;
        public String name;
        public long releaseDate;
        public int resource;
        public String uuid;
        public int id;

        public CheckResult() {}

        public CheckResult(
                int downloads, String name, long releaseDate, int resource, String uuid, int id) {
            this.downloads = downloads;
            this.name = name;
            this.releaseDate = releaseDate;
            this.resource = resource;
            this.uuid = uuid;
            this.id = id;
        }
    }
}
