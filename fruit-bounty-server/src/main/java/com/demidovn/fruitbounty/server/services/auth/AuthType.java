package com.demidovn.fruitbounty.server.services.auth;

public enum AuthType {

  FB(AuthType.FB_TYPE),
  VK(AuthType.VK_TYPE),
  YANDEX(AuthType.YANDEX_TYPE),
  SB(AuthType.SB_TYPE);

  private static final String VK_TYPE = "vk";
  private static final String FB_TYPE = "fb";
  private static final String YANDEX_TYPE = "ya";
  private static final String SB_TYPE = "sb";

  private String stringRepresentation;

  AuthType(String stringRepresentation) {
    this.stringRepresentation = stringRepresentation;
  }

  public String getStringRepresentation() {
    return stringRepresentation;
  }

  public static AuthType fromString(String stringAuthType) {
    for (AuthType authType : AuthType.values()) {
      if (authType.stringRepresentation.equals(stringAuthType)) {
        return authType;
      }
    }
    throw new IllegalStateException("Unknown AuthType=" + stringAuthType);
  }

}
