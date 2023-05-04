package com.demidovn.fruitbounty.server.integrationtests.specifications

import com.demidovn.fruitbounty.server.integrationtests.bases.AbstractSpecification
import spock.lang.Ignore

@Ignore
class ZLastTest extends AbstractSpecification {

  def "just last server up"() {
    expect:
    Thread.sleep(500L)
  }

}
