"use strict";

var CANVAS_ID = "game-canvas";
var CANVAS_CONTEXT = "2d";
var LEFT_TEXT_ALIGN = "left";

var FIRST_PLAYER_CELLS_COLOR = "green";
var SECOND_PLAYER_CELLS_COLOR = "blue";
var BOARD_GRID_COLOR = "black";

var VALID_CELLS_WIDTH = 1;

var VALID_MOVES_ANIMATIOON_DURATION = 1000;
var BUSY_CELL_ANIMATIOON_DURATION = 1000;
var OPPONENT_TURN_ANIMATION_DURATION_MS = 1000;
var CAPTURING_CELLS_ANIMATION_DURATION = 400;
var HAND_ICON_ANIMATION_SPEED = 1;
var HAND_ICON_ANIMATION_MOVES_MAX = 5;
var HAND_ICON_ANIMATION_START_MOVES = HAND_ICON_ANIMATION_MOVES_MAX + HAND_ICON_ANIMATION_SPEED;
var POSSIBLE_CELLS_ANIMATION_INTERVAL_MS = 1000 * 6;
var POSSIBLE_CELLS_ANIMATION_DURATION = 1000 * 3;
var POSSIBLE_CELLS_ANIMATION_SPEED = 1;
var POSSIBLE_CELLS_ANIMATION_VALUE_MIN = -3;
var POSSIBLE_CELLS_ANIMATION_VALUE_MAX = 1;
var POSSIBLE_CELLS_ANIMATION_VALUE_STARTED = 1;

var fontsByLocale = {
  "en": '"Showcard Gothic"',
  "ru": '"Showcard gothic cyrillic"'
}
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
var CAPTURED_OPACITY_CELL = 0.25;
var TIMER_INTERVAL = 90;
var FRUIT_IMG_SIZE = 38;
var CELL_SIZE = 38;

var BOARD_X = 0;
var BOARD_Y = 0;
var boardWidth;
var boardHeight;

var imgGameScreen = new Image();
var fruitsImage = new Image();
var handImage = new Image();
var imgWarning = new Image();
var imgDefeat = new Image();
var imgVictory = new Image();
var imgDraw = new Image();
var imgBtnNext = new Image();
var imgSurrender = new Image();

var canvas;
var ctx;
var timerId;
var animation = {};
var game;
var oldGame;
var capturedCellsAnimation;
var movesCounter;
var maxTimerProgressWidth;
var gameWindowMayBeClosed;

function initGameUi() {
  maxTimerProgressWidth = $('#time-progress').width();
  canvas = document.getElementById(CANVAS_ID);
  ctx = canvas.getContext(CANVAS_CONTEXT);

  $("#" + CANVAS_ID).on("click", canvasClicked);
  $('#player-surrender').on("click", surrenderClicked);
  $('#subwindow-close').on("click", onSubwindowClose);
  $('#subwindow-btn-next').on("click", onSubwindowClose);

  preloadGameSecondImages();
  setImagesOnTags();
}

function processGameStartedOperation(newGame) {
  window.game = newGame;
  $(".background").css("background-image", "url(" + imgGameScreen.src + ")");

  canvas = document.getElementById(CANVAS_ID);
  CELL_SIZE = getCanvasWidth() / getCellsCount();
  boardWidth = CELL_SIZE * getCellsCount();
  boardHeight = boardWidth;

  canvas.width = getCanvasWidth();
  canvas.height = getCanvasHeight();

  resetGameInfo();
  if (game.currentPlayer.id === userInfo.id) {
    resetOpponentTurnAnimation();
  }

  resetGameRequestUi();
  switchToGameWindow();
  resetProps(newGame);
  processGameChangedOperation(newGame);
}

function resetProps(game) {
  capturedCellsAnimation = {};
  for (var i = 0; i < game.players.length; i++) {
    capturedCellsAnimation[game.players[i].id] = {};
    capturedCellsAnimation[game.players[i].id].cells = [];
    capturedCellsAnimation[game.players[i].id].started = Date.now();
  }

  movesCounter = 0;
  animation.busyCellsStart = 0;
  animation.validMovesStart = 0;
  resetPossibleCellsAnimation();
}

function resetPossibleCellsAnimation() {
  animation.possibleCellsEnabled = false;
  animation.possibleCellsLastMs = Date.now();
}

function resetGameInfo() {
  gameWindowMayBeClosed = false;
  $('#subwindow-background').hide();
  hideConfirmWindow();
}

function preloadGameSecondImages() {
  fruitsImage.src = "/img/fruits.png";
  handImage.src = "/img/hand.png";
  imgGameScreen.src = '/img/components/game-background.png';
  imgWarning.src = '/img/components/window_warning.' + browserLocale + '.png';
  imgDefeat.src = '/img/components/window_defeat.' + browserLocale + '.png';
  imgVictory.src = '/img/components/window_victory.' + browserLocale + '.png';
  imgDraw.src = '/img/components/window_draw.' + browserLocale + '.png';
  imgBtnNext.src = '/img/components/button_next.' + browserLocale + '.png';
  imgSurrender.src = '/img/components/surrender.' + browserLocale + '.png';
}

function setImagesOnTags() {
  $('#player-surrender-img').attr('src', imgSurrender.src);
  $('#subwindow-btn-next').css("background-image", "url(" + imgBtnNext.src + ")");
  $('#warnwindow-container').css("background-image", "url(" + imgWarning.src + ")");
}

function processGameChangedOperation(newGame) {
  oldGame = window.game;
  window.game = newGame;
  killGameTimer();
  movesCounter += 1;

  newGame.incomingTime = Date.now();

  fillBoardWithCoords();
  game.board.cells = reverseBoard();
  resetPossibleCellsAnimation();
  prepareCapturedCellsAnimation(oldGame, newGame);
  paintGame(newGame);

  timerId = setInterval(
    function() {
      preparePossibleCellsAnimation(newGame);
      paintGame(newGame);
    },
    TIMER_INTERVAL);
}

function switchToGameWindow() {
  $("#lobby-window").hide();
  $("#game-window").show();
}

function showConfirmWindow(yesFunction, noFunction, text) {
  $("#warnwindow-text").text(text);

  $("#warnwindow-yes").on("click", yesFunction);
  $("#warnwindow-no").on("click", noFunction);

  $("#warnwindow-background").show();
}

function hideConfirmWindow() {
  $("#warnwindow-background").hide();
}

function onSubwindowClose(e) {
  $(".background").css("background-image", "url(" + imgLobbyScreen.src + ")");
  $("#game-window").hide();
  $("#lobby-window").show();
}

function surrenderClicked() {
  showConfirmWindow(onSurrender, hideConfirmWindow, localize('concede-confirmation'));
}

function onSurrender() {
  var surrenderPayload = {
    type: SURRENDER_GAME_ACTION
  };
  sendGameAction(surrenderPayload);

  hideConfirmWindow();
}

function killGameTimer() {
  if (timerId != null) {
    clearInterval(timerId);
  }
}

function canvasClicked(e) {
  var x = e.offsetX;
  var y = e.offsetY;

  if (x >= BOARD_X && x < BOARD_X + boardWidth &&
      y >= BOARD_Y && y < BOARD_Y + boardHeight) {
    gameBoardClicked(x, y);
  }
}

function gameBoardClicked(x, y) {
  if (game.finished) {
    return;
  }

  if (game.currentPlayer.id !== userInfo.id) {
    startOpponentTurnAnimation();
    return;
  } else {
    resetOpponentTurnAnimation();
  }

  var xCellIndex = Math.floor(x / CELL_SIZE);
  var yCellIndex = Math.floor(y / CELL_SIZE);

  var cells = game.board.cells;
  var targetMoveCell = cells[xCellIndex][yCellIndex];
  var opponentCellType = findOpponentCellType(userInfo.id, game);
  var validMoveCells = findValidMoveCells(userInfo.id, game);

  if (isMoveValid(xCellIndex, yCellIndex, validMoveCells)) {
    if (game.reversedBoard) {
      xCellIndex = game.board.cells.length - xCellIndex - 1;
      yCellIndex = game.board.cells[xCellIndex].length - yCellIndex - 1;
    }

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
    animation.handReverseMoving = HAND_ICON_ANIMATION_SPEED;
    animation.handValue = HAND_ICON_ANIMATION_START_MOVES;
    animation.validMovesStart = Date.now();
  }
}

function startOpponentTurnAnimation() {
  animation.opponentTurnStartedMs = Date.now();
}

function resetOpponentTurnAnimation() {
  animation.opponentTurnStartedMs = undefined;
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

function reverseBoard() {
  var startedCell = getPlayerStartedCell(game, userInfo.id);
  game.reversedBoard = startedCell.x !== 0;

  if (!game.reversedBoard) {
    return game.board.cells;
  }

  var cells = game.board.cells;
  var reversedCells = [];

  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];
    reversedCells.push([]);

    for (var y = 0; y < row.length; y++) {
      reversedCells[x].push([]);
    }
  }

  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];
    for (var y = 0; y < row.length; y++) {
      var reversedCell = row[y];
      reversedCell.x = cells.length - reversedCell.x - 1;
      reversedCell.y = row.length - reversedCell.y - 1;

      reversedCells[reversedCell.x][reversedCell.y] = reversedCell;
    }
  }

  return reversedCells;
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
    gameWindowMayBeClosed = true;
    hideConfirmWindow();
  }

  canvas.width = getCanvasWidth();

  paintPlayers(game);
  paintBoard(game);
  paintPossibleCellsAnimation(game);
  paintBoardGrid(game);
  paintTips(game);
  paintHelpAnimation();
  paintOpponentTurnAnimation();
  paintBusyCellsAnimation();
  if (!game.finished) {
    paintCellsCapturingAnimation();
  }
  paintWinner(game);
}

function prepareCapturedCellsAnimation(oldGame, newGame) {
  if (oldGame === undefined || newGame === undefined || newGame.finished) {
    return;
  }

  for (var i = 0; i < game.players.length; i++) {
    capturedCellsAnimation[game.players[i].id].cells = [];
  }

  var cells = newGame.board.cells;
  var playerIdLastChanges = findPlayerIdBoardChanges(oldGame, newGame, cells);
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];

    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      if (cell.owner === playerIdLastChanges) {
        capturedCellsAnimation[cell.owner].cells.push(cell);
        capturedCellsAnimation[cell.owner].started = Date.now();
      }
    }
  }
}

function preparePossibleCellsAnimation(newGame) {
  if (newGame.finished) {
    return;
  }

  var validMovesTimeout = Date.now() - animation.possibleCellsLastMs;
  if (validMovesTimeout > POSSIBLE_CELLS_ANIMATION_INTERVAL_MS) {
    animation.possibleCellsSpeed = POSSIBLE_CELLS_ANIMATION_SPEED;
    animation.possibleCellsValue = POSSIBLE_CELLS_ANIMATION_VALUE_STARTED;
    animation.possibleCellsLastMs = Date.now();
    animation.possibleCellsEnabled = true;
  } else if (validMovesTimeout > POSSIBLE_CELLS_ANIMATION_DURATION) {
    animation.possibleCellsEnabled = false;
  }
}

function findPlayerIdBoardChanges(oldGame, newGame, cells) {
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];

    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      if (cell.owner !== oldGame.board.cells[x][y].owner) {
        return cell.owner;
      }
    }
  }
}

function paintPlayers(game) {
  var leftPlayerIndex;
  var rightPlayerIndex;
  for (var i = 0; i < game.players.length; i++) {
    var player = game.players[i];
    if (userInfo.id === player.id) {
      leftPlayerIndex = i;
    } else {
      rightPlayerIndex = i;
    }
  }

  paintPlayer(game.players[leftPlayerIndex], game, "left");
  paintPlayer(game.players[rightPlayerIndex], game, "right");
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
  $('#' + playerSide + '-pl-info').attr("data-original-title", concatGameStats(player));

  // If game is going on
  if (!game.finished) {
    // Active player
    if (player.id === game.currentPlayer.id) {
      $('#' + playerSide + '-pl-shadow').show();
    } else {
      $('#' + playerSide + '-pl-shadow').hide();
    }

    // Timer
    if (!isTutorialGame()) {
      var moveTimeLeft = game.clientCurrentMoveTimeLeft - (Date.now() - game.incomingTime);
      var timerProgressWidth = maxTimerProgressWidth * moveTimeLeft / game.timePerMoveMs;
      $('#time-progress').width(timerProgressWidth);
    }
  }

  if (game.finished) {
    $('#' + playerSide + '-pl-info').removeClass("player-active");
  }
}

function paintBoard(game) {
  // Fill background
  ctx.fillStyle = "white";
  ctx.globalAlpha = 1;
  ctx.fillRect(BOARD_X, BOARD_Y, boardWidth, boardHeight);

  var cells = game.board.cells;
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];

    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      var fruitImgCoords = getImageCoordinates(cell);

      ctx.drawImage(
        fruitsImage,
        fruitImgCoords.x, fruitImgCoords.y, FRUIT_IMG_SIZE, FRUIT_IMG_SIZE,
        x * CELL_SIZE, y * CELL_SIZE + BOARD_Y, CELL_SIZE, CELL_SIZE);

      if (cell.owner) {
        if (cell.owner === game.players[0].id) {
          ctx.fillStyle = FIRST_PLAYER_CELLS_COLOR;
        } else {
          ctx.fillStyle = SECOND_PLAYER_CELLS_COLOR;
        }

        ctx.globalAlpha = CAPTURED_OPACITY_CELL;
        ctx.fillRect(x * CELL_SIZE, y * CELL_SIZE + BOARD_Y, CELL_SIZE, CELL_SIZE);
        ctx.globalAlpha = 1;
      }
    }
  }
}

function paintPossibleCellsAnimation(game) {
  if (game.finished || !isClientMove(game) || !animation.possibleCellsEnabled) {
    return;
  }

  var validMoveCells = findValidMoveCells(userInfo.id, game);
  var cells = game.board.cells;
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];

    for (var y = 0; y < row.length; y++) {
      var cell = row[y];
      if (!isMoveValid(x, y, validMoveCells)) {
        continue;
      }

      var fruitImgCoords = getImageCoordinates(cell);

      // Fill background
      ctx.fillStyle = "white";
      ctx.globalAlpha = 1;
      ctx.fillRect(x * CELL_SIZE, y * CELL_SIZE + BOARD_Y, CELL_SIZE, CELL_SIZE);

      // Paint zoomed fruit
      ctx.drawImage(
        fruitsImage,
        fruitImgCoords.x, fruitImgCoords.y, FRUIT_IMG_SIZE, FRUIT_IMG_SIZE,
        x * CELL_SIZE - animation.possibleCellsValue,
        y * CELL_SIZE + BOARD_Y - animation.possibleCellsValue,
        CELL_SIZE + animation.possibleCellsValue * 2,
        CELL_SIZE + animation.possibleCellsValue * 2);
    }
  }

  // Change animation values
  if (animation.possibleCellsValue <= POSSIBLE_CELLS_ANIMATION_VALUE_MIN) {
    animation.possibleCellsSpeed = Math.abs(animation.possibleCellsSpeed);
  } else if (animation.possibleCellsValue >= POSSIBLE_CELLS_ANIMATION_VALUE_MAX) {
    animation.possibleCellsSpeed = -Math.abs(animation.possibleCellsSpeed);
  }
  animation.possibleCellsValue += animation.possibleCellsSpeed;
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
  ctx.lineTo(BOARD_X + boardWidth, 0);
  ctx.stroke();

  // Left border
  ctx.beginPath();
  ctx.moveTo(0, 0);
  ctx.lineTo(0, BOARD_Y + boardHeight);
  ctx.stroke();
}

function getImageCoordinates(cell) {
  return imageCoordinates[cell.type];
}

function isTutorialGame() {
  return userInfo.wins < 1;
}

function paintTips(game) {
  if (game.finished || !isTutorialGame() || !isClientMove(game)) {
    return;
  }

  var cells = game.board.cells;
  var addingTipHeight = getCanvasTipsParams().addingTipHeight;
  var tipY = cells[0].length * CELL_SIZE / 2 - addingTipHeight;
  var tipX = cells.length * CELL_SIZE / 2;

  var playerMovesCount = Math.ceil(movesCounter / 2);
  var i = 1;

  if (playerMovesCount === i++) {
    tipY -= addingTipHeight * 2;

    var tip = localize('tutor.1.1');
    tipY += addingTipHeight;
    paintStrokedText(tip, tipX, tipY);

    tip = localize('tutor.1.2l') + ' ' + localize('tutor.1.3');
    tipY += addingTipHeight;
    paintStrokedText(tip, tipX, tipY);

    tip = localize('tutor.1.4');
    tipY += addingTipHeight;
    paintStrokedText(tip, tipX, tipY);

    tip = localize('tutor.1.5');
    tipY += addingTipHeight;
    paintStrokedText(tip, tipX, tipY);

    tip = localize('tutor.1.6');
    tipY += addingTipHeight;
    paintStrokedText(tip, tipX, tipY);
  } else if (playerMovesCount === i++) {
    tip = localize('tutor.2.1');
    tipY += addingTipHeight;
    paintStrokedText(tip, tipX, tipY);

    tip = localize('tutor.2.2');
    tipY += addingTipHeight;
    paintStrokedText(tip, tipX, tipY);
  } else if (playerMovesCount === i++) {
    tip = localize('tutor.3.1');
    tipY += addingTipHeight;
    paintStrokedText(tip, tipX, tipY);

    tip = localize('tutor.3.2');
    tipY += addingTipHeight;
    paintStrokedText(tip, tipX, tipY);
  } else if (playerMovesCount === i++) {
    tip = localize('tutor.4.1');
    tipY += addingTipHeight;
    paintStrokedText(tip, tipX, tipY);

    tip = localize('tutor.4.2');
    tipY += addingTipHeight;
    paintStrokedText(tip, tipX, tipY);
  } else if (playerMovesCount === i++) {
    tip = localize('tutor.5.1');
    tipY += addingTipHeight;
    paintStrokedText(tip, tipX, tipY);

    tip = localize('tutor.5.2');
    tipY += addingTipHeight;
    paintStrokedText(tip, tipX, tipY);
  } else if (playerMovesCount === i++) {
    tip = localize('tutor.6');
    tipY += addingTipHeight;
    paintStrokedText(tip, tipX, tipY);
  }
}

function paintWinner(game) {
  if (!game.finished) {
    return;
  }

  var gameResult;
  if (game.winner) {
    if (game.winner.id === userInfo.id) {
      gameResult = 'win';
    } else {
      gameResult = 'defeat';
    }
  } else {
    gameResult = "draw";
  }

  // Cells count
  var playerCellsCount = findPlayerCells(userInfo.id, game).length;
  var opponentId = findOpponentId();
  var opponentCellsCount = findPlayerCells(opponentId, game).length;
  var text = playerCellsCount + "/" + opponentCellsCount;
  $('#subwindow-cells').text(text);

  // Game points
  var player = findSelfPlayer();
  var addedScore = player.addedScore;
  if (addedScore > -1) {
    addedScore = "+" + addedScore;
  }
  text = localize('score') + ": " + addedScore;
  $('#subwindow-points').text(text);

  // Set player images
  $('#subwindow-left-pl-img').attr('src', $('#left-pl-img').attr('src'));
  $('#subwindow-right-pl-img').attr('src', $('#right-pl-img').attr('src'));

  // Set player names
  $('#subwindow-left-pl-name').text($('#left-pl-name').text());
  $('#subwindow-right-pl-name').text($('#right-pl-name').text());

  // Set background image
  if (gameResult === "win") {
    $('#subwindow-container').css("background-image", "url(" + imgVictory.src + ")");
  } else if (gameResult === "defeat") {
    $('#subwindow-container').css("background-image", "url(" + imgDefeat.src + ")");
  } else {
    $('#subwindow-container').css("background-image", "url(" + imgDraw.src + ")");
  }

  $('#subwindow-background').show();
}


function paintStrokedText(text, x, y) {
  ctx.textAlign = "center";
  ctx.font = getCanvasTipsParams().fontSize + ' ' + fontsByLocale[browserLocale];
  ctx.strokeStyle = 'black';
  ctx.lineWidth = getCanvasTipsParams().lineWidth;
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

function paintHelpAnimation() {
  var validMovesTimeout = animation.validMovesStart + VALID_MOVES_ANIMATIOON_DURATION - Date.now();
  if (validMovesTimeout < 0) {
    return;
  }

  // Find only one cell (to highlight).
  var cells = game.board.cells;
  var highlightSum = 999;
  var highlightCells = [];
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];
    for (var y = 0; y < row.length; y++) {
      var cell = row[y];
      if (animation.validMoves.includes(cell)) {
        if (highlightSum > findCenterRange(x, y, cells)) {
          highlightSum = findCenterRange(x, y, cells);
          highlightCells = [];
          highlightCells.push(cell);
        } else if (highlightSum === findCenterRange(x, y, cells)) {
          highlightCells.push(cell);
        }
      }
    }
  }
  if (highlightCells.length === 0) {
    return;
  }

  var recommendedCell = highlightCells[Math.floor(highlightCells.length / 2)];

  darkenCells([recommendedCell]);

  // Paint help circles
  var percentOfTimeout = validMovesTimeout / VALID_MOVES_ANIMATIOON_DURATION;
  var radius = CELL_SIZE * 1.5 * percentOfTimeout;
  ctx.beginPath();
  ctx.strokeStyle = "rgb(65,242,21)";
  ctx.lineWidth = VALID_CELLS_WIDTH;
  ctx.arc(recommendedCell.x * CELL_SIZE + CELL_SIZE / 2, recommendedCell.y * CELL_SIZE + CELL_SIZE / 2, radius, 0, 2 * Math.PI, true);

  radius -= 5;
  if (radius > 0) {
    ctx.arc(recommendedCell.x * CELL_SIZE + CELL_SIZE / 2, recommendedCell.y * CELL_SIZE + CELL_SIZE / 2, radius, 0, 2 * Math.PI, true);
  }
  ctx.stroke();

  // Paint hand icon.
  if (animation.handValue <= 0) {
    animation.handReverseMoving = Math.abs(animation.handReverseMoving);
  } else if (animation.handValue >= HAND_ICON_ANIMATION_MOVES_MAX) {
    animation.handReverseMoving = -Math.abs(animation.handReverseMoving);
  }
  animation.handValue += animation.handReverseMoving;

  ctx.drawImage(
    handImage,
    recommendedCell.x * CELL_SIZE + CELL_SIZE / 2,
    recommendedCell.y * CELL_SIZE + BOARD_Y + CELL_SIZE / 2,
    CELL_SIZE + animation.handValue,
    CELL_SIZE + animation.handValue);
}

function paintBusyCellsAnimation() {
  var busyCellsTimeout = animation.busyCellsStart + BUSY_CELL_ANIMATIOON_DURATION - Date.now();
  if (busyCellsTimeout < 0) {
    return;
  }
  var cells = game.board.cells;

  for (var i = 0; i < animation.busyCells.length; i++) {
    var cell = animation.busyCells[i];

    ctx.fillStyle = "rgba(0, 0, 0, 0.5)";
    ctx.fillRect(cell.x * CELL_SIZE, cell.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

    var centerX = cells[0].length * CELL_SIZE / 2;
    var centerY = cells.length * CELL_SIZE / 2;
    paintStrokedText(localize('fruitIsOccupied'), centerX, centerY);
  }
}

function paintOpponentTurnAnimation() {
  if (animation.opponentTurnStartedMs === undefined) {
    return;
  }

  if (Date.now() - animation.opponentTurnStartedMs > OPPONENT_TURN_ANIMATION_DURATION_MS) {
    resetOpponentTurnAnimation();
    return;
  }

  var cells = game.board.cells;
  var addingTipHeight = getCanvasTipsParams().addingTipHeight;
  var tipY = cells[0].length * CELL_SIZE / 2 - addingTipHeight;
  var tipX = cells.length * CELL_SIZE / 2;

  tipY += addingTipHeight;
  paintStrokedText(localize('opponentTurn'), tipX, tipY);
}

function darkenCells(exceptCells) {
  var cells = game.board.cells;
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];
    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      if (!exceptCells.includes(cell)) {
        ctx.fillStyle = "rgba(0, 0, 0, 0.5)";
        ctx.fillRect(cell.x * CELL_SIZE, cell.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
      }
    }
  }
}

function paintCellsCapturingAnimation() {
  for (var playerId = 0; playerId < game.players.length; playerId++) {
    var player = game.players[playerId];

    var timeout = capturedCellsAnimation[player.id].started + CAPTURING_CELLS_ANIMATION_DURATION - Date.now();
    if (timeout < 0) {
      continue;
    }
    var percentOfTimeout = timeout / CAPTURING_CELLS_ANIMATION_DURATION;

    for (var i = 0; i < capturedCellsAnimation[player.id].cells.length; i++) {
      var cell = capturedCellsAnimation[player.id].cells[i];
      if (cell.owner) {
        if (cell.owner === game.players[0].id) {
          ctx.fillStyle = FIRST_PLAYER_CELLS_COLOR;
        } else {
          ctx.fillStyle = SECOND_PLAYER_CELLS_COLOR;
        }

        ctx.globalAlpha = percentOfTimeout;
        ctx.fillRect(cell.x * CELL_SIZE, cell.y * CELL_SIZE + BOARD_Y, CELL_SIZE, CELL_SIZE);
        ctx.globalAlpha = 1;
      }
    }
  }
}

function findCenterRange(x, y, cells) {
  return Math.abs(cells.length / 2 - x) +
    Math.abs(cells.length / 2 - y);
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

function findSelfPlayer() {
  // Works only for 2 players in game.
  if (game.players[0].id === userInfo.id) {
    return game.players[0];
  } else {
    return  game.players[1];
  }
}

function getCanvasWidth() {
  var chatOutputScrolling = document.getElementById("chat-output-scrolling");
  return chatOutputScrolling.offsetWidth;
}

function getCanvasHeight() {
  return getCanvasWidth();
}

function getCellsCount() {
  return game.board.cells.length;
}
