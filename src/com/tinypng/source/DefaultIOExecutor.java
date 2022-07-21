package com.tinypng.source;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class DefaultIOExecutor {

    private static final int PROCESSORS_COUNT;

    static {
        int number = Runtime.getRuntime().availableProcessors();
        PROCESSORS_COUNT = number < 1 ? 4 : number;
    }

    private final ThreadPoolExecutor IOExecutor;

    public ThreadPoolExecutor get() {
        return IOExecutor;
    }

    private static final DefaultIOExecutor mInstance = new DefaultIOExecutor();

    public static DefaultIOExecutor getInstance() {
        return mInstance;
    }

    private DefaultIOExecutor() {
        IOExecutor = new ThreadPoolExecutor(PROCESSORS_COUNT, (2 * PROCESSORS_COUNT) + 1, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        IOExecutor.setThreadFactory(new DefaultThreadFactory());
    }

    private static class DefaultThreadFactory implements ThreadFactory {
        private final ThreadGroup group;

        private DefaultThreadFactory() {
            this.group = new ThreadGroup("DefaultIOExecutor_Group");
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r);
            t.setName("DefaultIOExecutor_" + t.hashCode());
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != 5) {
                t.setPriority(5);
            }
            return t;
        }
    }
}
