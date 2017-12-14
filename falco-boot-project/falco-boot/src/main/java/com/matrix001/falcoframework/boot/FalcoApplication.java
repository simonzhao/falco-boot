package com.matrix001.falcoframework.boot;

import com.matrix001.falcoframework.context.ConfigurableApplicationContext;

public class FalcoApplication {
    private Class<?> primarySource;

    public FalcoApplication(Class<?> primarySource){
        this.primarySource = primarySource;
    }

    public ConfigurableApplicationContext run(String... args){
        ConfigurableApplicationContext context = null;
        return context;
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args){
        return new FalcoApplication(primarySource).run(args);
    }
}