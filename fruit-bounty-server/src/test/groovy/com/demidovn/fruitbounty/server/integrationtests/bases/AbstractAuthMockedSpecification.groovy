package com.demidovn.fruitbounty.server.integrationtests.bases

import com.demidovn.fruitbounty.server.dto.operations.ThirdPartyAuthedUserInfo
import com.demidovn.fruitbounty.server.integrationtests.opeartions.OperationExecutor
import com.demidovn.fruitbounty.server.integrationtests.opeartions.OperationsCreator
import com.demidovn.fruitbounty.server.services.auth.authenticator.ThirdPartyUserAuthenticator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean

import static org.mockito.BDDMockito.given

abstract class AbstractAuthMockedSpecification extends AbstractWebsocketClientSpecification {

  private static final String MOCKED_THIRD_PARTY_TYPE = "ts"
  private static final String MOCKED_USER_NAME_PREFIX = "mocked-user-"
  protected static final int DEFAULT_USER_ID = 1
  protected static final int SECOND_USER_ID = 2

  @MockBean
  private ThirdPartyUserAuthenticator thirdPartyUserAuthenticator

  @Autowired
  protected OperationExecutor operationExecutor

  @Autowired
  private OperationsCreator operationsCreator

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

    given(this.thirdPartyUserAuthenticator.authenticate(operationsCreator.getAuthOperation(userId)))
        .willReturn(authedUserInfo)
  }

  protected String getMockedUserName(int userId) {
    return MOCKED_USER_NAME_PREFIX + userId
  }

}
