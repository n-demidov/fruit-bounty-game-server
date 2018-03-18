package com.demidovn.fruitbounty.server;

import com.demidovn.fruitbounty.game.GameApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Import(GameApplication.class)
@EnableScheduling
@EnableJpaRepositories("com.demidovn.fruitbounty.server.persistence.repository")
@EntityScan("com.demidovn.fruitbounty.server.persistence.entities")
@ComponentScan("com.demidovn.fruitbounty.server")
@SpringBootApplication
public class ServerApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(ServerApplication.class, args);
  }

}
