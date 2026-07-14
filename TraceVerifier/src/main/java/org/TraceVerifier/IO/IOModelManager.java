package org.TraceVerifier.IO;

import org.TraceVerifier.Query.TraceQuery;
import org.TraceVerifier.Query.TraceQueryCollection;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


public class IOModelManager implements IOManager {
    private static volatile IOModelManager instance;
    private final Map<Class<?>, FileIO<?>> IOMapping = new HashMap<>();

    private IOModelManager() {
        initIO();
    }

    public static IOManager getInstance() {
        if (instance == null) {
            instance = new IOModelManager();
            instance.initIO();
        }
        return instance;
    }

    private void initIO() {
        // Standard I/O is json
        IOMapping.put(Object.class, new JsonIO<>());
        // Trace queries have their own handling due to redefinition of some gson classes.
        IOMapping.put(TraceQuery.class, new TraceQueryIOJson<TraceQuery>());
        IOMapping.put(TraceQueryCollection.class, new TraceQueryIOJson<TraceQueryCollection>());
    }

    /**
     * Import object from file. We search for exact match first, then interface, then default IO manager.
     */
    @Override
    public <T> T Import(Path filepath, Class<T> objectType) throws IOException {
        String strFilepath = filepath.toAbsolutePath().toString();
        // Check for exact match
        if (IOMapping.containsKey(objectType)) {
            FileIO<T> IOManager = (FileIO<T>) IOMapping.get(objectType);
            return IOManager.Import(filepath, objectType);
        }

        // Check for interface match
        for (Class<?> interfaces : objectType.getInterfaces()) {
            if (IOMapping.containsKey(interfaces)) {
                FileIO<T> IOManager = (FileIO<T>) IOMapping.get(interfaces);
                return IOManager.Import(filepath, objectType);
            }
        }

        // Check for default IO
        if (IOMapping.containsKey(Object.class)) {
            FileIO<T> IOManager = (FileIO<T>) IOMapping.get(Object.class);
            return IOManager.Import(filepath, objectType);
        }

        return null;
    }

    /**
     * Export object to file. We search for exact match first, then interface, then default IO manager.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> void Export(Path filepath, String filename, T object, boolean overwrite) throws IOException {
        Class<?> classExport = object.getClass();

        // Check for exact match
        if (IOMapping.containsKey(object.getClass())) {
            FileIO<T> fileIO = (FileIO<T>) IOMapping.get(classExport);
            fileIO.Export(filepath, filename, object, overwrite);
            return;
        }

        // Check for interface match
        for (Class<?> interfaces : object.getClass().getInterfaces()) {
            if (IOMapping.containsKey(interfaces)) {
                FileIO<T> ioManager = (FileIO<T>) IOMapping.get(interfaces);
                ioManager.Export(filepath, filename, object, overwrite);
                return;
            }
        }

        // Check for default IO
        if (IOMapping.containsKey(Object.class)) {
            FileIO<T> ioManager = (FileIO<T>) IOMapping.get(Object.class);
            ioManager.Export(filepath, filename, object, overwrite);
            return;
        }

        throw new IOException();
    }
}
