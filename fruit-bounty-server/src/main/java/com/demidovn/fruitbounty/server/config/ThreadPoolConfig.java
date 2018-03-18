package com.demidovn.fruitbounty.server.config;

import com.demidovn.fruitbounty.server.AppConfigs;
import com.demidovn.fruitbounty.server.services.metrics.ServerMetricsLogger;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadPoolConfig {

  private static final String AUTH_POOL_NAME_FORMAT = "auth-pool-%d";
  private static final String GAME_NOTIFIER_POOL_NAME_FORMAT = "game-notif-pool-%d";

  @Autowired
  private ServerMetricsLogger serverMetricsLogger;

  @Bean
  public ExecutorService authExecutorService() {
    BlockingQueue<Runnable> queue =
      new ArrayBlockingQueue<>(AppConfigs.AUTH_THREAD_POOL_QUEUE_SIZE);
    serverMetricsLogger.setAuthPoolQueue(queue);

    ThreadFactory threadFactory = new ThreadFactoryBuilder()
        .setNameFormat(AUTH_POOL_NAME_FORMAT)
        .build();

    return new ThreadPoolExecutor(
        AppConfigs.AUTH_THREAD_POOL_CORE,
        AppConfigs.AUTH_THREAD_POOL_MAX,
        AppConfigs.AUTH_THREAD_POOL_THREAD_TTL,
        TimeUnit.SECONDS,
        queue,
        threadFactory);
  }

  @Bean
  public ExecutorService gameNotifierExecutorService() {
    BlockingQueue<Runnable> queue =
      new ArrayBlockingQueue<>(AppConfigs.GAME_NOTIFIER_THREAD_POOL_QUEUE_SIZE);
    serverMetricsLogger.setGameNotifierPoolQueue(queue);

    ThreadFactory threadFactory = new ThreadFactoryBuilder()
      .setNameFormat(GAME_NOTIFIER_POOL_NAME_FORMAT)
      .build();

    return new ThreadPoolExecutor(
      AppConfigs.GAME_NOTIFIER_THREAD_POOL_CORE,
      AppConfigs.GAME_NOTIFIER_THREAD_POOL_MAX,
      AppConfigs.GAME_NOTIFIER_THREAD_POOL_THREAD_TTL,
      TimeUnit.SECONDS,
      queue,
      threadFactory);
  }

}
