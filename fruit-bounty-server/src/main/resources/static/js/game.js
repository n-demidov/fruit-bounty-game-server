"use strict";

var CANVAS_ID = "game-canvas";
var CANVAS_CONTEXT = "2d";
var LEFT_TEXT_ALIGN = "left";

var FIRST_PLAYER_CELLS_COLOR = "green";
var SECOND_PLAYER_CELLS_COLOR = "blue";
var BOARD_GRID_COLOR = "black";

var VALID_CELLS_WIDTH = 1;

var TIMER_INTERVAL = 90;
var POSSIBLE_CELLS_ANIMATION_DURATION_MS = 1000 * 3;
var VALID_MOVES_ANIMATION_DURATION_MS = POSSIBLE_CELLS_ANIMATION_DURATION_MS;
var BUSY_TYPE_ANIMATION_DURATION_MS = 1000;
var OPPONENT_TURN_ANIMATION_DURATION_MS = 1000;
var CAPTURING_CELLS_ANIMATION_DURATION_MS = TIMER_INTERVAL * 5.5;

var HAND_ICON_ANIMATION_SPEED = 2;
var HAND_ICON_ANIMATION_MOVES_MAX = 5;
var HAND_ICON_ANIMATION_START_MOVES = HAND_ICON_ANIMATION_MOVES_MAX + HAND_ICON_ANIMATION_SPEED;

var POSSIBLE_CELLS_ANIMATION_INTERVAL_BETWEEN_NEW_MS = 1000 * 6;
var POSSIBLE_CELLS_ANIMATION_SPEED = 1;
var POSSIBLE_CELLS_ANIMATION_VALUE_MIN = -3;
var POSSIBLE_CELLS_ANIMATION_VALUE_MAX = 1;
var POSSIBLE_CELLS_ANIMATION_VALUE_STARTED = 1;

var MAX_BUSY_TYPE_ANIMATION_OPACITY = 0.6;
var MIN_BUSY_TYPE_ANIMATION_OPACITY = 0.3;
var DELTA_BUSY_TYPE_OPACITY = 0.15;
var busyTypeAnimationTempVar;
var busyTypeAnimationTempVarDirect;

var CAPTURING_CELL_ANIMATION_DELTA = 0.2;
var CAPTURING_CELL_ANIMATION_MIN = CAPTURING_CELL_ANIMATION_DELTA;
var CAPTURING_CELL_ANIMATION_MAX = 0.6;

var CHANCE_TO_SHOW_ADDS_PERCENT = 100;

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
var FRUIT_IMG_SIZE = 38;
var cellSize;

var BOARD_X = 0;
var BOARD_Y = 0;
var boardWidth;
var boardHeight;

var imgGameScreen = new Image();
var fruitsImage = new Image();
var handImage = new Image();
var arrowHelperImage = new Image();
var imgWarning = new Image();
var imgDefeat = new Image();
var imgVictory = new Image();
var imgDraw = new Image();
var imgBtnNext = new Image();
var imgSurrender = new Image();
var imgUnknownUser = new Image();

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

var isDefeat = false;

function initGameUi() {
  maxTimerProgressWidth = $('#time-progress').width();
  canvas = document.getElementById(CANVAS_ID);
  ctx = canvas.getContext(CANVAS_CONTEXT);

  $("#" + CANVAS_ID).on("mousedown", canvasClicked);
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
  cellSize = getCanvasWidth() / getCellsCount();
  boardWidth = cellSize * getCellsCount();
  boardHeight = boardWidth;

  canvas.width = getCanvasWidth();
  canvas.height = getCanvasHeight();

  resetGameInfo();
  if (isCurrentTurn(game)) {
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
    capturedCellsAnimation[game.players[i].id].oldCells = [];
    capturedCellsAnimation[game.players[i].id].alpha = CAPTURING_CELL_ANIMATION_MIN;
    capturedCellsAnimation[game.players[i].id].deltaAlpha = CAPTURING_CELL_ANIMATION_DELTA;
    capturedCellsAnimation[game.players[i].id].started = Date.now();
  }

  movesCounter = 0;
  animation.busyCellsStart = 0;
  animation.validMovesStart = 0;
  resetPossibleCellsAnimation();
  resetBusyTypeAnimation();
}

function resetPossibleCellsAnimation() {
  animation.possibleCellsEnabled = false;
  animation.possibleCellsLastMs = Date.now();
}

function resetBusyTypeAnimation() {
  busyTypeAnimationTempVar = MAX_BUSY_TYPE_ANIMATION_OPACITY;
  busyTypeAnimationTempVarDirect = -1 * DELTA_BUSY_TYPE_OPACITY;
}

function resetGameInfo() {
  gameWindowMayBeClosed = false;
  $('#subwindow-background').hide();
  hideConfirmWindow();
}

function preloadGameSecondImages() {
  fruitsImage.src = IMG_PREFIX + "/img/fruits.png";
  handImage.src = IMG_PREFIX + "/img/hand.png";
  arrowHelperImage.src = IMG_PREFIX + "/img/arrow_helper.png";
  imgGameScreen.src = IMG_PREFIX + '/img/components/game-background.png';
  imgWarning.src = IMG_PREFIX + '/img/components/window_warning.' + browserLocale + '.png';
  imgDefeat.src = IMG_PREFIX + '/img/components/window_defeat.' + browserLocale + '.png';
  imgVictory.src = IMG_PREFIX + '/img/components/window_victory.' + browserLocale + '.png';
  imgDraw.src = IMG_PREFIX + '/img/components/window_draw.' + browserLocale + '.png';
  imgBtnNext.src = IMG_PREFIX + '/img/components/button_next.' + browserLocale + '.png';
  imgSurrender.src = IMG_PREFIX + '/img/components/surrender.' + browserLocale + '.png';
  imgUnknownUser.src = IMG_PREFIX + '/img/components/unknown_user.png';
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

  if (!game.finished && isPaintHelpTutorialAnimation(game)) {
    startHelpAnimation();
  }

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

function showConfirmWindow(yesFunction, noFunction, text, showInput, initInputText) {
  $("#warnwindow-text").text(text);

  $("#warnwindow-yes").on("click", yesFunction);
  $("#warnwindow-no").on("click", noFunction);

  if (showInput) {
    $('#warnwindow-input').val(initInputText);
    $('#warnwindow-text').css('top', '304px');
    $("#warnwindow-input").show();
  } else {
    $('#warnwindow-text').css('top', '324px');
    $("#warnwindow-input").hide();
  }

  $("#warnwindow-background").show();
}

function hideConfirmWindow() {
  $("#warnwindow-background").hide();
}

function onSubwindowClose(e) {
  if (isDefeat) {
    showAdds();
  } else {
    showAdds(CHANCE_TO_SHOW_ADDS_PERCENT);
  }

  $(".background").css("background-image", "url(" + imgLobbyScreen.src + ")");
  $("#game-window").hide();
  $("#lobby-window").show();
}

function surrenderClicked() {
  showConfirmWindow(onSurrender, hideConfirmWindow, localize('concede-confirmation'), false, null);
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

  if (!isCurrentTurn(game)) {
    startOpponentTurnAnimation();
    return;
  } else {
    resetOpponentTurnAnimation();
  }

  var xCellIndex = Math.floor(x / cellSize);
  var yCellIndex = Math.floor(y / cellSize);

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
    startHelpAnimation();
  }
}

function startHelpAnimation() {
  var validMoveCells = findValidMoveCells(userInfo.id, game);

  animation.validMoves = validMoveCells;
  animation.handReverseMoving = HAND_ICON_ANIMATION_SPEED;
  animation.handValue = HAND_ICON_ANIMATION_START_MOVES;
  animation.validMovesStart = Date.now();
  startPossibleCellsAnimation();
}

function continueHelpAnimation() {
  animation.validMovesStart = Date.now();
  animation.possibleCellsLastMs = Date.now();
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
  paintHelpAnimation();
  paintOpponentTurnAnimation();
  paintBusyTypeAnimation();
  if (!game.finished) {
    paintCellsCapturingAnimation();
    if (isPaintHelpTutorialAnimation(game)) {
      continueHelpAnimation();
    }
  }
  paintWinner(game);
}

function prepareCapturedCellsAnimation(oldGame, newGame) {
  if (oldGame === undefined || newGame === undefined || newGame.finished) {
    return;
  }

  for (var i = 0; i < game.players.length; i++) {
    capturedCellsAnimation[game.players[i].id].cells = [];
    capturedCellsAnimation[game.players[i].id].oldCells = [];
  }

  var cells = newGame.board.cells;
  var playerIdLastChanges = findPlayerIdBoardChanges(oldGame, newGame, cells);
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];

    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      if (cell.owner === playerIdLastChanges) {
        capturedCellsAnimation[cell.owner].cells.push(cell);
        capturedCellsAnimation[cell.owner].oldCells.push(oldGame.board.cells[x][y]);
        capturedCellsAnimation[cell.owner].alpha = CAPTURING_CELL_ANIMATION_MIN;
        capturedCellsAnimation[cell.owner].deltaAlpha = CAPTURING_CELL_ANIMATION_DELTA;
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
  if (validMovesTimeout > POSSIBLE_CELLS_ANIMATION_INTERVAL_BETWEEN_NEW_MS) {
    startPossibleCellsAnimation();
  } else if (validMovesTimeout > POSSIBLE_CELLS_ANIMATION_DURATION_MS) {
    animation.possibleCellsEnabled = false;
  }
}

function startPossibleCellsAnimation() {
  animation.possibleCellsSpeed = POSSIBLE_CELLS_ANIMATION_SPEED;
  animation.possibleCellsValue = POSSIBLE_CELLS_ANIMATION_VALUE_STARTED;
  animation.possibleCellsLastMs = Date.now();
  animation.possibleCellsEnabled = true;
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
  if (playerImage.attr('src') !== prepareServerImg(player.img)) {
    playerImage.attr('src', prepareServerImg(player.img));
  }

  // Player's name
  var playerName = $('#' + playerSide + '-pl-name');
  if (playerName.text() !== player.publicName) {
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
    var moveTimeLeft = game.clientCurrentMoveTimeLeft - (Date.now() - game.incomingTime);
    var timerProgressWidth = maxTimerProgressWidth * moveTimeLeft / game.timePerMoveMs;
    $('#time-progress').width(timerProgressWidth);
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

      drawFruit(cell);

      if (cell.owner) {
        if (cell.owner === game.players[0].id) {
          ctx.fillStyle = FIRST_PLAYER_CELLS_COLOR;
        } else {
          ctx.fillStyle = SECOND_PLAYER_CELLS_COLOR;
        }

        ctx.globalAlpha = CAPTURED_OPACITY_CELL;
        ctx.fillRect(x * cellSize, y * cellSize + BOARD_Y, cellSize, cellSize);
        ctx.globalAlpha = 1;
      }
    }
  }
}

function drawFruit(cell) {
  var fruitImgCoords = getImageCoordinates(cell);
  ctx.drawImage(
    fruitsImage,
    fruitImgCoords.x, fruitImgCoords.y, FRUIT_IMG_SIZE, FRUIT_IMG_SIZE,
    cell.x * cellSize, cell.y * cellSize + BOARD_Y, cellSize, cellSize);
}

function paintPossibleCellsAnimation(game) {
  if (game.finished || !isCurrentTurn(game) || !animation.possibleCellsEnabled) {
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

      // Fill background
      fillBackgroundCell(cell);

      // Paint zoomed fruit
      var fruitImgCoords = getImageCoordinates(cell);
      ctx.drawImage(
        fruitsImage,
        fruitImgCoords.x, fruitImgCoords.y, FRUIT_IMG_SIZE, FRUIT_IMG_SIZE,
        x * cellSize - animation.possibleCellsValue,
        y * cellSize + BOARD_Y - animation.possibleCellsValue,
        cellSize + animation.possibleCellsValue * 2,
        cellSize + animation.possibleCellsValue * 2);
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

function fillBackgroundCell(cell) {
  ctx.fillStyle = "white";
  ctx.globalAlpha = 1;
  ctx.fillRect(cell.x * cellSize, cell.y * cellSize + BOARD_Y, cellSize, cellSize);
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
      ctx.moveTo(x * cellSize + cellSize, y * cellSize + BOARD_Y);
      ctx.lineTo(x * cellSize + cellSize, y * cellSize + BOARD_Y + cellSize);
      ctx.stroke();

      // Bottom cell line
      ctx.beginPath();
      ctx.moveTo(x * cellSize, y * cellSize + BOARD_Y + cellSize);
      ctx.lineTo(x * cellSize + cellSize, y * cellSize + BOARD_Y + cellSize);
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
    isDefeat = false;
  } else if (gameResult === "defeat") {
    $('#subwindow-container').css("background-image", "url(" + imgDefeat.src + ")");
    isDefeat = true;
  } else {
    $('#subwindow-container').css("background-image", "url(" + imgDraw.src + ")");
    isDefeat = true;
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

function getPlayerStartedCell(game, playerId) {
  if (playerId === game.players[0].id) {
    return {"x": BOARD_X, "y": BOARD_Y};
  } else {
    var cells = game.board.cells;
    return {
      "x": BOARD_X + (cells.length - 1) * cellSize,
      "y": BOARD_Y + (cells[0].length - 1) * cellSize};
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
  var validMovesTimeout = animation.validMovesStart + VALID_MOVES_ANIMATION_DURATION_MS - Date.now();
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

  darkenCells(findValidMoveCells(userInfo.id, game), true);

  // Paint hand icon.
  if (animation.handValue < 0 || animation.handValue > HAND_ICON_ANIMATION_MOVES_MAX) {
    animation.handReverseMoving *= -1;
  }
  animation.handValue += animation.handReverseMoving;

  ctx.drawImage(
    handImage,
    recommendedCell.x * cellSize + cellSize *2/3 + animation.handValue,
    recommendedCell.y * cellSize + BOARD_Y + cellSize *2/3 + animation.handValue,
    cellSize,
    cellSize);

  paintShortText(localize('captureFruit'));
}

function paintBusyTypeAnimation() {
  var busyCellsTimeout = animation.busyCellsStart + BUSY_TYPE_ANIMATION_DURATION_MS - Date.now();
  if (busyCellsTimeout < 0) {
    resetBusyTypeAnimation();
    return;
  }

  for (var i = 0; i < animation.busyCells.length; i++) {
    var cell = animation.busyCells[i];

    ctx.fillStyle = "rgba(0, 0, 0, " + busyTypeAnimationTempVar + ")";
    ctx.fillRect(cell.x * cellSize, cell.y * cellSize, cellSize, cellSize);
  }

  busyTypeAnimationTempVar += busyTypeAnimationTempVarDirect;
  if (busyTypeAnimationTempVar < MIN_BUSY_TYPE_ANIMATION_OPACITY
    || busyTypeAnimationTempVar > MAX_BUSY_TYPE_ANIMATION_OPACITY) {
    busyTypeAnimationTempVarDirect *= -1;
  }

  paintShortText(localize('fruitIsOccupied'));
}

function paintOpponentTurnAnimation() {
  if (animation.opponentTurnStartedMs === undefined) {
    return;
  }

  if (Date.now() - animation.opponentTurnStartedMs > OPPONENT_TURN_ANIMATION_DURATION_MS) {
    resetOpponentTurnAnimation();
    return;
  }

  paintShortText(localize('opponentTurn'));
}

function paintShortText(text) {
  var cells = game.board.cells;
  var centerX = cells[0].length * cellSize / 2;
  var centerY = cells.length * cellSize / 2;

  paintStrokedText(text, centerX, centerY);
}

function darkenCells(exceptCells, excludeCapturedCells) {
  var cells = game.board.cells;
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];
    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      if (exceptCells.includes(cell)) {
        continue;
      }

      if (excludeCapturedCells && cell.owner === userInfo.id) {
        continue;
      }

      ctx.fillStyle = "rgba(0, 0, 0, 0.5)";
      ctx.fillRect(cell.x * cellSize, cell.y * cellSize, cellSize, cellSize);
    }
  }
}

function paintCellsCapturingAnimation() {
  for (var playerId = 0; playerId < game.players.length; playerId++) {
    var player = game.players[playerId];

    var timeout = capturedCellsAnimation[player.id].started + CAPTURING_CELLS_ANIMATION_DURATION_MS - Date.now();
    if (timeout < 0) {
      continue;
    }

    for (var i = 0; i < capturedCellsAnimation[player.id].cells.length; i++) {
      var cell = capturedCellsAnimation[player.id].cells[i];

      if (capturedCellsAnimation[player.id].deltaAlpha > 0) {
        // Redraw old fruit
        var oldCell = capturedCellsAnimation[player.id].oldCells[i];
        fillBackgroundCell(oldCell);
        drawFruit(oldCell);
      }

      if (cell.owner) {
        if (cell.owner === game.players[0].id) {
          ctx.fillStyle = FIRST_PLAYER_CELLS_COLOR;
        } else {
          ctx.fillStyle = SECOND_PLAYER_CELLS_COLOR;
        }

        var prevAlpha = ctx.globalAlpha;
        ctx.globalAlpha = capturedCellsAnimation[player.id].alpha;
        ctx.fillRect(cell.x * cellSize, cell.y * cellSize + BOARD_Y, cellSize, cellSize);
        ctx.globalAlpha = prevAlpha;
      }
    }

    capturedCellsAnimation[player.id].alpha += capturedCellsAnimation[player.id].deltaAlpha;
    if (capturedCellsAnimation[player.id].alpha >= CAPTURING_CELL_ANIMATION_MAX) {
      capturedCellsAnimation[player.id].deltaAlpha *= -1;
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

function isCurrentTurn(game) {
  return userInfo.id === game.currentPlayer.id;
}

function isTutorial() {
  return game.tutorial;
}

function isPaintHelpTutorialAnimation(game) {
  return isTutorial() && isCurrentTurn(game) && game.turnsCount <= 6;
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
