package com.demidovn.fruitbounty.server.converters;

import com.demidovn.fruitbounty.server.dto.operations.ThirdPartyAuthedUserInfo;
import com.demidovn.fruitbounty.server.dto.operations.thirdparties.FacebookAuthedUserInfo;

import com.demidovn.fruitbounty.server.services.auth.AuthType;
import org.springframework.stereotype.Component;

@Component
public class FacebookAuthedUserInfo2ThirdPartyAuthedUserInfoConverter
  implements FruitServerConverter<FacebookAuthedUserInfo, ThirdPartyAuthedUserInfo> {

  private static final String FB_THIRD_PARTY_TYPE = AuthType.FB.getStringRepresentation();
  private static final String PUBLIC_NAME_FORMAT = "%s %s";
  private static final String USER_IMAGE_DYNAMIC_REF = "https://graph.facebook.com/%s/picture";

  @Override
  public ThirdPartyAuthedUserInfo convert(FacebookAuthedUserInfo facebookAuthedUserInfo) {
    ThirdPartyAuthedUserInfo thirdPartyAuthedUserInfo = new ThirdPartyAuthedUserInfo();

    long userId = facebookAuthedUserInfo.getId();
    thirdPartyAuthedUserInfo.setThirdPartyId(String.valueOf(userId));
    thirdPartyAuthedUserInfo.setThirdPartyType(FB_THIRD_PARTY_TYPE);
    thirdPartyAuthedUserInfo.setPublicName(String.format(
      PUBLIC_NAME_FORMAT,
      facebookAuthedUserInfo.getFirst_name(),
      facebookAuthedUserInfo.getLast_name()));

    String imageUrl = String.format(USER_IMAGE_DYNAMIC_REF, userId);
    thirdPartyAuthedUserInfo.setImg(imageUrl);

    return thirdPartyAuthedUserInfo;
  }

}
