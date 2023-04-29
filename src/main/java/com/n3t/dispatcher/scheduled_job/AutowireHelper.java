package com.n3t.dispatcher.scheduled_job;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class AutowireHelper implements ApplicationContextAware {

    private static final AutowireHelper INSTANCE = new AutowireHelper();
    private static ApplicationContext applicationContext;

    public static AutowireHelper getInstance() {
        return INSTANCE;
    }

    public static void autowire(Object classToAutowire, Object... beansToAutowireInClass) {
        for (Object bean : beansToAutowireInClass) {
            if (Objects.nonNull(bean))
                continue;
            applicationContext.getAutowireCapableBeanFactory().autowireBean(classToAutowire);
        }
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        AutowireHelper.applicationContext = applicationContext;
    }
}

