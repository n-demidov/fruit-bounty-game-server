package com.demidovn.fruitbounty.server.integrationtests.bases

import com.demidovn.fruitbounty.server.ServerApplication
import com.demidovn.fruitbounty.server.integrationtests.db.embedpg.EmbeddedPostgresWrapper
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification;

@ActiveProfiles("test")
@SpringBootTest(
        classes = [ServerApplication.class],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AbstractSpecification extends Specification {

  @Shared EmbeddedPostgresWrapper embeddedPostgresWrapper = new EmbeddedPostgresWrapper();

  def setupSpec() throws IOException, InterruptedException {
    embeddedPostgresWrapper.start()
  }

  def cleanupSpec() throws IOException {
    embeddedPostgresWrapper.stop()
  }

}
