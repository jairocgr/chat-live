package io.chat.live.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Configuration
@RequiredArgsConstructor
public class ExecutorServiceConfig {

    @Value("${app.executor.max-threads}")
    private final int maxThreads;

    @Bean
    public ExecutorService provide() {
        var runtime = Runtime.getRuntime();
        var cpus = runtime.availableProcessors();
        var nThreads = ensureMax(cpus, maxThreads);
        return Executors.newFixedThreadPool(nThreads);
    }

    private int ensureMax(int value, int max) {
        return min(max(value, 1), max);
    }

}
