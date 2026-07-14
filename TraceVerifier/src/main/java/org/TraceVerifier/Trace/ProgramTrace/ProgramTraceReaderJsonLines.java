package org.TraceVerifier.Trace.ProgramTrace;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.TraceVerifier.Trace.*;
import org.TraceVerifier.Util.File.DirectoryPatternSearch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Program for reading program traces from a JSONL file.
 */
public class ProgramTraceReaderJsonLines implements ProgramTraceReader {

    @Override
    public Trace fromFile(Path filepath) {
        Trace trace = new Trace();
        try (BufferedReader lineReader = new BufferedReader(new FileReader(filepath.toString()))) {
            Gson g = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

            // A sorted treemap is used to keep a sorted set of id keys of all actions.
            // This allows for processing the actions in order of their id without rearranging the file.
            SortedMap<Integer, JsonObject> jsonObjects = new TreeMap<>();
            int maxID = 0;
            for (var line : lineReader.lines().toList()) {
                JsonObject jsonObject = g.fromJson(line, JsonObject.class);
                int objectID = jsonObject.get("id").getAsInt();
                if (jsonObjects.containsKey(objectID)) {
                    throw new RuntimeException("Invalid trace. Missing identifiers.");
                }

                maxID = Math.max(objectID, maxID);
                jsonObjects.put(objectID, jsonObject.deepCopy());
            }

            // Use '+ 1' as id encompass a range of 0 up to N - 1 (inclusive), where N is the number of actions.
            if (jsonObjects.size() != maxID + 1) {
                throw new RuntimeException("Invalid trace. Missing identifiers.");
            }

            for (Integer integer : jsonObjects.keySet()) {
                JsonObject jsonObject = jsonObjects.get(integer);
                var action = processAction(jsonObject);
                trace.append(action);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Remove anything after the first found '.'.
        String strippedFileName = filepath.getFileName().toString().replaceFirst("[.][^.]+$", "");
        trace.setIdentifier(strippedFileName);
        return trace;
    }

    @Override
    public TraceGroup fromDirectory(TraceGroupIdentifier groupIdentifier, Path dirpath, String matchingPattern) throws IOException {
        TraceGroup traceGroup = new TraceGroup(groupIdentifier);
        List<Path> paths = DirectoryPatternSearch.searchFiles(dirpath, matchingPattern);
        for (var path : paths) {
            Trace t = fromFile(path);
            traceGroup.addTrace(t);
        }

        return traceGroup;
    }

    private TraceAction processAction(JsonObject jsonObject) {
        String type = jsonObject.get("type").getAsString();
        TraceAction action = null;
        ProgramTraceActions actionType = ProgramTraceActions.valueOf(type);
        switch (actionType) {
            // Parse alloc line
            case ProgramTraceActions.Alloc -> {
                JsonElement pointerObj = jsonObject.get("pointer");
                JsonElement sizeObj = jsonObject.get("size");
                if (pointerObj == null || sizeObj == null) {
                    throw new RuntimeException("Invalid program trace.");
                }

                int pointer = pointerObj.getAsInt();
                int size = sizeObj.getAsInt();
                action = new TraceActionAlloc(pointer, size);
            }
            // Parse free line
            case ProgramTraceActions.Free -> {
                JsonElement pointerObj = jsonObject.get("pointer");
                if (pointerObj == null) {
                    throw new RuntimeException("Invalid program trace.");
                }

                int pointer = pointerObj.getAsInt();
                action = new TraceActionFree(pointer);
            }
        }
        return action;
    }
}
