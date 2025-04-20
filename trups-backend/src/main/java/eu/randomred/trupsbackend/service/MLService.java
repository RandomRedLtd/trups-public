package eu.randomred.trupsbackend.service;

import eu.randomred.trupsbackend.model.MLModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
public class MLService {

    @Value("${trups.python-path}")
    private String pythonPath;

    @Value("${trups.training-script-path}")
    private String trainingScript;

    @Value("${trups.inference-script-path}")
    private String inferenceScript;

    private final SchedulerService schedulerService;

    @Autowired
    public MLService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public Float runTraining(MLModel mlModel) {
        StopWatch sw = StopWatch.createStarted();

        byte[] score = runProcess(trainingScript, mlModel.getId().toString(), mlModel.getType().toString(), UUID.randomUUID().toString());

        sw.stop();

        log.info("Training model ID {}, type {}, owner ID {} took {} ms", mlModel.getId(), mlModel.getType(), mlModel.getOwner().getId(), sw.getDuration().toMillis());

        return Float.parseFloat(new String(score));
    }

    public byte[] runInference(Integer appUserId, Integer modelId, byte[] ciphertext) {
        log.info("Running inference for model ID {}, user ID {}", modelId, appUserId);

        StopWatch sw = StopWatch.createStarted();

        byte[] result = runProcess(inferenceScript, appUserId.toString(), modelId.toString(), new String(Base64.getEncoder().encode(ciphertext)), UUID.randomUUID().toString());

        sw.stop();

        log.info("Inference for model ID {}, app user ID {}, took {} ms, ciphertext size: {}", modelId, appUserId, sw.getDuration().toMillis(), ciphertext.length);

        return Base64.getDecoder().decode(result);
    }

    private byte[] runProcess(String... args) {
        ProcessBuilder pb = new ProcessBuilder(Stream.concat(Stream.of(pythonPath), Arrays.stream(args)).toArray(String[]::new));
        try {
            schedulerService.scheduleProcessTimeoutJob(args[args.length - 1]);

            pb.redirectErrorStream(true);

            Process pythonProcess = pb.start();

            byte[] output = pythonProcess.getInputStream().readAllBytes();

            pythonProcess.waitFor();

            if (pythonProcess.exitValue() == 137)
                throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT);

            return output;
        } catch (IOException e) {
            log.error("Running Python process for {}, {}, {} threw an exception: {}", args[0], args[1], args[2], e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
