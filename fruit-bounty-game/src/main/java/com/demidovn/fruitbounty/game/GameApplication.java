package com.demidovn.fruitbounty.game;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ComponentScan
public class GameApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(GameApplication.class, args);
  }

}
