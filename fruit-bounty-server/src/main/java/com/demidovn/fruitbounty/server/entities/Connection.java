package com.demidovn.fruitbounty.server.entities;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;

@Data
public class Connection {

  @Setter(AccessLevel.NONE)
  private final WebSocketSession wsSession;

  @Setter(AccessLevel.NONE)
  private final long connectionEstablished;

  @Setter(AccessLevel.NONE)
  private long lastActionTime;

  private long userId;

  @Setter(AccessLevel.NONE)
  private final AtomicInteger authAttempts = new AtomicInteger(0);

  public Connection(WebSocketSession wsSession) {
    this.wsSession = wsSession;
    this.connectionEstablished = Instant.now().getEpochSecond();
  }

  public String getId() {
    return wsSession.getId();
  }

  public void updateLastActionTime() {
    this.lastActionTime = Instant.now().getEpochSecond();
  }

  public boolean isAuthed() {
    return userId != 0;
  }

}
