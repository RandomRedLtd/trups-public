package eu.randomred.trupsbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileService {

    private final Path basePath = Paths.get("trups-data");
    private final Path models = basePath.resolve("models");
    private final Path keys = basePath.resolve("evaluation-keys");

    public void saveModelData(Integer modelId, MultipartFile file) {
        log.info("Saving model data for model ID {}, file size: {}B", modelId, file.getSize());
        saveFile(models.resolve(modelId.toString()), "data.csv", file);
    }

    public void saveEvaluationKey(Integer appUserId, Integer modelId, MultipartFile file) {
        log.info("Saving evaluation key for model ID {}, user ID {}, file size: {}B", modelId, appUserId, file.getSize());
        saveFile(keys.resolve(appUserId.toString()), modelId.toString(), file);
    }

    public byte[] getModelClientFiles(Integer modelId) {
        try {
            Path path = models.resolve(modelId.toString()).resolve("client-files.zip");
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("Error occurred during the reading of client-files.zip, model ID: {}, path: {}, error message: {}", modelId, basePath.toAbsolutePath(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void saveFile(Path dirPath, String filename, MultipartFile file) {
        try {
            Files.createDirectories(dirPath);
            Files.write(dirPath.resolve(filename), file.getBytes());
        } catch (IOException e) {
            log.error("Error occurred during the saving of file under {}, error message: {}", dirPath.resolve(filename).toAbsolutePath(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
