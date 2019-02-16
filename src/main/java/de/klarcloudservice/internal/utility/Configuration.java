package de.klarcloudservice.internal.utility;

import java.lang.reflect.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;

public final class Configuration
{
    protected JsonObject jsonObject;
    public static final Gson GSON;
    public static final JsonParser PARSER;

    public Configuration() {
        this.jsonObject = new JsonObject();
    }

    public Configuration(final JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Configuration addStringProperty(final String key, final String value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public Configuration addIntegerProperty(final String key, final Integer value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public Configuration addBooleanProperty(final String key, final Boolean value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public Configuration addConfigurationProperty(final String key, final Configuration value) {
        this.jsonObject.add(key, value.jsonObject);
        return this;
    }

    public Configuration addProperty(final String key, final Object value) {
        this.jsonObject.add(key, Configuration.GSON.toJsonTree(value));
        return this;
    }

    public Configuration remove(final String key) {
        this.jsonObject.remove(key);
        return this;
    }

    public String getStringValue(final String key) {
        return this.jsonObject.has(key) ? this.jsonObject.get(key).getAsString() : "null";
    }

    public int getIntegerValue(final String key) {
        return this.jsonObject.has(key) ? this.jsonObject.get(key).getAsInt() : 0;
    }

    public boolean getBooleanValue(final String key) {
        return this.jsonObject.has(key) && this.jsonObject.get(key).getAsBoolean();
    }

    public <T> T getValue(final String key, final Class<T> clazz) {
        return this.jsonObject.has(key) ? Configuration.GSON.fromJson(this.jsonObject.get(key), clazz) : null;
    }

    public <T> T getValue(final String key, final Type type) {
        return this.jsonObject.has(key) ? Configuration.GSON.fromJson(this.jsonObject.get(key), type) : null;
    }

    public Configuration getConfiguration(final String key) {
        return this.jsonObject.has(key) ? new Configuration(this.jsonObject.get(key).getAsJsonObject()) : null;
    }

    private boolean saveAsConfigurationFile(final File backend) {
        if (backend.exists()) {
            backend.delete();
        }
        try (final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(backend), StandardCharsets.UTF_8.name())) {
            Configuration.GSON.toJson(this.jsonObject, writer);
            return true;
        }
        catch (IOException ex) {
            ex.getStackTrace();
            return false;
        }
    }

    public boolean saveAsConfigurationFile(final Path path) {
        return this.saveAsConfigurationFile(path.toFile());
    }

    public static Configuration loadConfiguration(final File file) {
        try (final InputStreamReader reader = new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8.name());
             final BufferedReader bufferedReader = new BufferedReader(reader)) {
            return new Configuration(Configuration.PARSER.parse(bufferedReader).getAsJsonObject());
        }
        catch (IOException ex) {
            ex.getStackTrace();
            return new Configuration();
        }
    }

    public static Configuration loadConfiguration(final Path path) {
        return loadConfiguration(path.toFile());
    }

    @Deprecated
    public Configuration clear() {
        this.jsonObject.entrySet().forEach(jsonObject -> this.remove(jsonObject.getKey()));
        return this;
    }

    public String getJsonString() {
        return this.jsonObject.toString();
    }

    public boolean contains(final String key) {
        return this.jsonObject.has(key);
    }

    public JsonObject getJsonObject() {
        return this.jsonObject;
    }

    public void setJsonObject(final JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    static {
        GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();
        PARSER = new JsonParser();
    }
}
