package pl.zb3;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class NativeLoader {
    private static Set<String> loadedLibraries = new HashSet<>();

    public static void loadLibrary(String libraryName) {
        if (loadedLibraries.contains(libraryName)) {
            return;
        }

        String osName = System.getProperty("os.name");
        String arch = System.getProperty("os.arch");
        String libraryPath = String.format("natives/%s/%s/%s", osName, arch, System.mapLibraryName(libraryName));

        InputStream libraryStream = NativeLoader.class.getClassLoader().getResourceAsStream(libraryPath);

        if (libraryStream == null) {
            throw new RuntimeException("Native library not found in JAR: " + libraryPath);
        }

        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            File tempLibraryFile = new File(tempDir, libraryName);

            try (OutputStream os = new FileOutputStream(tempLibraryFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = libraryStream.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            System.load(tempLibraryFile.getAbsolutePath());
            tempLibraryFile.deleteOnExit();
            loadedLibraries.add(libraryName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load native library: " + e.getMessage());
        }
    }
}