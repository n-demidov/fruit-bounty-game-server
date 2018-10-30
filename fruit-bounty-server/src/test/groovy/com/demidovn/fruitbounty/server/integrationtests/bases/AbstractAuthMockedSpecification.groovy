package com.demidovn.fruitbounty.server.integrationtests.bases

import com.demidovn.fruitbounty.server.dto.operations.ThirdPartyAuthedUserInfo
import com.demidovn.fruitbounty.server.integrationtests.opeartions.OperationExecutor
import com.demidovn.fruitbounty.server.integrationtests.opeartions.OperationsCreator
import com.demidovn.fruitbounty.server.services.auth.AuthType
import com.demidovn.fruitbounty.server.services.auth.authenticator.ThirdPartyUserAuthenticator
import org.springframework.beans.factory.annotation.Autowired
import org.spockframework.spring.SpringBean

abstract class AbstractAuthMockedSpecification extends AbstractWebsocketClientSpecification {

  private static final String MOCKED_THIRD_PARTY_TYPE = AuthType.FB.getStringRepresentation()
  private static final String MOCKED_USER_NAME_PREFIX = "mocked-user-"
  protected static final int DEFAULT_USER_ID = 1
  protected static final int SECOND_USER_ID = 2

  @SpringBean
  Map<AuthType, ThirdPartyUserAuthenticator> thirdPartyUserAuthenticators = new HashMap()

  @Autowired
  protected OperationExecutor operationExecutor

  @Autowired
  private OperationsCreator operationsCreator

  ThirdPartyUserAuthenticator fbThirdPartyUserAuthenticator = Mock()
  ThirdPartyUserAuthenticator vkThirdPartyUserAuthenticator = Mock()

  def setup() {
    mockAuth(DEFAULT_USER_ID)
    mockAuth(SECOND_USER_ID)
  }

  protected void mockAuth(int userId) {
    ThirdPartyAuthedUserInfo authedUserInfo = new ThirdPartyAuthedUserInfo()
    String userIdString = String.valueOf(userId)

    authedUserInfo.setPublicName(getMockedUserName(userId))
    authedUserInfo.setThirdPartyId(userIdString)
    authedUserInfo.setThirdPartyType(MOCKED_THIRD_PARTY_TYPE)

    fbThirdPartyUserAuthenticator.getAuthType() >> AuthType.FB
    fbThirdPartyUserAuthenticator
            .authenticate(operationsCreator.getAuthOperation(userId, AuthType.FB)) >> authedUserInfo

    fbThirdPartyUserAuthenticator.getAuthType() >> AuthType.VK
    vkThirdPartyUserAuthenticator
            .authenticate(operationsCreator.getAuthOperation(userId, AuthType.VK)) >> authedUserInfo

    thirdPartyUserAuthenticators.put(AuthType.FB, fbThirdPartyUserAuthenticator)
    thirdPartyUserAuthenticators.put(AuthType.VK, vkThirdPartyUserAuthenticator)
  }

  protected String getMockedUserName(int userId) {
    return MOCKED_USER_NAME_PREFIX + userId
  }

}
