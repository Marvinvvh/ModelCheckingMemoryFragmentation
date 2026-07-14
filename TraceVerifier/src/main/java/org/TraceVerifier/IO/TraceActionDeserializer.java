package org.TraceVerifier.IO;

import com.google.gson.*;
import org.TraceVerifier.Trace.TraceAction;
import org.TraceVerifier.Trace.TraceActionAlloc;
import org.TraceVerifier.Trace.TraceActionFree;

import java.lang.reflect.Type;

/**
 * Deserializer for trace actions saved as files.
 */
public class TraceActionDeserializer implements JsonDeserializer<TraceAction> {

    @Override
    public TraceAction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        String retrievedActionType = jsonObj.get("type").getAsString();
        TraceAction.Type traceActionType = TraceAction.Type.valueOf(retrievedActionType);
        switch (traceActionType) {
            case eAlloc -> {
                int pointer = jsonObj.get("pointer").getAsInt();
                int size = jsonObj.get("size").getAsInt();

                return new TraceActionAlloc(pointer, size);
            }
            case eFree -> {
                int pointer = jsonObj.get("pointer").getAsInt();
                return new TraceActionFree(pointer);
            }
        }
        throw new JsonParseException("Invalid trace action");
    }
}
