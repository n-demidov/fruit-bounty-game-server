package com.demidovn.fruitbounty.server.dto.operations.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {

  private long id;
  private String name;
  private String img;
  private int score;
  private int wins, defeats, draws;

}
