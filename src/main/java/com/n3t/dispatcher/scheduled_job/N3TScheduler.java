package com.n3t.dispatcher.scheduled_job;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
public class N3TScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(N3TScheduler.class);

    @Autowired
    private Scheduler qScheduler;

    public void start() throws SchedulerException {
        Assert.notNull(this.qScheduler, "Failed to start scheduler (reason: invalid scheduler).");
        if (!qScheduler.isStarted()) {
            LOGGER.info("Starting up scheduler...");
            this.qScheduler.start();
        }
    }

    public void shutdown() throws SchedulerException {
        LOGGER.info("Shutting down scheduler...");
        Assert.notNull(this.qScheduler, "Failed to shutdown scheduler (reason: invalid scheduler).");
        this.qScheduler.shutdown();
    }

    public List<JobExecutionContext> getQuartzRunningJobs() throws SchedulerException {
        Assert.notNull(this.qScheduler, "Failed to get Quartz running jobs (reason: invalid scheduler).");
        return this.qScheduler.getCurrentlyExecutingJobs();
    }

    public void scheduleJobCheckETA() throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(JobCheckETA.TRIGGER_NAME, JobCheckETA.TRIGGER_GROUP);
        if (this.qScheduler.checkExists(triggerKey)) {
            Trigger oldTrigger = this.qScheduler.getTrigger(triggerKey);
            TriggerBuilder triggerBuilder = oldTrigger.getTriggerBuilder();
            Trigger trigger = triggerBuilder.startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(JobCheckETA.CRON_EXPRESSION))
                    .build();
            this.qScheduler.rescheduleJob(oldTrigger.getKey(), trigger);
            LOGGER.info("Re-scheduled JobCheckETA.");
        } else {
            JobDetail jobDetail = JobBuilder.newJob(JobCheckETA.class)
                    .withIdentity(new JobKey(JobCheckETA.JOB_NAME, JobCheckETA.JOB_GROUP))
                    .build();
            TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(triggerKey);
            Trigger trigger = triggerBuilder.startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(JobCheckETA.CRON_EXPRESSION))
                    .build();
            this.qScheduler.scheduleJob(jobDetail, trigger);
            LOGGER.info("Scheduled JobCheckETA.");
        }
    }
}
