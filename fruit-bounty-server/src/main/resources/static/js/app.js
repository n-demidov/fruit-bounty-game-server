"use strict";

var LATER_SYMBOL = "&lt;";
var GRATER_SYMBOL = "&gt;";
var WEBSOCKET_ENTRY_URL = "/connect-app";
var USER_QUEUE = "/app/from_client";
var APP_ID = "722449047946247";

var TIPS_TIMER_INTERVAL = 12500;
var READY_TO_PLAY = "Play", WAITING_FOR_GAME = "Matching players...";
var ENTER_KEY_CODE = 13;
var C_KEY_CODE = 67;
var TILDE_KEY_CODE = 192;

var AUTH_OPERATION_TYPE = "Auth", FB_TYPE = "fb";
var SEND_CHAT_OPERATION_TYPE = "SendChat";
var SEND_PLAY_REQUEST_OPERATION_TYPE = "GameRequest";
var SEND_GAME_ACTION = "GameAction", MOVE_GAME_ACTION = "Move", SURRENDER_GAME_ACTION = "Surrender";
var APPLY_GAME_REQUEST = "y";
var CANCEL_GAME_REQUEST = "n";

var OP_RES_USER_INFO = "UserInfo", OP_RES_CHAT = "SendChat",
  OP_RES_GAME_STARTED = "GameStarted", OP_RES_GAME_CHANGED = "GameChanged",
  OP_RES_RATING_TABLE = "RatingTable";

var chatScrolling = $('#chat-output-scrolling');
var chatMsg = $("#chat-msg-input");

var isAuthed = false;
var loginStatus;
var stompClient = null;
var userInfo;
var isWaitingForOpponent = false;
var tipsTimerId;

function sendOperation(operationType, data) {
  var operation = {
    type: operationType,
    data: data,
  }

  stompClient.send(USER_QUEUE, {}, JSON.stringify(operation));
}

function sendAuth(loginStatus) {
  var authPayload = {
    type: FB_TYPE,
    accessToken: loginStatus.authResponse.accessToken,
    userId: loginStatus.authResponse.userID
  }

  sendOperation(AUTH_OPERATION_TYPE, authPayload);
}

function sendChat() {
  var chatMessage = chatMsg.val();

  if (chatMessage.length === 0) {
    return;
  }

  var sendChatPayload = {
    msg: chatMessage
  }
  chatMsg.val("");

  sendOperation(SEND_CHAT_OPERATION_TYPE, sendChatPayload);
}

function sendPlayRequest() {
  var sendPlayPayload;

  if (isWaitingForOpponent) {
    sendPlayPayload = {
      ack: CANCEL_GAME_REQUEST
    }

    resetGameRequestUi();
  } else {
    sendPlayPayload = {
      ack: APPLY_GAME_REQUEST
    }

    isWaitingForOpponent = true;
    $("#play").text(WAITING_FOR_GAME);
    startDisplayTips();
  }

  sendOperation(SEND_PLAY_REQUEST_OPERATION_TYPE, sendPlayPayload);
}

function resetGameRequestUi() {
  isWaitingForOpponent = false;
  $("#play").text(READY_TO_PLAY);
  stopDisplayTips();
}

function startDisplayTips() {
  killTipsTimer();

  showRandomTip();
  tipsTimerId = setInterval(showRandomTip, TIPS_TIMER_INTERVAL);
}

function stopDisplayTips() {
  killTipsTimer();
  $("#tips-content").hide();
}

function killTipsTimer() {
  if (tipsTimerId != null) {
    clearInterval(tipsTimerId);
  }
}

function showRandomTip() {
  var tip;

  if (isFirstGame()) {
    tip = tips[0];
  } else {
    tip = tips[getRandomInt(0, tips.length)];
  }

  $("#tips").html("<strong>Tips: </strong>" + tip);
  $("#tips-content").show();
}

function isFirstGame() {
  return userInfo.wins === 0 && userInfo.defeats === 0 && userInfo.draws === 0;
}

function connectToServer() {
  var socket = new SockJS(WEBSOCKET_ENTRY_URL);
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    setConnected(true);

    stompClient.subscribe('/topic/broadcast', function (operation) {
      processOperation(JSON.parse(operation.body));
    });

    stompClient.subscribe('/user/queue/to_client', function (operation) {
      processOperation(JSON.parse(operation.body));
    });

    stompClient.subscribe('/user/queue/errors', function(message) {
      postSysMsg("Got an error from the server: " + message);
    });

    sendAuth(loginStatus);
  }, function(error) {
    postSysMsg("Connection error: " + error);
    setConnected(false);
  });
}

function processOperation(operation) {
  if (operation.type === OP_RES_USER_INFO) {
    processUserInfoOperation(operation.data);
  } else if (operation.type === OP_RES_CHAT) {
    processChatOperation(operation.data);
  } else if (operation.type === OP_RES_GAME_STARTED) {
    processGameStartedOperation(operation.data);
  } else if (operation.type === OP_RES_GAME_CHANGED) {
    processGameChangedOperation(operation.data);
  } else if (operation.type === OP_RES_RATING_TABLE) {
    processRatingTableOperation(operation.data);
  } else {
    postSysMsg("Unknown operation type from server: " + operation);
  }
}

function processUserInfoOperation(data) {
  userInfo = data;

  $("#userImg").attr("src", userInfo.img);
  $("#userName").text(userInfo.name);
  $("#userScore").text("Score: " + userInfo.score);
  $("#user-info-data").attr("data-original-title", "wins: " + userInfo.wins + " defeats: " + userInfo.defeats + " draws: " + userInfo.draws);

  showMainWindow();
}

function processChatOperation(messages) {
  var isScrollChatToBottom = false;

  messages.forEach(msg => {
    var elements = new Array();
    $(".chat-msg").each(function() {
      elements.push($(this));
    });

    if (!elements.some(el => el.text() === msg)) {
      if (!isScrollChatToBottom && isNeedScrollChat()) {
        isScrollChatToBottom = true;
      }

      postMsg(msg);
    }
  });

  if (isScrollChatToBottom) {
    chatScrolling.scrollTop(chatScrolling.prop('scrollHeight'));
  }
}

function isNeedScrollChat() {
  return chatScrolling.scrollTop() + chatScrolling.prop('offsetHeight') >= chatScrolling.prop('scrollHeight');
}

function processRatingTableOperation(topRated) {
  var topRatedElement = $("#top-rated-players");

  topRatedElement.html("");

  var counter = 1;
  topRated.forEach(function(user) {
    var tooltipText = "wins: " + user.wins + " defeats: " + user.defeats + " draws: " + user.draws;
    var tooltipAttrs = "data-toggle='tooltip' data-placement='bottom' title='" + tooltipText + "'";
    
    topRatedElement.append("<tr " + tooltipAttrs + ">" +
      wrapTd(counter++ + ".") +
      wrapTd('<img class="rating-table-player-img" src="' + user.img + '">') +
      wrapTd(user.name) + wrapTd(user.score) + "</tr>");
  });

  $('[data-toggle="tooltip"]').tooltip();
}

function wrapTd(text) {
  return "<td class='cursor-default'>" + text + "</td>";
}

function getRandomInt(min, max) {
  return Math.floor(Math.random() * (max - min)) + min;
}



function initUi() {
  $("form").on('submit', function (e) {
    e.preventDefault();
  });
  $("#reconnect").click(connectToServer);
  $("#play").click(sendPlayRequest);
  $("#send").click(sendChat);

  $("#tips").on('click', '#share', share);

  $(chatMsg).on('keydown', function(e) {
    if (e.keyCode === ENTER_KEY_CODE) {
      sendChat();
    }
  });

  $('body').on('keydown', function(e) {
    if (e.altKey && e.keyCode === C_KEY_CODE
     || e.ctrlKey && e.keyCode === TILDE_KEY_CODE
     || e.keyCode === ENTER_KEY_CODE) {
      chatMsg.focus();
    }
  });

  $('[data-toggle="tooltip"]').tooltip();
  initGameUi();
}

function setConnected(connected) {
  if (connected) {
    $("#discnctPopup").hide();
  } else {
    $("#discnctPopup").show();
    killGameTimer();
    resetGameRequestUi();
  }
}

function postMsg(message) {
  $("#greetings").append("<tr><td><span class='chat-msg'>" + message + "</span></td></tr>");
}

function postSysMsg(message) {
  postMsg(message = LATER_SYMBOL + message + GRATER_SYMBOL);
}

function showMainWindow() {
  $("#main-container").show();
  $("#loading-window").hide();
}

function share() {
  FB.ui(
   {
    method: 'share',
    href: 'https://apps.facebook.com/fruit-bounty'
  }, function(response){});
}




window.fbAsyncInit = function() {
  initFb();

//  FB.AppEvents.logPageView();
//  FB.Event.subscribe('auth.authResponseChange', statusChanged);

  tryToLogin();
};

function initFb() {
  FB.init({
    appId            : APP_ID,
    version          : 'v2.12',
    autoLogAppEvents : true,
    xfbml            : false,
    cookie           : true,
    status           : false
  });
}

function tryToLogin() {
  FB.getLoginStatus(function(response) {
    if (response.status === 'connected') {
      statusChanged(response);
    } else {
      FB.login(
        function(response) {
          statusChanged(response);
        },
        {scope: 'public_profile'});
    }
  });
}

function statusChanged(logStatus) {
  if (logStatus.status != 'connected' || isAuthed) {
    return;
  }

  isAuthed = true;
  loginStatus = logStatus;

  connectToServer();
  initUi();
}



(function(d, s, id){
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) {return;}
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_US/sdk.js";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));
