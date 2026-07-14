package org.TraceVerifier.IO;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Export trace queries to file. Requires adapters for exporting the UPPAAL query outcomes.
 */
public class TraceQueryIOJson<T> implements FileIO<T>, StringSerializer<T> {
    @Override
    public T Import(Path filepath, Class<T> object) {
        return null;
    }

    @Override
    public void Export(Path location, String filename, T object, boolean overwrite) throws IOException {
        Path file = Path.of(location.toAbsolutePath().toString(), filename);

        if (!Files.exists(location)) {
            Files.createDirectories(location);
        }

        if (!Files.exists(location) || (!overwrite && Files.exists(file))) {
            throw new Error("Location doesn't exist or file already exists.");
        }

        PrintWriter out = new PrintWriter(file.toString());
        out.print(serialize(object));
        out.close();
    }

    @Override
    public String serialize(T object) {
        Gson g = new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .serializeNulls()
                .registerTypeAdapter(java.awt.Color.class, new ColorAdapter())
                .registerTypeAdapter(Double.class, new DoubleAdapter())
                .create();
        return g.toJson(object);
    }

    @Override
    public T deserialize(String serializedObject, Class<T> objectType) {
        return null;
    }

    // Custom adapter for UPPAAL Colors. Color doesn't seem to import/export properly.
    private static class ColorAdapter implements JsonDeserializer<Color>, JsonSerializer<java.awt.Color> {
        @Override
        public java.awt.Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new java.awt.Color(0, 0, 0);
        }

        @Override
        public JsonElement serialize(java.awt.Color src, Type typeOfSrc, JsonSerializationContext context) {
            return null;
        }
    }

    // Custom adapter for UPPAAL doubles. Doubles don't import/export properly.
    private static class DoubleAdapter extends TypeAdapter<Double> {

        @Override
        public void write(JsonWriter jsonWriter, Double aDouble) throws IOException {
            if (aDouble == null) {
                jsonWriter.nullValue();
                return;
            }
            double dval = aDouble;
            if (Double.isNaN(dval) || Double.isInfinite(dval)) {
                String sval = aDouble.toString();
                jsonWriter.value(sval);
                return;
            }
            jsonWriter.value(aDouble);
        }

        @Override
        public Double read(JsonReader jsonReader) throws IOException {
            return 0.0;
        }
    }
}
