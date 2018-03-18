package com.demidovn.fruitbounty.server.dto.operations.thirdparties;

import lombok.Data;

import java.util.LinkedHashMap;

@Data
public class FacebookAuthedUserInfo {

  private long id;
  private String first_name;
  private String last_name;
  private LinkedHashMap picture;

}
