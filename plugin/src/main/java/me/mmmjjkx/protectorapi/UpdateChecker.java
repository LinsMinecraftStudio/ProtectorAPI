package me.mmmjjkx.protectorapi;

import com.google.gson.Gson;
import io.github.lijinhong11.protectorapi.ProtectorAPI;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import org.bukkit.plugin.java.JavaPlugin;

class UpdateChecker {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void check() {
        JavaPlugin plugin = ProtectorAPI.getPluginHost();
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
                                .warning("There is a new version of ProtectorAPI available! (Current: %s, Latest: %s)"
                                        .formatted(ver, result.name));
                        plugin.getLogger()
                                .warning("Download it here: https://modrinth.com/plugin/protectorapi/versions/"
                                        + result.name);
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

        public CheckResult(int downloads, String name, long releaseDate, int resource, String uuid, int id) {
            this.downloads = downloads;
            this.name = name;
            this.releaseDate = releaseDate;
            this.resource = resource;
            this.uuid = uuid;
            this.id = id;
        }
    }
}
