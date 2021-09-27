package com.demidovn.fruitbounty.server.services.metrics;

import com.demidovn.fruitbounty.server.persistence.entities.metrics.DateStat;
import com.demidovn.fruitbounty.server.persistence.entities.metrics.DateStatMinutes;
import com.demidovn.fruitbounty.server.persistence.entities.metrics.Metrics;
import com.demidovn.fruitbounty.server.persistence.entities.metrics.HistoryMetrics;
import com.demidovn.fruitbounty.server.persistence.repository.MetricsRepository;
import com.demidovn.fruitbounty.server.services.ConnectionService;
import com.demidovn.fruitbounty.server.services.UserService;
import com.demidovn.fruitbounty.server.services.game.UserGames;
import java.lang.management.ManagementFactory;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServerMetricsLogger {

  private static final long METRICS_ID = 1L;

  @Value("${game-server.metrics.store-days}")
  private int STORE_METRICS_DAYS;

  @Autowired
  private ConnectionService connectionService;

  @Autowired
  private UserGames userGames;

  @Autowired
  private UserService userService;

  @Autowired
  private MetricsRepository metricsRepository;

  private BlockingQueue<Runnable> authPoolQueue;
  private BlockingQueue<Runnable> gameNotifierPoolQueue;

  public void setAuthPoolQueue(BlockingQueue<Runnable> authPoolQueue) {
    this.authPoolQueue = authPoolQueue;
  }

  public void setGameNotifierPoolQueue(BlockingQueue<Runnable> gameNotifierPoolQueue) {
    this.gameNotifierPoolQueue = gameNotifierPoolQueue;
  }

  public void logMetrics() {
    int authedConns = connectionService.countAuthedConnections();
    String currentMetrics = String.format(
            "threads=%d, notAuthCons=%d, authCons=%d, playingUsers=%d, authPoolQueue.size=%d, gameNotifierPoolQueue.size=%d, users=%d",
            ManagementFactory.getThreadMXBean().getThreadCount(),
            connectionService.countNotAuthedConnections(),
            authedConns,
            userGames.countPlayingUsers(),
            authPoolQueue.size(),
            gameNotifierPoolQueue.size(),
            userService.getCount());

    log.info("metrics: {}", currentMetrics);

    persistMetricsToDb(authedConns, currentMetrics);
  }

  private void persistMetricsToDb(int authedConns, String currentMetrics) {
    // Keep all in one row because free DB has a limit of rows :)
    Metrics metrics = loadMetrics();

    LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
    String currentDate = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

    HistoryMetrics histMetrics = metrics.getHistMetrics();
    DateStat dateStat = histMetrics.getStatsByDate().get(currentDate);
    if (dateStat == null) {
      dateStat = new DateStat();
      histMetrics.getStatsByDate().put(currentDate, dateStat);
    }

    // Increment total minutes for Date
    int totalDayMinutes = dateStat.getTotalMinutes() + 1;
    dateStat.setTotalMinutes(totalDayMinutes);

    DateStatMinutes dateStatMinutes = dateStat.getMinutesByPlayers().get(authedConns);
    if (dateStatMinutes == null) {
      dateStatMinutes = new DateStatMinutes();
      dateStat.getMinutesByPlayers().put(authedConns, dateStatMinutes);
    }

    // Increment minutes for authed connections.
    int connectionsDayMinutes = dateStatMinutes.getMinutes() + 1;
    dateStatMinutes.setMinutes(connectionsDayMinutes);

    recalculateDayPercents(dateStat.getMinutesByPlayers(), totalDayMinutes);
    removeRedundantDates(histMetrics);

    // Update current metric info
    metrics.setCurrentMetrics(currentMetrics);

    metricsRepository.save(metrics);
  }

  private Metrics loadMetrics() {
    Metrics metrics = metricsRepository.findOne(METRICS_ID);
    if (metrics == null) {
      metrics = new Metrics();
      metrics.setHistMetrics(new HistoryMetrics());
    }
    return metrics;
  }

  private void recalculateDayPercents(Map<Integer, DateStatMinutes> minutesByPlayers, int totalDayMinutes) {
    for (Map.Entry<Integer, DateStatMinutes> entry : minutesByPlayers.entrySet()) {
      DateStatMinutes dateStatMinutes = entry.getValue();
      int connectionsDayMinutes = dateStatMinutes.getMinutes();
      int connectionsDayPercents = connectionsDayMinutes * 100 / totalDayMinutes;
      dateStatMinutes.setInPercents(connectionsDayPercents + "%");
    }
  }

  private void removeRedundantDates(HistoryMetrics metricsStatistics) {
    List<String> validDates = prepareValidDates();
    metricsStatistics.getStatsByDate().entrySet()
            .removeIf(entry -> !validDates.contains(entry.getKey()));
  }

  private List<String> prepareValidDates() {
    LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
    List<String> dates = new ArrayList<>(STORE_METRICS_DAYS);
    int counter = 0;

    do {
      String tempDate = localDate
              .minus(counter, ChronoUnit.DAYS)
              .format(DateTimeFormatter.ISO_LOCAL_DATE);
      dates.add(tempDate);
      counter++;
    } while (counter < STORE_METRICS_DAYS);

    return dates;
  }

}
