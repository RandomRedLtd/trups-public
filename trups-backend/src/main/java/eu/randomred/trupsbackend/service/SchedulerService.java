package eu.randomred.trupsbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@Slf4j
public class SchedulerService {

    @Value("${trups.process-timeout}")
    private Integer processTimeout;

    @Value("${trups.training-job-offset}")
    private Integer trainingJobOffset;

    private final Scheduler scheduler;

    @Autowired
    public SchedulerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleTrainingJob(Integer modelId) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("modelId", modelId);

        JobDetail jobDetail = JobBuilder.newJob(TrainingJob.class)
                .withIdentity(MessageFormat.format("TRAINING-JOB-MODEL-{0}", modelId))
                .usingJobData(jobDataMap)
                .build();

        Trigger jobTrigger = TriggerBuilder.newTrigger()
                .withIdentity(MessageFormat.format("TRAINING-JOB-MODEL-{0}-TRIGGER", modelId))
                .startAt(Date.from(Instant.now().plus(trainingJobOffset, ChronoUnit.SECONDS)))
                .forJob(jobDetail)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        try {
            scheduler.scheduleJob(jobDetail, jobTrigger);
        } catch (SchedulerException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void scheduleProcessTimeoutJob(String pUUID) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("pUUID", pUUID);

        JobDetail jobDetail = JobBuilder.newJob(ProcessTimeoutJob.class)
                .withIdentity(MessageFormat.format("PROCESS-TIMEOUT-JOB-{0}", pUUID))
                .usingJobData(jobDataMap)
                .build();

        Trigger jobTrigger = TriggerBuilder.newTrigger()
                .withIdentity(MessageFormat.format("PROCESS-TIMEOUT-JOB-{0}-TRIGGER", pUUID))
                .startAt(Date.from(Instant.now().plus(processTimeout, ChronoUnit.SECONDS)))
                .forJob(jobDetail)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        try {
            scheduler.scheduleJob(jobDetail, jobTrigger);
        } catch (SchedulerException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
