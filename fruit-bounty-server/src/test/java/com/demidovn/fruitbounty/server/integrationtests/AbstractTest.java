package com.demidovn.fruitbounty.server.integrationtests;

import com.demidovn.fruitbounty.server.ServerApplication;
import com.demidovn.fruitbounty.server.integrationtests.db.embedpg.EmbeddedPostgresWrapper;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest(
  classes = {ServerApplication.class},
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractTest {

  private static EmbeddedPostgresWrapper embeddedPostgresWrapper = new EmbeddedPostgresWrapper();

  @BeforeClass
  public static void startEmbeddedPg() throws IOException, InterruptedException {
    embeddedPostgresWrapper.start();
  }

  @AfterClass
  public static void stopEmbeddedPg() throws IOException {
    embeddedPostgresWrapper.stop();
  }

}
