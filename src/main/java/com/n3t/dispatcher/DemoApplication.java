package com.n3t.dispatcher;

import com.n3t.dispatcher.scheduled_job.N3TScheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableScheduling
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
public class DemoApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        initializeScheduler();
    }

    private static void initializeScheduler() {
        try {
            N3TScheduler scheduler = DemoSpringContext.getBean(N3TScheduler.class);
            scheduler.scheduleJobCheckETA();
        } catch (SchedulerException se) {
            LOGGER.error("SchedulerException occurred while initializing SchedulingManager");
            se.printStackTrace();
        } catch (NullPointerException npe) {
            LOGGER.error("NullPointerException occurred while initializing SchedulingManager");
            npe.printStackTrace();
        } catch (Exception e) {
            LOGGER.error("Exception occurred while initializing SchedulingManager");
            e.printStackTrace();
        }
    }
}
