package com.n3t.dispatcher.scheduled_job;

import com.n3t.dispatcher.domain.ETA;
import com.n3t.dispatcher.domain.SpecificationsBuilder;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.n3t.dispatcher.service.*;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class JobCheckETA implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobCheckETA.class);
    public static final String JOB_GROUP = "JOB_GROUP_CHECK_ETA";
    public static final String JOB_NAME = "JOB_NAME_CHECK_ETA";
    public static final String TRIGGER_GROUP = "TRIGGER_GROUP_CHECK_ETA";
    public static final String TRIGGER_NAME = "TRIGGER_NAME_CHECK_ETA";
    public static final String CRON_EXPRESSION = "0 */2 * ? * *";
    public static final int NO_OF_PENDING = 3;

    @Autowired
    private GoogleMapService googleMapService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("{} starts executing...", this.getClass().getSimpleName());
        AutowireHelper.autowire(this, this.googleMapService);
        SpecificationsBuilder<ETA> specificationsBuilder = new SpecificationsBuilder<>();
        specificationsBuilder.addSpecification(ETA.Specs.filterByStatus(ETA.Status.PENDING));
        specificationsBuilder.addSpecification(ETA.Specs.filterByCount(NO_OF_PENDING));
        List<ETA> etaAttempts = Optional
                .ofNullable(this.googleMapService.query(specificationsBuilder.build()))
                .map(Collection::stream)
                .orElse(Stream.empty())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // TODO: Performance issue - Keep analyze task size to take action if task list is too large
        LOGGER.info("Found {} ETAs", etaAttempts.size());
        for (ETA etaAttempt : etaAttempts) {
            Long emergencyId = etaAttempt.getEmergency().getId(); //eta nay cua lan cap cuu nao

        }
        LOGGER.info("{} ends executing...", this.getClass().getSimpleName());
    }
}
