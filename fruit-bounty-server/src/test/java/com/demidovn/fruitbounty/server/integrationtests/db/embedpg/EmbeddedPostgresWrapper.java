package com.demidovn.fruitbounty.server.integrationtests.db.embedpg;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmbeddedPostgresWrapper {

  private static final int EMBEDDED_POSTGRES_PORT = 9999;

  private EmbeddedPostgres embeddedPostgres;

  public void start() throws IOException, InterruptedException {
    log.info(String.format("Start EmbeddedPostgres at %d port", EMBEDDED_POSTGRES_PORT));

    if (embeddedPostgres == null) {
      embeddedPostgres = EmbeddedPostgres.builder()
        .setPort(EMBEDDED_POSTGRES_PORT)
        .start();
    }
  }

  public void stop() throws IOException {
    log.info("Stop EmbeddedPostgres");

    if (embeddedPostgres != null) {
      embeddedPostgres.close();
      embeddedPostgres = null;
    }
  }

}
