package com.demidovn.fruitbounty.server.services.bot.movefinder

import com.demidovn.fruitbounty.game.model.Pair
import com.demidovn.fruitbounty.game.services.game.bot.movefinder.Level2ThresholdDeepMoveFinder
import com.demidovn.fruitbounty.gameapi.model.Board
import com.demidovn.fruitbounty.gameapi.model.Cell
import com.demidovn.fruitbounty.gameapi.model.Game
import com.demidovn.fruitbounty.gameapi.model.Player
import spock.lang.Specification
import spock.lang.Subject

class Level2ThresholdDeepMoveFinderSpecification extends Specification {

    int EM = 0, OP = 1, ME = 2
    int TIME_PER_MOVE = 1000 * 60 * 60 * 24
    int MAX_DEEP_MOVE = 6

    @Subject
    Level2ThresholdDeepMoveFinder bestMoveFinder = new Level2ThresholdDeepMoveFinder()

    def "should path #1 smoke test"() {
        setup:
        List<Player> players = new ArrayList<>()
        def currentPlayer = createPlayer(ME)
        players.add(createPlayer(OP))
        players.add(currentPlayer)

        Game game = new Game(
                new Board(createCellsForSmokeTest()),
                0,
                false,
                players,
                currentPlayer,
                TIME_PER_MOVE,
                System.currentTimeMillis(),
                TIME_PER_MOVE,
                false,
                null
        )

        when:
        Pair<Integer, Integer> actual = bestMoveFinder.findBestMove(currentPlayer, game, MAX_DEEP_MOVE)

        then:
        actual == new Pair(0, 1)
    }

    def "should path #2 test (with simple blocking)"() {
        setup:
        List<Player> players = new ArrayList<>()
        def currentPlayer = createPlayer(ME)
        players.add(createPlayer(OP))
        players.add(currentPlayer)

        Game game = new Game(
                new Board(createCells_2_test()),
                0,
                false,
                players,
                currentPlayer,
                TIME_PER_MOVE,
                System.currentTimeMillis(),
                TIME_PER_MOVE,
                false,
                null
        )

        when:
        Pair<Integer, Integer> actual = bestMoveFinder.findBestMove(currentPlayer, game, MAX_DEEP_MOVE)

        then:
        actual == new Pair(1, 2)
    }

    def "should path #3 test (with simple blocking #2)"() {
        setup:
        List<Player> players = new ArrayList<>()
        def currentPlayer = createPlayer(ME)
        players.add(createPlayer(OP))
        players.add(currentPlayer)

        Game game = new Game(
                new Board(createCells_3_test()),
                0,
                false,
                players,
                currentPlayer,
                TIME_PER_MOVE,
                System.currentTimeMillis(),
                TIME_PER_MOVE,
                false,
                null
        )

        when:
        Pair<Integer, Integer> actual = bestMoveFinder.findBestMove(currentPlayer, game, MAX_DEEP_MOVE)

        then:
        actual == new Pair(3, 6)
    }

    def "should path #4 test (4x4)"() {
        setup:
        List<Player> players = new ArrayList<>()
        def currentPlayer = createPlayer(ME)
        players.add(createPlayer(OP))
        players.add(currentPlayer)

        Game game = new Game(
                new Board(createCells_4x4()),
                0,
                false,
                players,
                currentPlayer,
                TIME_PER_MOVE,
                System.currentTimeMillis(),
                TIME_PER_MOVE,
                false,
                null
        )

        when:
        Pair<Integer, Integer> actual = bestMoveFinder.findBestMove(currentPlayer, game, MAX_DEEP_MOVE)

        then:
        println(actual)
        1 == 1
    }

    def "should path #5 test (5x5)"() {
        setup:
        List<Player> players = new ArrayList<>()
        def currentPlayer = createPlayer(ME)
        players.add(createPlayer(OP))
        players.add(currentPlayer)

        Game game = new Game(
                new Board(createCells_5x5()),
                0,
                false,
                players,
                currentPlayer,
                TIME_PER_MOVE,
                System.currentTimeMillis(),
                TIME_PER_MOVE,
                false,
                null
        )

        when:
        Pair<Integer, Integer> actual = bestMoveFinder.findBestMove(currentPlayer, game, MAX_DEEP_MOVE)

        then:
        println(actual)
        1 == 1
    }

    def "should path #6 test (12x12)"() {
        setup:
        List<Player> players = new ArrayList<>()
        def currentPlayer = createPlayer(ME)
        players.add(createPlayer(OP))
        players.add(currentPlayer)

        Game game = new Game(
                new Board(createCells_12x12()),
                0,
                false,
                players,
                currentPlayer,
                TIME_PER_MOVE,
                System.currentTimeMillis(),
                TIME_PER_MOVE,
                false,
                null
        )

        when:
        Pair<Integer, Integer> actual = bestMoveFinder.findBestMove(currentPlayer, game, MAX_DEEP_MOVE)

        then:
        println(actual)
        1 == 1
    }

    private Player createPlayer(int id) {
        def player = new Player()
        player.setId(id)
        player
    }

    Cell[][] createCellsForSmokeTest() {
        Cell[][] cells = new Cell[1][3];

        cells[0][0] = new Cell(OP, 1, 0, 0)
        cells[0][1] = new Cell(EM, 2, 0, 1)
        cells[0][2] = new Cell(ME, 3, 0, 2)

        cells
    }

    Cell[][] createCells_2_test() {
        Cell[][] cells = new Cell[3][3];

        cells[0][0] = new Cell(OP, 1, 0, 0)
        cells[0][1] = new Cell(EM, 3, 0, 1)
        cells[0][2] = new Cell(EM, 4, 0, 2)

        cells[1][0] = new Cell(EM, 3, 1, 0)
        cells[1][1] = new Cell(EM, 4, 1, 1)
        cells[1][2] = new Cell(EM, 3, 1, 2)

        cells[2][0] = new Cell(EM, 3, 2, 0)
        cells[2][1] = new Cell(EM, 4, 2, 1)
        cells[2][2] = new Cell(ME, 1, 2, 2)

        cells
    }

    Cell[][] createCells_3_test() {
        Cell[][] cells = new Cell[5][7]

        cells[0][0] = new Cell(OP, 1, 0, 0)
        cells[0][1] = new Cell(EM, 3, 0, 1)
        cells[0][2] = new Cell(EM, 4, 0, 2)
        cells[0][3] = new Cell(EM, 4, 0, 3)
        cells[0][4] = new Cell(EM, 4, 0, 4)
        cells[0][5] = new Cell(EM, 4, 0, 5)
        cells[0][6] = new Cell(EM, 4, 0, 6)

        cells[1][0] = new Cell(EM, 5, 1, 0)
        cells[1][1] = new Cell(EM, 4, 1, 1)
        cells[1][2] = new Cell(EM, 4, 1, 2)
        cells[1][3] = new Cell(EM, 4, 1, 3)
        cells[1][4] = new Cell(EM, 4, 1, 4)
        cells[1][5] = new Cell(EM, 4, 1, 5)
        cells[1][6] = new Cell(EM, 4, 1, 6)

        cells[2][0] = new Cell(EM, 4, 2, 0)
        cells[2][1] = new Cell(EM, 4, 2, 1)
        cells[2][2] = new Cell(EM, 4, 2, 2)
        cells[2][3] = new Cell(EM, 4, 2, 3)
        cells[2][4] = new Cell(EM, 4, 2, 4)
        cells[2][5] = new Cell(EM, 4, 2, 5)
        cells[2][6] = new Cell(EM, 4, 2, 6)

        cells[3][0] = new Cell(EM, 6, 3, 0)
        cells[3][1] = new Cell(EM, 6, 3, 1)
        cells[3][2] = new Cell(EM, 6, 3, 2)
        cells[3][3] = new Cell(EM, 6, 3, 3)
        cells[3][4] = new Cell(EM, 6, 3, 4)
        cells[3][5] = new Cell(EM, 6, 3, 5)
        cells[3][6] = new Cell(EM, 3, 3, 6)

        cells[4][0] = new Cell(EM, 5, 4, 0)
        cells[4][1] = new Cell(EM, 5, 4, 1)
        cells[4][2] = new Cell(EM, 5, 4, 2)
        cells[4][3] = new Cell(EM, 5, 4, 3)
        cells[4][4] = new Cell(EM, 5, 4, 4)
        cells[4][5] = new Cell(EM, 5, 4, 5)
        cells[4][6] = new Cell(ME, 2, 4, 6)

        cells
    }

    Cell[][] createCells_4x4() {
        int x = -1
        Cell[][] cells = new Cell[4][4]

        x++
        cells[x][0] = new Cell(OP, 1, x, 0)
        cells[x][1] = new Cell(EM, 2, x, 1)
        cells[x][2] = new Cell(EM, 3, x, 2)
        cells[x][3] = new Cell(EM, 4, x, 3)

        x++
        cells[x][0] = new Cell(EM, 5, x, 0)
        cells[x][1] = new Cell(EM, 6, x, 1)
        cells[x][2] = new Cell(EM, 7, x, 2)
        cells[x][3] = new Cell(EM, 8, x, 3)

        x++
        cells[x][0] = new Cell(EM, 1, x, 0)
        cells[x][1] = new Cell(EM, 2, x, 1)
        cells[x][2] = new Cell(EM, 3, x, 2)
        cells[x][3] = new Cell(EM, 4, x, 3)

        x++
        cells[x][0] = new Cell(EM, 5, x, 0)
        cells[x][1] = new Cell(EM, 6, x, 1)
        cells[x][2] = new Cell(EM, 7, x, 2)
        cells[x][3] = new Cell(ME, 8, x, 3)

        cells
    }



    Cell[][] createCells_5x5() {
        int x = -1
        Cell[][] cells = new Cell[5][5]

        x++
        cells[x][0] = new Cell(OP, 1, x, 0)
        cells[x][1] = new Cell(EM, 3, x, 1)
        cells[x][2] = new Cell(EM, 2, x, 2)
        cells[x][3] = new Cell(EM, 4, x, 3)
        cells[x][4] = new Cell(EM, 8, x, 4)

        x++
        cells[x][0] = new Cell(EM, 2, x, 0)
        cells[x][1] = new Cell(EM, 4, x, 1)
        cells[x][2] = new Cell(EM, 8, x, 2)
        cells[x][3] = new Cell(EM, 6, x, 3)
        cells[x][4] = new Cell(EM, 5, x, 4)

        x++
        cells[x][0] = new Cell(EM, 1, x, 0)
        cells[x][1] = new Cell(EM, 7, x, 1)
        cells[x][2] = new Cell(EM, 5, x, 2)
        cells[x][3] = new Cell(EM, 4, x, 3)
        cells[x][4] = new Cell(EM, 8, x, 4)

        x++
        cells[x][0] = new Cell(EM, 5, x, 0)
        cells[x][1] = new Cell(EM, 3, x, 1)
        cells[x][2] = new Cell(EM, 4, x, 2)
        cells[x][3] = new Cell(EM, 8, x, 3)
        cells[x][4] = new Cell(EM, 6, x, 4)

        x++
        cells[x][0] = new Cell(EM, 6, x, 0)
        cells[x][1] = new Cell(EM, 2, x, 1)
        cells[x][2] = new Cell(EM, 3, x, 2)
        cells[x][3] = new Cell(EM, 5, x, 3)
        cells[x][4] = new Cell(ME, 4, x, 4)

        cells
    }

    Cell[][] createCells_6x6() {
        int x = -1
        Cell[][] cells = new Cell[6][6]

        x++
        cells[x][0] = new Cell(OP, 1, x, 0)
        cells[x][1] = new Cell(EM, 3, x, 1)
        cells[x][2] = new Cell(EM, 2, x, 2)
        cells[x][3] = new Cell(EM, 4, x, 3)
        cells[x][4] = new Cell(EM, 8, x, 4)
        cells[x][5] = new Cell(EM, 4, x, 5)

        x++
        cells[x][0] = new Cell(EM, 2, x, 0)
        cells[x][1] = new Cell(EM, 4, x, 1)
        cells[x][2] = new Cell(EM, 8, x, 2)
        cells[x][3] = new Cell(EM, 6, x, 3)
        cells[x][4] = new Cell(EM, 5, x, 4)
        cells[x][5] = new Cell(EM, 2, x, 5)

        x++
        cells[x][0] = new Cell(EM, 1, x, 0)
        cells[x][1] = new Cell(EM, 7, x, 1)
        cells[x][2] = new Cell(EM, 5, x, 2)
        cells[x][3] = new Cell(EM, 4, x, 3)
        cells[x][4] = new Cell(EM, 8, x, 4)
        cells[x][5] = new Cell(EM, 4, x, 5)

        x++
        cells[x][0] = new Cell(EM, 2, x, 0)
        cells[x][1] = new Cell(EM, 2, x, 1)
        cells[x][2] = new Cell(EM, 2, x, 2)
        cells[x][3] = new Cell(EM, 3, x, 3)
        cells[x][4] = new Cell(EM, 1, x, 4)
        cells[x][5] = new Cell(EM, 5, x, 5)

        x++
        cells[x][0] = new Cell(EM, 5, x, 0)
        cells[x][1] = new Cell(EM, 3, x, 1)
        cells[x][2] = new Cell(EM, 4, x, 2)
        cells[x][3] = new Cell(EM, 8, x, 3)
        cells[x][4] = new Cell(EM, 7, x, 4)
        cells[x][5] = new Cell(EM, 6, x, 5)

        x++
        cells[x][0] = new Cell(EM, 6, x, 0)
        cells[x][1] = new Cell(EM, 2, x, 1)
        cells[x][2] = new Cell(EM, 3, x, 2)
        cells[x][3] = new Cell(EM, 6, x, 3)
        cells[x][4] = new Cell(EM, 5, x, 4)
        cells[x][5] = new Cell(ME, 4, x, 5)

        cells
    }

    Cell[][] createCells_7x7() {
        int x = -1
        Cell[][] cells = new Cell[7][7]

        x++
        cells[x][0] = new Cell(OP, 1, x, 0)
        cells[x][1] = new Cell(EM, 3, x, 1)
        cells[x][2] = new Cell(EM, 2, x, 2)
        cells[x][3] = new Cell(EM, 4, x, 3)
        cells[x][4] = new Cell(EM, 8, x, 4)
        cells[x][5] = new Cell(EM, 4, x, 5)
        cells[x][6] = new Cell(EM, 3, x, 6)

        x++
        cells[x][0] = new Cell(EM, 2, x, 0)
        cells[x][1] = new Cell(EM, 4, x, 1)
        cells[x][2] = new Cell(EM, 8, x, 2)
        cells[x][3] = new Cell(EM, 6, x, 3)
        cells[x][4] = new Cell(EM, 5, x, 4)
        cells[x][5] = new Cell(EM, 2, x, 5)
        cells[x][6] = new Cell(EM, 1, x, 6)

        x++
        cells[x][0] = new Cell(EM, 1, x, 0)
        cells[x][1] = new Cell(EM, 7, x, 1)
        cells[x][2] = new Cell(EM, 5, x, 2)
        cells[x][3] = new Cell(EM, 4, x, 3)
        cells[x][4] = new Cell(EM, 8, x, 4)
        cells[x][5] = new Cell(EM, 4, x, 5)
        cells[x][6] = new Cell(EM, 3, x, 6)

        x++
        cells[x][0] = new Cell(EM, 2, x, 0)
        cells[x][1] = new Cell(EM, 2, x, 1)
        cells[x][2] = new Cell(EM, 2, x, 2)
        cells[x][3] = new Cell(EM, 3, x, 3)
        cells[x][4] = new Cell(EM, 1, x, 4)
        cells[x][5] = new Cell(EM, 5, x, 5)
        cells[x][6] = new Cell(EM, 6, x, 6)

        x++
        cells[x][0] = new Cell(EM, 5, x, 0)
        cells[x][1] = new Cell(EM, 3, x, 1)
        cells[x][2] = new Cell(EM, 4, x, 2)
        cells[x][3] = new Cell(EM, 8, x, 3)
        cells[x][4] = new Cell(EM, 7, x, 4)
        cells[x][5] = new Cell(EM, 4, x, 5)
        cells[x][6] = new Cell(EM, 8, x, 6)

        x++
        cells[x][0] = new Cell(EM, 1, x, 0)
        cells[x][1] = new Cell(EM, 3, x, 1)
        cells[x][2] = new Cell(EM, 4, x, 2)
        cells[x][3] = new Cell(EM, 6, x, 3)
        cells[x][4] = new Cell(EM, 7, x, 4)
        cells[x][5] = new Cell(EM, 3, x, 5)
        cells[x][6] = new Cell(EM, 5, x, 6)

        x++
        cells[x][0] = new Cell(EM, 6, x, 0)
        cells[x][1] = new Cell(EM, 2, x, 1)
        cells[x][2] = new Cell(EM, 3, x, 2)
        cells[x][3] = new Cell(EM, 6, x, 3)
        cells[x][4] = new Cell(EM, 5, x, 4)
        cells[x][5] = new Cell(EM, 6, x, 5)
        cells[x][6] = new Cell(ME, 4, x, 6)

        cells
    }

    Cell[][] createCells_12x12() {
        int x = -1
        Cell[][] cells = new Cell[12][12]

        x++
        cells[x][0] = new Cell(OP, 4, x, 0)
        cells[x][1] = new Cell(EM, 2, x, 1)
        cells[x][2] = new Cell(EM, 1, x, 2)
        cells[x][3] = new Cell(EM, 6, x, 3)
        cells[x][4] = new Cell(EM, 5, x, 4)
        cells[x][5] = new Cell(EM, 4, x, 5)
        cells[x][6] = new Cell(EM, 1, x, 6)
        cells[x][7] = new Cell(EM, 6, x, 7)
        cells[x][8] = new Cell(EM, 2, x, 8)
        cells[x][9] = new Cell(EM, 9, x, 9)
        cells[x][10] = new Cell(EM, 5, x, 10)
        cells[x][11] = new Cell(EM, 4, x, 11)

        x++
        cells[x][0] = new Cell(EM, 5, x, 0)
        cells[x][1] = new Cell(EM, 7, x, 1)
        cells[x][2] = new Cell(EM, 5, x, 2)
        cells[x][3] = new Cell(EM, 5, x, 3)
        cells[x][4] = new Cell(EM, 4, x, 4)
        cells[x][5] = new Cell(EM, 9, x, 5)
        cells[x][6] = new Cell(EM, 7, x, 6)
        cells[x][7] = new Cell(EM, 9, x, 7)
        cells[x][8] = new Cell(EM, 7, x, 8)
        cells[x][9] = new Cell(EM, 3, x, 9)
        cells[x][10] = new Cell(EM, 2, x, 10)
        cells[x][11] = new Cell(EM, 9, x, 11)

        x++
        cells[x][0] = new Cell(EM, 1, x, 0)
        cells[x][1] = new Cell(EM, 4, x, 1)
        cells[x][2] = new Cell(EM, 8, x, 2)
        cells[x][3] = new Cell(EM, 5, x, 3)
        cells[x][4] = new Cell(EM, 5, x, 4)
        cells[x][5] = new Cell(EM, 3, x, 5)
        cells[x][6] = new Cell(EM, 5, x, 6)
        cells[x][7] = new Cell(EM, 3, x, 7)
        cells[x][8] = new Cell(EM, 5, x, 8)
        cells[x][9] = new Cell(EM, 6, x, 9)
        cells[x][10] = new Cell(EM, 5, x, 10)
        cells[x][11] = new Cell(EM, 3, x, 11)

        x++
        cells[x][0] = new Cell(EM, 4, x, 0)
        cells[x][1] = new Cell(EM, 1, x, 1)
        cells[x][2] = new Cell(EM, 3, x, 2)
        cells[x][3] = new Cell(EM, 9, x, 3)
        cells[x][4] = new Cell(EM, 7, x, 4)
        cells[x][5] = new Cell(EM, 9, x, 5)
        cells[x][6] = new Cell(EM, 3, x, 6)
        cells[x][7] = new Cell(EM, 7, x, 7)
        cells[x][8] = new Cell(EM, 2, x, 8)
        cells[x][9] = new Cell(EM, 8, x, 9)
        cells[x][10] = new Cell(EM, 8, x, 10)
        cells[x][11] = new Cell(EM, 7, x, 11)

        x++
        cells[x][0] = new Cell(EM, 8, x, 0)
        cells[x][1] = new Cell(EM, 6, x, 1)
        cells[x][2] = new Cell(EM, 2, x, 2)
        cells[x][3] = new Cell(EM, 9, x, 3)
        cells[x][4] = new Cell(EM, 9, x, 4)
        cells[x][5] = new Cell(EM, 8, x, 5)
        cells[x][6] = new Cell(EM, 9, x, 6)
        cells[x][7] = new Cell(EM, 1, x, 7)
        cells[x][8] = new Cell(EM, 7, x, 8)
        cells[x][9] = new Cell(EM, 2, x, 9)
        cells[x][10] = new Cell(EM, 6, x, 10)
        cells[x][11] = new Cell(EM, 9, x, 11)

        x++
        cells[x][0] = new Cell(EM, 7, x, 0)
        cells[x][1] = new Cell(EM, 3, x, 1)
        cells[x][2] = new Cell(EM, 3, x, 2)
        cells[x][3] = new Cell(EM, 4, x, 3)
        cells[x][4] = new Cell(EM, 8, x, 4)
        cells[x][5] = new Cell(EM, 5, x, 5)
        cells[x][6] = new Cell(EM, 9, x, 6)
        cells[x][7] = new Cell(EM, 4, x, 7)
        cells[x][8] = new Cell(EM, 7, x, 8)
        cells[x][9] = new Cell(EM, 9, x, 9)
        cells[x][10] = new Cell(EM, 5, x, 10)
        cells[x][11] = new Cell(EM, 2, x, 11)

        x++
        cells[x][0] = new Cell(EM, 9, x, 0)
        cells[x][1] = new Cell(EM, 3, x, 1)
        cells[x][2] = new Cell(EM, 4, x, 2)
        cells[x][3] = new Cell(EM, 4, x, 3)
        cells[x][4] = new Cell(EM, 8, x, 4)
        cells[x][5] = new Cell(EM, 2, x, 5)
        cells[x][6] = new Cell(EM, 8, x, 6)
        cells[x][7] = new Cell(EM, 6, x, 7)
        cells[x][8] = new Cell(EM, 1, x, 8)
        cells[x][9] = new Cell(EM, 1, x, 9)
        cells[x][10] = new Cell(EM, 1, x, 10)
        cells[x][11] = new Cell(EM, 4, x, 11)

        x++
        cells[x][0] = new Cell(EM, 5, x, 0)
        cells[x][1] = new Cell(EM, 9, x, 1)
        cells[x][2] = new Cell(EM, 6, x, 2)
        cells[x][3] = new Cell(EM, 8, x, 3)
        cells[x][4] = new Cell(EM, 6, x, 4)
        cells[x][5] = new Cell(EM, 8, x, 5)
        cells[x][6] = new Cell(EM, 8, x, 6)
        cells[x][7] = new Cell(EM, 5, x, 7)
        cells[x][8] = new Cell(EM, 2, x, 8)
        cells[x][9] = new Cell(EM, 6, x, 9)
        cells[x][10] = new Cell(EM, 4, x, 10)
        cells[x][11] = new Cell(EM, 4, x, 11)

        x++
        cells[x][0] = new Cell(EM, 6, x, 0)
        cells[x][1] = new Cell(EM, 9, x, 1)
        cells[x][2] = new Cell(EM, 9, x, 2)
        cells[x][3] = new Cell(EM, 9, x, 3)
        cells[x][4] = new Cell(EM, 1, x, 4)
        cells[x][5] = new Cell(EM, 6, x, 5)
        cells[x][6] = new Cell(EM, 8, x, 6)
        cells[x][7] = new Cell(EM, 6, x, 7)
        cells[x][8] = new Cell(EM, 2, x, 8)
        cells[x][9] = new Cell(EM, 9, x, 9)
        cells[x][10] = new Cell(EM, 4, x, 10)
        cells[x][11] = new Cell(EM, 5, x, 11)

        x++
        cells[x][0] = new Cell(EM, 4, x, 0)
        cells[x][1] = new Cell(EM, 6, x, 1)
        cells[x][2] = new Cell(EM, 9, x, 2)
        cells[x][3] = new Cell(EM, 6, x, 3)
        cells[x][4] = new Cell(EM, 3, x, 4)
        cells[x][5] = new Cell(EM, 3, x, 5)
        cells[x][6] = new Cell(EM, 6, x, 6)
        cells[x][7] = new Cell(EM, 3, x, 7)
        cells[x][8] = new Cell(EM, 1, x, 8)
        cells[x][9] = new Cell(EM, 5, x, 9)
        cells[x][10] = new Cell(EM, 3, x, 10)
        cells[x][11] = new Cell(EM, 9, x, 11)

        x++
        cells[x][0] = new Cell(EM, 8, x, 0)
        cells[x][1] = new Cell(EM, 5, x, 1)
        cells[x][2] = new Cell(EM, 3, x, 2)
        cells[x][3] = new Cell(EM, 4, x, 3)
        cells[x][4] = new Cell(EM, 1, x, 4)
        cells[x][5] = new Cell(EM, 1, x, 5)
        cells[x][6] = new Cell(EM, 2, x, 6)
        cells[x][7] = new Cell(EM, 2, x, 7)
        cells[x][8] = new Cell(EM, 4, x, 8)
        cells[x][9] = new Cell(EM, 3, x, 9)
        cells[x][10] = new Cell(EM, 8, x, 10)
        cells[x][11] = new Cell(EM, 7, x, 11)

        x++
        cells[x][0] = new Cell(EM, 4, x, 0)
        cells[x][1] = new Cell(EM, 2, x, 1)
        cells[x][2] = new Cell(EM, 7, x, 2)
        cells[x][3] = new Cell(EM, 7, x, 3)
        cells[x][4] = new Cell(EM, 4, x, 4)
        cells[x][5] = new Cell(EM, 6, x, 5)
        cells[x][6] = new Cell(EM, 4, x, 6)
        cells[x][7] = new Cell(EM, 2, x, 7)
        cells[x][8] = new Cell(EM, 8, x, 8)
        cells[x][9] = new Cell(EM, 7, x, 9)
        cells[x][10] = new Cell(EM, 1, x, 10)
        cells[x][11] = new Cell(ME, 6, x, 11)

        cells
    }

}
