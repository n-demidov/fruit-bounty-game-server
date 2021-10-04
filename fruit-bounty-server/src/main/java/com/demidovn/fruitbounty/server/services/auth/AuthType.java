package com.demidovn.fruitbounty.server.services.auth;

public enum AuthType {

  FB(AuthType.FB_TYPE),
  VK(AuthType.VK_TYPE),
  YANDEX(AuthType.YANDEX_TYPE);

  private static final String VK_TYPE = "vk";
  private static final String FB_TYPE = "fb";
  private static final String YANDEX_TYPE = "ya";

  private String stringRepresentation;

  AuthType(String stringRepresentation) {
    this.stringRepresentation = stringRepresentation;
  }

  public String getStringRepresentation() {
    return stringRepresentation;
  }

  public static AuthType fromString(String stringAuthType) {
    if (VK_TYPE.equals(stringAuthType)) {
      return VK;
    } else if (YANDEX_TYPE.equals(stringAuthType)) {
      return YANDEX;
    } else {
      return FB;
    }
  }

}
