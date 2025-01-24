package org;

import java.io.*;
import java.util.Base64;

public class SerializeToBase64 {

    public static String serializeObjectToBase64(Object obj) throws IOException {
        // Serializarea obiectului intr-un array de bytes
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(obj);
        }

        // transformarea din array de bytes in string base64
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    public static Object deserializeObjectFromBase64(String base64) throws IOException, ClassNotFoundException {
        // transformarea din base64 string in bytes array
        byte[] data = Base64.getDecoder().decode(base64);

        // deserealizare
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return objectInputStream.readObject();
        }
    }
}