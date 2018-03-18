package com.demidovn.fruitbounty.server.dto.operations;

import lombok.Data;

@Data
public class ThirdPartyAuthedUserInfo {

  private String thirdPartyType;
  private String thirdPartyId;
  private String publicName;
  private String img;

}
