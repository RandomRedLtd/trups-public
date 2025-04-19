package eu.randomred.trupsbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class ProcessTimeoutJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) {
        String pUUID = context.getMergedJobDataMap().getString("pUUID");

        ProcessHandle.allProcesses()
                .filter(p -> p.info().commandLine().map(c -> c.contains(pUUID)).orElse(false))
                .findFirst()
                .ifPresent(ProcessHandle::destroyForcibly);
    }

}
