package com.demidovn.fruitbounty.server.converters;

import com.demidovn.fruitbounty.server.dto.operations.ThirdPartyAuthedUserInfo;
import com.demidovn.fruitbounty.server.services.auth.AuthType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class VkAuthResponseMap2ThirdPartyAuthedUserInfoConverter
        implements FruitServerConverter<Map<String, List<Map<String, Object>>>, ThirdPartyAuthedUserInfo> {

  private static final String VK_THIRD_PARTY_TYPE = AuthType.VK.getStringRepresentation();
  private static final String PUBLIC_NAME_FORMAT = "%s %s";

  @Override
  public ThirdPartyAuthedUserInfo convert(Map<String, List<Map<String, Object>>> vkAuthedUserInfo) {
    ThirdPartyAuthedUserInfo thirdPartyAuthedUserInfo = new ThirdPartyAuthedUserInfo();

    Map<String, Object> vkResponse = vkAuthedUserInfo.get("response").get(0);

    thirdPartyAuthedUserInfo.setThirdPartyId(String.valueOf(vkResponse.get("id")));
    thirdPartyAuthedUserInfo.setThirdPartyType(VK_THIRD_PARTY_TYPE);
    thirdPartyAuthedUserInfo.setPublicName(String.format(
            PUBLIC_NAME_FORMAT,
            vkResponse.get("first_name"),
            vkResponse.get("last_name")));
    thirdPartyAuthedUserInfo.setImg((String) vkResponse.get("photo_50"));

    return thirdPartyAuthedUserInfo;
  }

}
