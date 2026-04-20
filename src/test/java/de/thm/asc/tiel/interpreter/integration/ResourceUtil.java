package de.thm.asc.tiel.interpreter.integration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ResourceUtil {

    private static String readResourceAsString(String resourceName) {

        try (var inputStream = ResourceUtil.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourceName);
            }
            try (var scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
                scanner.useDelimiter("\\A");
                return scanner.hasNext() ? scanner.next() : "";
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading resource", e);
        }
    }

    public static String getIntegrationTestFile(String name) {
        return readResourceAsString("integration_tests/" + name);
    }
}

