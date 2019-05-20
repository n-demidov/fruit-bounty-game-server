"use strict";

var CANVAS_ID = "game-canvas";
var CANVAS_CONTEXT = "2d";
var LEFT_TEXT_ALIGN = "left";

var FRUITS_IMAGE = "/img/fruits.png";
var FIRST_PLAYER_CELLS_COLOR = "green";
var SECOND_PLAYER_CELLS_COLOR = "blue";
var BOARD_GRID_COLOR = "black";

var STARTED_CELL_LINE_WIDTH = 3;
var ARROWS_LINE_WIDTH = 2;

var VALID_MOVES_ANIMATIOON_DURATION = 1000;
var BUSY_CELL_ANIMATIOON_DURATION = 1000;

var imageCoordinates = {
  1: {"x": 0, "y": 0},
  2: {"x": 38, "y": 0},
  3: {"x": 76, "y": 0},
  4: {"x": 0, "y": 38},
  5: {"x": 38, "y": 38},
  6: {"x": 76, "y": 38},
  7: {"x": 0, "y": 76},
  8: {"x": 38, "y": 76},
  9: {"x": 76, "y": 76}
};
var OPACITY_CELL = 0.11;
var TIMER_INTERVAL = 90;
var CELL_SIZE = 38;

var CELLS_COUNT = 14;
var BOARD_X = 0;
var BOARD_Y = 0;
var BOARD_WIDTH = CELL_SIZE * CELLS_COUNT;
var BOARD_HEIGHT = BOARD_WIDTH;

var CANVAS_WIDTH = BOARD_X + BOARD_WIDTH;
var CANVAS_HEIGHT = BOARD_HEIGHT;

var fruitsImage;
var canvas;
var ctx;
var timerId;
var isSurrender;
var animation = {};
var game;

function initGameUi() {
  canvas = document.getElementById(CANVAS_ID);
  ctx = canvas.getContext(CANVAS_CONTEXT);

  canvas.width = CANVAS_WIDTH;
  canvas.height = CANVAS_HEIGHT;

  $("#" + CANVAS_ID).on("click", canvasClicked);
  $('.surrender-btn').on("click", closeButtonClicked);

  fruitsImage = new Image();
  fruitsImage.src = FRUITS_IMAGE;
}

function processGameStartedOperation(game) {
  resetGameRequestUi();
  switchToGameWindow();
  processGameChangedOperation(game);

  resetGameInfo();

  // Surrender button
  if (game.players[0].id === userInfo.id) {
    $('#left-pl-surrender').show();
  } else {
    $('#right-pl-surrender').show();
  }
}

function resetGameInfo() {
  $('.added-score').hide();

  $('.surrender-btn').hide();
  $('.surrender-btn').text(localize('concede'));
  $('.player-winner').hide();
  $('.player-winner').text();
}

function processGameChangedOperation(game) {
  window.game = game;
  fillBoardWithCoords();

  killGameTimer();

  game.incomingTime = Date.now();
  paintGame(game);

  timerId = setInterval(
    function() {
      paintGame(game);
    },
    TIMER_INTERVAL);
}

function switchToGameWindow() {
  $("#lobby-window").hide();
  $("#game-window").show();
}

function closeButtonClicked(e) {
  if (e.target.innerText === localize('close')) {
    closeGameWindowClicked();
  } else {
    surrenderClicked();
  }
}

function closeGameWindowClicked() {
  $("#game-window").hide();
  $("#lobby-window").show();
}

function surrenderClicked() {
  if (confirm(localize('concede-confirmation'))) {
    var surrenderPayload = {
      type: SURRENDER_GAME_ACTION
    };

    sendGameAction(surrenderPayload);
  }
}

function killGameTimer() {
  if (timerId != null) {
    clearInterval(timerId);
  }
}

function canvasClicked(e) {
  var x = e.offsetX;
  var y = e.offsetY;

  if (x >= BOARD_X && x < BOARD_X + BOARD_WIDTH &&
      y >= BOARD_Y && y < BOARD_Y + BOARD_HEIGHT) {
    gameBoardClicked(x, y);
  }
}

function gameBoardClicked(x, y) {
  var xCellIndex = Math.floor(x / CELL_SIZE);
  var yCellIndex = Math.floor(y / CELL_SIZE);

  var cells = game.board.cells;
  var targetMoveCell = cells[xCellIndex][yCellIndex];
  var opponentCellType = findOpponentCellType(userInfo.id, game);
  var validMoveCells = findValidMoveCells(userInfo.id, game);

  if (isMoveValid(xCellIndex, yCellIndex, validMoveCells)) {
    var movePayload = {
      type: MOVE_GAME_ACTION,
      x: xCellIndex,
      y: yCellIndex
    };

    sendGameAction(movePayload);
  } else if (opponentCellType === targetMoveCell.type
    && isCellNeighbor(targetMoveCell, userInfo.id, cells)) {
    var opponentId = findOpponentId();
    animation.busyCells = findPlayerCells(opponentId, game);
    animation.busyCells.push(targetMoveCell);
    animation.busyCellsStart = Date.now();
  } else {
    animation.validMoves = validMoveCells;
    animation.validMovesStart = Date.now();
  }
}

function fillBoardWithCoords() {
  var cells = game.board.cells;
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];

    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      cell.x = x;
      cell.y = y;
    }
  }
}

function isMoveValid(x, y, validCells) {
  for (var i = 0; i < validCells.length; i++) {
    var validCell = validCells[i];

    if (validCell.x === x && validCell.y === y) {
      return  true;
    }
  }

  return false;
}

function sendGameAction(movePayload) {
  sendOperation(SEND_GAME_ACTION, movePayload);
}



function paintGame(game) {
  if (game == null) {
    return;
  }

  if (game.finished) {
    killGameTimer();
    $('.surrender-btn').text(localize('close'));
  }

  canvas.width = CANVAS_WIDTH;

  paintPlayers(game);
  paintBoard(game);
  paintBoardGrid(game);
  paintTips(game);
  drawAnimation();
}

function paintPlayers(game) {
  // hide all timers
  $('.player-timer').hide();

  paintPlayer(game.players[0], game, "left");
  paintPlayer(game.players[1], game, "right");
}

function paintPlayer(player, game, playerSide) {
  // Player's image
  var playerImage = $('#' + playerSide + '-pl-img');
  if (playerImage.attr('src') != player.img) {
    playerImage.attr('src', player.img);
  }

  // Player's name
  var playerName = $('#' + playerSide + '-pl-name');
  if (playerName.text() != player.publicName) {
    playerName.text(player.publicName);
  }

  // Other player's params
  $('#' + playerSide + '-pl-score').text(localize("score") + ": " + player.score);
  $('#' + playerSide + '-pl-wins').text(localize("wins") + ": " + player.wins);
  $('#' + playerSide + '-pl-defeats').text(localize("defeats") + ": " + player.defeats);
  $('#' + playerSide + '-pl-draws').text(localize("draws") + ": " + player.draws);

  // if game is going
  if (!game.finished && player.id === game.currentPlayer.id) {
    // Timer
    var moveTimeLeft = Math.ceil((game.clientCurrentMoveTimeLeft - (Date.now() - game.incomingTime)) / 1000);
    $('#' + playerSide + '-pl-timer').text(moveTimeLeft);
    $('#' + playerSide + '-pl-timer').show();
  }

  if (game.finished) {
    // Score
    var addedScore = player.addedScore;
    if (addedScore > -1) {
      addedScore = "+" + addedScore;
    }
    var playerAddedScore = $('#' + playerSide + '-pl-added-score');
    playerAddedScore.text(addedScore);
    playerAddedScore.show();

    // Winner
    if (game.winner && game.winner.id === player.id) {
      var winnerPlayer = $('#' + playerSide + '-pl-winner');
      winnerPlayer.text(localize('win'));
      winnerPlayer.show();
    }
  }
}

function paintBoard(game) {
  var cells = game.board.cells;
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];

    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      var fruitImgCoords = getImageCoordinates(cell);

      ctx.drawImage(
        fruitsImage,
        fruitImgCoords.x, fruitImgCoords.y, CELL_SIZE, CELL_SIZE,
        x * CELL_SIZE, y * CELL_SIZE + BOARD_Y, CELL_SIZE, CELL_SIZE);

      if (cell.owner) {
        if (cell.owner === game.players[0].id) {
          ctx.fillStyle = FIRST_PLAYER_CELLS_COLOR;
        } else {
          ctx.fillStyle = SECOND_PLAYER_CELLS_COLOR;
        }

        ctx.globalAlpha = OPACITY_CELL;
        ctx.fillRect(x * CELL_SIZE, y * CELL_SIZE + BOARD_Y, CELL_SIZE, CELL_SIZE);
        ctx.globalAlpha = 1;
      }
    }
  }
}

function paintBoardGrid(game) {
  ctx.fillStyle = BOARD_GRID_COLOR;

  var cells = game.board.cells;
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];

    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      // Right cell line
      ctx.beginPath();
      ctx.moveTo(x * CELL_SIZE + CELL_SIZE, y * CELL_SIZE + BOARD_Y);
      ctx.lineTo(x * CELL_SIZE + CELL_SIZE, y * CELL_SIZE + BOARD_Y + CELL_SIZE);
      ctx.stroke();

      // Bottom cell line
      ctx.beginPath();
      ctx.moveTo(x * CELL_SIZE, y * CELL_SIZE + BOARD_Y + CELL_SIZE);
      ctx.lineTo(x * CELL_SIZE + CELL_SIZE, y * CELL_SIZE + BOARD_Y + CELL_SIZE);
      ctx.stroke();
    }
  }

  // Top border
  ctx.beginPath();
  ctx.moveTo(0, 0);
  ctx.lineTo(BOARD_X + BOARD_WIDTH, 0);
  ctx.stroke();

  // Left border
  ctx.beginPath();
  ctx.moveTo(0, 0);
  ctx.lineTo(0, BOARD_Y + BOARD_HEIGHT);
  ctx.stroke();
}

function getImageCoordinates(cell) {
  return imageCoordinates[cell.type];
}

function paintTips(game) {
  if (userInfo.wins > 0
  || !isClientMove(game)) {
    return;
  }

  var cells = game.board.cells;
  var startedCell = getPlayerStartedCell(game, userInfo.id);
  var addingTipHeight = 40;
  var tipY = cells[0].length * CELL_SIZE / 2 - addingTipHeight * 2;
  var tipX = cells.length * CELL_SIZE / 2;

  var playerCells = countPlayerCells(cells, userInfo.id);
  if (playerCells === 1) {
    highlightStartedCell(startedCell);
    drawArrows2Neighbors(startedCell);

    var playerStartedPosition = startedCell.x === 0 ? localize('tutor.left-top') : localize('tutor.right-bottom');
    var tip = localize('tutor.you-started') + ' ' + playerStartedPosition + ' ' + localize('tutor.board-corner');

    tipY += addingTipHeight;
    drawStroked(tip, tipX, tipY);

    tip = localize('tutor.grab-adjoin-fruits');
    tipY += addingTipHeight;
    drawStroked(tip, tipX, tipY);

    tip = localize('tutor.capture-any-nearest');
    tipY += addingTipHeight;
    drawStroked(tip, tipX, tipY);
  } else if (playerCells >= 2 && playerCells <= 3) {
    tip = localize('tutor.capture-several-fruits-1');
    tipY += addingTipHeight;
    drawStroked(tip, tipX, tipY);

    tip = localize('tutor.capture-several-fruits-2');
    tipY += addingTipHeight;
    drawStroked(tip, tipX, tipY);
  } else if (playerCells >= 4 && playerCells <= 5) {
    tip = localize('tutor.can-not-capture-opponent-fruit-1');
    tipY += addingTipHeight;
    drawStroked(tip, tipX, tipY);

    tip = localize('tutor.can-not-capture-opponent-fruit-2');
    tipY += addingTipHeight;
    drawStroked(tip, tipX, tipY);
  } else if (playerCells >= 6 && playerCells <= 7) {
    tip = localize('tutor.try-collect-max-fruits');
    tipY += addingTipHeight;
    drawStroked(tip, tipX, tipY);

    tip = localize('tutor.the-max-will-win');
    tipY += addingTipHeight;
    drawStroked(tip, tipX, tipY);
  }
}

function highlightStartedCell(startedCell) {
  ctx.strokeStyle = "red";
  ctx.lineWidth = STARTED_CELL_LINE_WIDTH;

  ctx.beginPath();
  var halfCell = CELL_SIZE / 2;
  ctx.arc(startedCell.x + halfCell, startedCell.y + halfCell, halfCell, 0, 2 * Math.PI);
  ctx.stroke();
}

function drawArrows2Neighbors(startedCell) {
  ctx.beginPath();
  ctx.lineWidth = ARROWS_LINE_WIDTH;
  var coef = 1;
  if (startedCell.x != 0) {
    coef = -1;
  }

  var initArrow = startedCell.x + CELL_SIZE / 2;
  drawArrow(initArrow, initArrow, initArrow + CELL_SIZE * coef, initArrow);
  drawArrow(initArrow, initArrow, initArrow, initArrow + CELL_SIZE * coef);
  ctx.stroke();
}


/* == General functions == */
function drawArrow(fromx, fromy, tox, toy) {
  var headlen = 10;   // length of head in pixels
  var angle = Math.atan2(toy-fromy,tox-fromx);
  ctx.moveTo(fromx, fromy);
  ctx.lineTo(tox, toy);
  ctx.lineTo(tox-headlen*Math.cos(angle-Math.PI/6),toy-headlen*Math.sin(angle-Math.PI/6));
  ctx.moveTo(tox, toy);
  ctx.lineTo(tox-headlen*Math.cos(angle+Math.PI/6),toy-headlen*Math.sin(angle+Math.PI/6));
}

function drawStroked(text, x, y) {
  ctx.textAlign = "center";
  ctx.font = '20px Sans-serif';
  ctx.strokeStyle = 'black';
  ctx.lineWidth = 6;
  ctx.strokeText(text, x, y);

  ctx.fillStyle = 'white';
  ctx.fillText(text, x, y);
}

function isClientMove(game) {
  return userInfo.id === game.currentPlayer.id;
}

function getPlayerStartedCell(game, playerId) {
  if (playerId === game.players[0].id) {
    return {"x": BOARD_X, "y": BOARD_Y};
  } else {
    var cells = game.board.cells;
    return {
      "x": BOARD_X + (cells.length - 1) * CELL_SIZE,
      "y": BOARD_Y + (cells[0].length - 1) * CELL_SIZE};
  }
}

function countPlayerCells(cells, playerId) {
  var playerCells = 0;
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];

    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      if (cell.owner === playerId) {
        playerCells++;
      }
    }
  }

  return playerCells;
}

function drawAnimation() {
  var validMovesTimeout = animation.validMovesStart + VALID_MOVES_ANIMATIOON_DURATION - Date.now();
  if (validMovesTimeout > 0) {
    var percentOfTimeout = (VALID_MOVES_ANIMATIOON_DURATION - validMovesTimeout) / 100;
    var radius = CELL_SIZE * validMovesTimeout / percentOfTimeout * 0.01;

    for (var x = 0; x < animation.validMoves.length; x++) {
      var cell = animation.validMoves[x];

      ctx.beginPath();
      ctx.strokeStyle = "rgba(0, 0, 0, 1)";
      ctx.arc(cell.x * CELL_SIZE + CELL_SIZE / 2, cell.y * CELL_SIZE + CELL_SIZE / 2, radius, 0, 2 * Math.PI, true);
      ctx.stroke();
    }
  }

  var busyCellsTimeout = animation.busyCellsStart + BUSY_CELL_ANIMATIOON_DURATION - Date.now();
  if (busyCellsTimeout > 0) {
    for (var i = 0; i < animation.busyCells.length; i++) {
      cell = animation.busyCells[i];

      ctx.fillStyle = "rgba(0, 0, 0, 0.5)";
      ctx.fillRect(cell.x * CELL_SIZE, cell.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }
  }
}

function findValidMoveCells(playerId, game) {
  var result = [];

  var opponentCellType = findOpponentCellType(playerId, game);
  var cells = game.board.cells;
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];

    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      if (cell.owner === 0
        && isCellNeighbor(cell, playerId, cells)
        && cell.type !== opponentCellType) {
        result.push(cell);
      }
    }
  }

  return result;
}

function findOpponentCellType(playerId, game) {
  // Works only for 2 players in game.
  var cells = game.board.cells;
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];

    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      if (cell.owner !== 0 && cell.owner !== playerId) {
        return cell.type;
      }
    }
  }

  return -1;
}

function isCellNeighbor(cell, playerId, cells) {
  var x = cell.x;
  var y = cell.y;
  var lastXCell = cells.length - 1;
  var lastYCell = cells[0].length - 1;

  if (x > 0) {
    if (cells[x - 1][y].owner === playerId) {
      return true;
    }
  }

  if (x < lastXCell) {
    if (cells[x + 1][y].owner === playerId) {
      return true;
    }
  }

  if (y > 0) {
    if (cells[x][y - 1].owner === playerId) {
      return true;
    }
  }

  if (y < lastYCell) {
    if (cells[x][y + 1].owner === playerId) {
      return true;
    }
  }

  return false;
}

function findPlayerCells(playerId, game) {
  var result = [];
  var cells = game.board.cells;
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];

    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      if (cell.owner === playerId) {
        result.push(cell);
      }
    }
  }

  return result;
}

function findOpponentId() {
  // Works only for 2 players in game.
  if (game.players[0].id === userInfo.id) {
    return game.players[1].id;
  } else {
    return  game.players[0].id;
  }
}
