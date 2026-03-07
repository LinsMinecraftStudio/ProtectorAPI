package me.mmmjjkx.protectorapi;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModrinthUpdateChecker {
    private final JavaPlugin plugin;
    private final String projectId;
    private final Gson gson = new Gson();

    public ModrinthUpdateChecker(JavaPlugin plugin, String projectId) {
        this.plugin = plugin;
        this.projectId = projectId;
    }

    public void check() {
        CompletableFuture.runAsync(() -> {
            try {
                String currentVersion = plugin.getDescription().getVersion();
                String url = buildUrl();

                HttpURLConnection connection =
                        (HttpURLConnection) new URL(url).openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", plugin.getName());
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int code = connection.getResponseCode();
                if (code != 200) {
                    plugin.getLogger().warning("Update check failed (HTTP " + code + ")");
                    return;
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Type listType = new TypeToken<List<ModrinthVersion>>() {}.getType();
                List<ModrinthVersion> versions =
                        gson.fromJson(response.toString(), listType);

                if (versions == null || versions.isEmpty()) {
                    plugin.getLogger().info("No updates found.");
                    return;
                }

                versions.sort(Comparator.comparing(v -> v.datePublished));
                ModrinthVersion latest = versions.get(versions.size() - 1);

                if (isNewer(latest.versionNumber, currentVersion)) {
                    plugin.getLogger().info("New version available!");
                    plugin.getLogger().info("Current: §c" + currentVersion);
                    plugin.getLogger().info("Latest:  §a" + latest.versionNumber);
                    plugin.getLogger().info("Download it here: https://modrinth.com/plugin/" + projectId);
                } else {
                    plugin.getLogger().info("Plugin is up to date.");
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Update check error: " + e.getMessage());
            }
        });
    }

    private String buildUrl() throws Exception {
        String loadersJson = "[\"spigot, paper\"]";
        String versionsJson = "[\"" + getMinecraftVersion() + "\"]";

        return "https://api.modrinth.com/v2/project/" + projectId + "/version"
                + "?loaders=" + URLEncoder.encode(loadersJson, "UTF-8")
                + "&game_versions=" + URLEncoder.encode(versionsJson, "UTF-8");
    }

    private String getMinecraftVersion() {
        Pattern pattern = Pattern.compile("\\(MC: ([^)]+)\\)");
        Matcher matcher = pattern.matcher(Bukkit.getVersion());

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "unknown";
    }

    private boolean isNewer(String latest, String current) {
        String[] l = normalize(latest);
        String[] c = normalize(current);

        int max = Math.max(l.length, c.length);

        for (int i = 0; i < max; i++) {
            int li = i < l.length ? parseInt(l[i]) : 0;
            int ci = i < c.length ? parseInt(c[i]) : 0;

            if (li > ci) return true;
            if (li < ci) return false;
        }
        return false;
    }

    private String[] normalize(String v) {
        return v.replace("v", "")
                .replace("-SNAPSHOT", "")
                .split("\\.");
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private static class ModrinthVersion {
        @SerializedName("version_number")
        String versionNumber;

        @SerializedName("date_published")
        OffsetDateTime datePublished;
    }
}