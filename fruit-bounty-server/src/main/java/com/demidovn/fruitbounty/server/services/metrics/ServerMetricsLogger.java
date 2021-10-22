package com.demidovn.fruitbounty.server.services.metrics;

import com.demidovn.fruitbounty.server.MetricsConsts;
import com.demidovn.fruitbounty.server.dto.operations.MetricsDto;
import com.demidovn.fruitbounty.server.persistence.entities.metrics.players.DateStat;
import com.demidovn.fruitbounty.server.persistence.entities.metrics.players.DateStatMinutes;
import com.demidovn.fruitbounty.server.persistence.entities.metrics.Metrics;
import com.demidovn.fruitbounty.server.persistence.entities.metrics.players.HistoryPlayersMetrics;
import com.demidovn.fruitbounty.server.persistence.entities.metrics.requests.HistoryStats;
import com.demidovn.fruitbounty.server.persistence.entities.metrics.requests.DateStats;
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
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServerMetricsLogger {

  private static final long METRICS_ID = 1L;

  @Value("${game-server.metrics.players-ttl-days}")
  private int PLAYER_METRICS_TTL_DAYS;

  @Value("${game-server.metrics.stats-ttl-days}")
  private int STATS_TTL_DAYS;

  @Autowired
  private ConnectionService connectionService;

  @Autowired
  private UserGames userGames;

  @Autowired
  private UserService userService;

  @Autowired
  private MetricsRepository metricsRepository;

  @Autowired
  private StatService statService;

  private BlockingQueue<Runnable> authPoolQueue;
  private BlockingQueue<Runnable> gameNotifierPoolQueue;

  public void setAuthPoolQueue(BlockingQueue<Runnable> authPoolQueue) {
    this.authPoolQueue = authPoolQueue;
  }

  public void setGameNotifierPoolQueue(BlockingQueue<Runnable> gameNotifierPoolQueue) {
    this.gameNotifierPoolQueue = gameNotifierPoolQueue;
  }

  public void logMetrics() {
    statService.incCounter(MetricsConsts.SERVER.UPTIME_MINUTES_STAT);
    Metrics metrics = loadMetrics();

    MetricsDto currentMetrics = getCurrentMetrics();
    log.info("Current metrics: {}", currentMetrics.getString());
    metrics.setCurrentMetrics(currentMetrics.getString());

    updateHistPlayers(currentMetrics.getAuthedConns(), metrics.getHistPlayers());
    updateHistStats(metrics.getHistStats());

    metricsRepository.save(metrics);
  }

  private MetricsDto getCurrentMetrics() {
    return new MetricsDto(
            ManagementFactory.getThreadMXBean().getThreadCount(),
            connectionService.countNotAuthedConnections(),
            connectionService.countAuthedConnections(),
            userGames.countPlayingUsers(),
            authPoolQueue.size(),
            gameNotifierPoolQueue.size(),
            userService.getCount());
  }

  private void updateHistPlayers(int onlinePlayersNum, HistoryPlayersMetrics persistedHistPlayers) {
    String currentDate = getCurrentDate();

    DateStat dateStat = persistedHistPlayers.getStatsByDate().get(currentDate);
    if (dateStat == null) {
      dateStat = new DateStat();
      persistedHistPlayers.getStatsByDate().put(currentDate, dateStat);
    }

    // Increment total minutes for Date
    int totalDayMinutes = dateStat.getTotalMinutes() + 1;
    dateStat.setTotalMinutes(totalDayMinutes);

    DateStatMinutes dateStatMinutes = dateStat.getMinutesByPlayers().get(onlinePlayersNum);
    if (dateStatMinutes == null) {
      dateStatMinutes = new DateStatMinutes();
      dateStat.getMinutesByPlayers().put(onlinePlayersNum, dateStatMinutes);
    }

    // Increment minutes for authed connections.
    int connectionsDayMinutes = dateStatMinutes.getMinutes() + 1;
    dateStatMinutes.setMinutes(connectionsDayMinutes);

    recalculateDayPercents(dateStat.getMinutesByPlayers(), totalDayMinutes);
    removeRedundantDates(persistedHistPlayers.getStatsByDate(), PLAYER_METRICS_TTL_DAYS);
  }

  private void updateHistStats(HistoryStats persistedHistStats) {
    Map<String, Long> newHistStats = statService.getAndClear();

    for (Map.Entry<String, Long> newHistStat : newHistStats.entrySet()) {
      if (newHistStat.getValue() > 0) {
        updateHistStats(newHistStat, persistedHistStats);
      }
    }
    sortCurrentHistStat(persistedHistStats);
    removeRedundantDates(persistedHistStats.getStatsByDate(), STATS_TTL_DAYS);
  }

  private void updateHistStats(Map.Entry<String, Long> newHistStat, HistoryStats persistedHistStats) {
    String currentDate = getCurrentDate();

    DateStats dateStats = persistedHistStats.getStatsByDate().get(currentDate);
    if (dateStats == null) {
      dateStats = new DateStats();
      persistedHistStats.getStatsByDate().put(currentDate, dateStats);
    }

    long totalByKey = dateStats.getStatsByKey().getOrDefault(newHistStat.getKey(), 0L);
    totalByKey += newHistStat.getValue();
    dateStats.getStatsByKey().put(newHistStat.getKey(), totalByKey);
  }

  private void sortCurrentHistStat(HistoryStats persistedHistStats) {
    String currentDate = getCurrentDate();
    DateStats dateStats = persistedHistStats.getStatsByDate().get(currentDate);

    Map<String, Long> unsortedStatsByKey = dateStats.getStatsByKey();
    Map<String, Long> sortedStatsByKey = new TreeMap<>(unsortedStatsByKey);
    dateStats.setStatsByKey(sortedStatsByKey);
  }

  private Metrics loadMetrics() {
    // Keep all in one row because free DB has a limit of rows :)
    Metrics metrics = metricsRepository.findOne(METRICS_ID);
    if (metrics == null) {
      metrics = new Metrics();
      metrics.setHistPlayers(new HistoryPlayersMetrics());
      metrics.setHistStats(new HistoryStats());
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

  private void removeRedundantDates(Map<String, ?> statsByDate, int storeMetricsDays) {
    List<String> validDates = prepareValidDates(storeMetricsDays);
    statsByDate.entrySet()
            .removeIf(entry -> !validDates.contains(entry.getKey()));
  }

  private List<String> prepareValidDates(int storeMetricsDays) {
    LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
    List<String> dates = new ArrayList<>(storeMetricsDays);
    int counter = 0;

    do {
      String tempDate = localDate
              .minus(counter, ChronoUnit.DAYS)
              .format(DateTimeFormatter.ISO_LOCAL_DATE);
      dates.add(tempDate);
      counter++;
    } while (counter < storeMetricsDays);

    return dates;
  }

  private String getCurrentDate() {
    LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
    return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
  }

}
