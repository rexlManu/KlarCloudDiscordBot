package de.klarcloudservice.internal.gitlab.API;

import com.google.gson.*;
import java.net.*;
import java.nio.charset.*;
import com.google.gson.stream.*;
import java.util.*;
import java.io.*;

public class GitLabAPI
{
    private final JsonParser jsonParser;

    public GitLabAPI() {
        this.jsonParser = new JsonParser();
    }

    public String getProject(final String key) {
        try {
            final URL apiUrl = new URL("https://gitlab.com/api/v4/projects/" + key);
            final HttpURLConnection connection = (HttpURLConnection)apiUrl.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            try (final JsonReader jsonReader = new JsonReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                return this.jsonParser.parse(jsonReader).getAsJsonObject() + "";
            }
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public void openIssue(final String key, final String token, final String name, final List<String> labels, final String description) {
        final StringBuilder stringBuilder = new StringBuilder();
        labels.forEach(e -> stringBuilder.append(e).append(","));
        final StringBuilder stringBuilder2 = new StringBuilder();
        Arrays.stream(description.split(" ")).forEach(e -> stringBuilder2.append(e).append("+"));
        try {
            final URL apiUrl = new URL("https://gitlab.com/api/v4/projects/" + key + "/issues?title=" + name + "&labels=" + stringBuilder.substring(0, stringBuilder.length() - 1) + "&description=" + stringBuilder2.substring(0, stringBuilder2.length() - 1));
            final HttpURLConnection connection = (HttpURLConnection)apiUrl.openConnection();
            connection.setRequestProperty("PRIVATE-TOKEN", token);
            connection.setRequestProperty("Application-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            try (final InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                final StringBuilder sb = new StringBuilder();
                final char[] chars = new char[4096];
                int len;
                while ((len = inputStreamReader.read(chars)) >= 0) {
                    sb.append(chars, 0, len);
                }
                System.out.println(sb.substring(0));
            }
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
