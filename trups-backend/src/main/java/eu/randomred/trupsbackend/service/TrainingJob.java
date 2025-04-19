package eu.randomred.trupsbackend.service;

import eu.randomred.trupsbackend.model.MLModel;
import eu.randomred.trupsbackend.model.MLModelStatus;
import eu.randomred.trupsbackend.repository.MLModelRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TrainingJob extends QuartzJobBean {

    @Autowired
    private MLService mlService;

    @Autowired
    private MLModelRepository mlModelRepository;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        Integer modelId = context.getMergedJobDataMap().getIntValue("modelId");

        MLModel mlModel = mlModelRepository.findOneById(modelId).orElseThrow(() -> {
            log.info("Model with ID {} not found for training, bailing training job", modelId);

            return new RuntimeException();
        });

        log.info("Starting training job for model ID {}, owner ID {}", mlModel.getId(), mlModel.getOwner().getId());

        mlModel.setStatus(MLModelStatus.TRAINING_IN_PROGRESS);

        mlModelRepository.saveAndFlush(mlModel);

        mlModel = mlModelRepository.findOneById(modelId).get();

        try {
            mlModel.setScore(mlService.runTraining(mlModel));
            mlModel.setStatus(MLModelStatus.TRAINING_SUCCESS);
            mlModel.setEnabled(true);

            log.info("Model with ID {}, owner ID {} successfully trained", mlModel.getId(), mlModel.getOwner().getId());
        } catch (RuntimeException e) {
            mlModel.setStatus(MLModelStatus.TRAINING_FAILURE);

            log.info("Model with ID {}, owner ID {} failed training, error message: {}", mlModel.getId(), mlModel.getOwner().getId(), e.getMessage());
        } finally {
            mlModelRepository.save(mlModel);
        }
    }

}
