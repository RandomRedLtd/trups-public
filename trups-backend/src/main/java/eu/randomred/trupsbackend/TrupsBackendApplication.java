package eu.randomred.trupsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class TrupsBackendApplication {

    public static void main(String[] args) {
        bootstrap();
        SpringApplication.run(TrupsBackendApplication.class, args);
    }

    private static void bootstrap() {
        Path path = Path.of("trups-data");

        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException("Bootstrap failed, error: " + e.getMessage());
            }
        }
    }

}
