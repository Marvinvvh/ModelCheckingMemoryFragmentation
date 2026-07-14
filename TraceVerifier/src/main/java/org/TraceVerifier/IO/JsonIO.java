package org.TraceVerifier.IO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.TraceVerifier.Trace.TraceAction;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonIO<T> implements FileIO<T>, StringSerializer<T> {

    @Override
    public T Import(Path filepath, Class<T> object) throws IOException {
        if (!Files.exists(filepath)) {
            throw new IOException();
        }
        return deserialize(Files.readString(filepath), object);
    }

    @Override
    public void Export(Path filepath, String filename, T object, boolean overwrite) throws IOException {
        Path file = Path.of(filepath.toAbsolutePath().toString(), filename);

        if (!Files.exists(filepath)) {
            Files.createDirectories(filepath);
        }

        if (!Files.exists(filepath) || (!overwrite && Files.exists(file))) {
            throw new Error("Location doesn't exist or file already exists.");
        }

        PrintWriter out = new PrintWriter(file.toString());
        out.print(serialize(object));
        out.close();
    }


    @Override
    public String serialize(T object) {
        Gson g = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return g.toJson(object);
    }

    @Override
    public T deserialize(String serializedObject, Class<T> objectType) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(TraceAction.class, new TraceActionDeserializer())
                .create();
        return gson.fromJson(serializedObject, objectType);
    }
}
