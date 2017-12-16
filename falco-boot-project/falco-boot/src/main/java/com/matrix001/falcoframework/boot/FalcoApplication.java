package com.matrix001.falcoframework.boot;

import com.matrix001.falcoframework.context.ConfigurableApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FalcoApplication {
    private Class<?> primarySource;
    private Logger logger = LoggerFactory.getLogger(FalcoApplication.class);

    public FalcoApplication(Class<?> primarySource) {
        this.primarySource = primarySource;
    }

    public ConfigurableApplicationContext run(String... args) {
        ConfigurableApplicationContext context = null;
        logger.info("Hello World!");
        return context;
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        return new FalcoApplication(primarySource).run(args);
    }
}