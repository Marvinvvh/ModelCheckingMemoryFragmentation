package org.TraceVerifier.IO;

public interface StringSerializer<T> {
    public String serialize(T object);

    public T deserialize(String serializedObject, Class<T> objectType);
}
