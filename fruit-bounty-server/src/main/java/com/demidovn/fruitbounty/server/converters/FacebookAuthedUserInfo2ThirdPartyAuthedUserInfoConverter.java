package com.demidovn.fruitbounty.server.converters;

import com.demidovn.fruitbounty.server.dto.operations.ThirdPartyAuthedUserInfo;
import com.demidovn.fruitbounty.server.dto.operations.thirdparties.FacebookAuthedUserInfo;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FacebookAuthedUserInfo2ThirdPartyAuthedUserInfoConverter
  implements FruitServerConverter<FacebookAuthedUserInfo, ThirdPartyAuthedUserInfo> {

  private static final String FB_THIRD_PARTY_TYPE = "fb";
  private static final String PUBLIC_NAME_FORMAT = "%s %s";

  @Override
  public ThirdPartyAuthedUserInfo convert(FacebookAuthedUserInfo facebookAuthedUserInfo) {
    ThirdPartyAuthedUserInfo thirdPartyAuthedUserInfo = new ThirdPartyAuthedUserInfo();

    thirdPartyAuthedUserInfo.setThirdPartyId(String.valueOf(facebookAuthedUserInfo.getId()));
    thirdPartyAuthedUserInfo.setThirdPartyType(FB_THIRD_PARTY_TYPE);
    thirdPartyAuthedUserInfo.setPublicName(String.format(
      PUBLIC_NAME_FORMAT,
      facebookAuthedUserInfo.getFirst_name(),
      facebookAuthedUserInfo.getLast_name()));

    String img = (String) ((Map) facebookAuthedUserInfo.getPicture().get("data")).get("url");
    thirdPartyAuthedUserInfo.setImg(img);

    return thirdPartyAuthedUserInfo;
  }

}
