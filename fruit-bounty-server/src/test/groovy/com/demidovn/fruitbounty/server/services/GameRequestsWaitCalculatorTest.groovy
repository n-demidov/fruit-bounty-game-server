package com.demidovn.fruitbounty.server.services

import spock.lang.Specification
import spock.lang.Subject

class GameRequestsWaitCalculatorTest extends Specification {

    @Subject
    GameRequestsWaitCalculator calculator = new GameRequestsWaitCalculator()

    def "should count getWaitByOnlineUsers correctly"() {
        setup:
        calculator.connectionService = mockConnectionService(onlineUsers)

        when:
        def actual = calculator.getWaitByOnlineUsers()

        then:
        actual == expected

        where:
        onlineUsers  | expected
        1            | 0
        2            | 1
        5            | 1
        6            | 2
        11           | 2
        12           | 3
        17           | 3
        18           | 4
    }

    def "should count getWaitUntilBot correctly"() {
        setup:
        calculator.connectionService = mockConnectionService(onlineUsers)

        when:
        def actual = calculator.getWaitUntilBot(score)

        then:
        actual == expected

        where:
        onlineUsers  | score  | expected
        1            | 700    | 0
        1            | 701    | 0
        2            | 700    | 1
        2            | 701    | 2

        1            | 849    | 0
        1            | 850    | 0
        2            | 849    | 2
        2            | 850    | 4

        200          | 850    | 4
        2000         | 9999   | 4
    }

    ConnectionService mockConnectionService(int onlineUsers) {
        ConnectionService mock = Mock() {
            countOnlineUsers() >> onlineUsers
        }
        mock
    }

}
