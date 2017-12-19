package com.matrix001.falcoframework.boot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class JobPool {
    private static ExecutorService pool;

    public static void init(int threadNum){
        pool = Executors.newFixedThreadPool(threadNum);
    }

    public static void init(){
        init(20);
    }

    public static ExecutorService getPool() {
        return pool;
    }
}
