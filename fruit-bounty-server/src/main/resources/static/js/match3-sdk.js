"use strict";

var LATER_SYMBOL = "&lt;";
var GRATER_SYMBOL = "&gt;";
var WEBSOCKET_ENTRY_URL = "";
var USER_QUEUE = "/app/from_client";
var VK_SDK_URL = "https://vk.com/js/api/xd_connection.js?2";
var VK_MOBILE_SDK_URL = "https://vk.com/js/api/mobile_sdk.js";
var VK_IFRAME_WINDOW_NAME = "fXD";

var ARROW_HELPER_TIMER_INTERVAL = 40;
var TIPS_TIMER_INTERVAL = 12500;

var ENTER_KEY_CODE = 13;
var C_KEY_CODE = 67;
var TILDE_KEY_CODE = 192;

var AUTH_OPERATION_TYPE = "Auth", FB_TYPE = "fb", VK_TYPE = "vk", YA_TYPE = "ya", SB_TYPE = "sb";
var SEND_CHAT_OPERATION_TYPE = "SendChat";
var SEND_PLAY_REQUEST_OPERATION_TYPE = "GameRequest";
var RENAME_OPERATION_TYPE = "Rename";
var SEND_GAME_ACTION = "GameAction", MOVE_GAME_ACTION = "Move", SURRENDER_GAME_ACTION = "Surrender";
var APPLY_GAME_REQUEST = "y";
var CANCEL_GAME_REQUEST = "n";

var OP_RES_USER_INFO = "UserInfo", OP_RES_CHAT = "SendChat",
    OP_RES_GAME_STARTED = "GameStarted", OP_RES_GAME_CHANGED = "GameChanged",
    OP_RES_RATING_TABLE = "RatingTable";

var chatScrolling = "";
var chatMsg = "";

var queryParams;
var isAuthed = false;
var fbLoginStatus;
var stompClient = null;
var userInfo;
var isWaitingForOpponent = false;
var tipsTimerId;

var ARROW_HELPER_ANIMATION_MIN_Y = "286";
var ARROW_HELPER_ANIMATION_MAX_Y = "298";
var ARROW_HELPER_ANIMATION_INIT_X = "276";
var arrowHelperTimerId;
var arrowHelperDirect = 1;

var justCreatedAccount = false;

var imgLobbyScreen = new Image();

function sendOperation(operationType, data) {
    var operation = {
        type: operationType,
        data: data
    };

    stompClient.send(USER_QUEUE, {}, JSON.stringify(operation));
}

function sendAuth(authPayload) {
    sendOperation(AUTH_OPERATION_TYPE, authPayload);
}

function sendChat() {
    var chatMessage = chatMsg.val();

    if (chatMessage.length === 0) {
        return;
    }

    var sendChatPayload = {
        msg: chatMessage
    };
    chatMsg.val("");

    sendOperation(SEND_CHAT_OPERATION_TYPE, sendChatPayload);
}

function sendRename() {
    var val = $('#warnwindow-input').val();
    var payload = {
        msg: val
    };

    sendOperation(RENAME_OPERATION_TYPE, payload);
    hideConfirmWindow();
}

function sendPlayRequest() {
    var sendPlayPayload;

    if (isWaitingForOpponent) {
        sendPlayPayload = {
            ack: CANCEL_GAME_REQUEST
        };

        resetGameRequestUi();
    } else {
        sendPlayPayload = {
            ack: APPLY_GAME_REQUEST
        };

        isWaitingForOpponent = true;
        $("#play-text").text(localize("matching-players"));
        $("#play-loader").show();
        startDisplayTips();
    }

    sendOperation(SEND_PLAY_REQUEST_OPERATION_TYPE, sendPlayPayload);
}

function resetGameRequestUi() {
    isWaitingForOpponent = false;
    $("#play-text").text(localize("play"));
    $("#play-loader").hide();
    stopDisplayTips();
}

function showRenameWindow() {
    showConfirmWindow(sendRename, hideConfirmWindow, localize('enter-name'), true, userInfo.name);
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
        tip = getFirstTip();
    } else {
        tip = getRandomTip();
    }

    $("#tips").html('<strong>' + localize('tip') + ': </strong>' + tip);
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

        var authPayload = getSocialNetworkPayload();
        authPayload.device = getPlatform();
        sendAuth(authPayload);
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
    if (justCreatedAccount) {
        justCreatedAccount = false;
        showRenameWindow();
    }

    $("#userImg").attr("src", prepareServerImg(userInfo.img));
    $("#userName").text(userInfo.name);
    $("#userScore").text(localize('score') + ': ' + userInfo.score);
    $("#user-info-data").attr("data-original-title", concatGameStats(userInfo));

    if (userInfo.wins < 5) {
        $('#playContent').attr("data-original-title", localize("captureAllFruits"));
    }

    $('[data-toggle="tooltip"]').tooltip();

    showMainWindow();
    showArrowHelper();
}

function prepareServerImg(img) {
    if (img.startsWith("..")) {
        return img.replace("..", IMG_PREFIX);
    }
    return img;
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
    return chatScrolling.scrollTop() + chatScrolling.prop('offsetHeight') >= chatScrolling.prop('scrollHeight') - 1;
}

function processRatingTableOperation(topRated) {
    var topPlayers = $("#top-players-container");
    topPlayers.html("");

    var counter = 0;
    topRated.forEach(function(user) {
        counter++;
        var tooltipText = concatGameStats(user);
        var tooltipAttrs = "data-toggle='tooltip' data-placement='bottom' title='" + tooltipText + "'";
        var placeColor = getPlayerPlaceColor(counter);

        topPlayers.append(
            "<div class='top-player " + placeColor + "' " + tooltipAttrs + ">" +
            wrapSpan(counter) +
            '<img class="rating-table-player-img" src="' + prepareServerImg(user.img) + '">' +
            "<div class='top-player-info'>" +
            "<div class='top-player-text'>" +
            "<span>" +
            user.name +
            "</span>" +
            "</div>" +
            "<div class='bold top-player-text-2'>" +
            user.score +
            "</div>" +
            "</div>" +
            "</div>");
    });

    $('[data-toggle="tooltip"]').tooltip();
}

function getPlayerPlaceColor(counter) {
    if (counter === 1) {
        return "player-first-place";
    } else if (counter === 2) {
        return "player-second-place";
    } else if (counter === 3) {
        return "player-third-place";
    } else {
        return "";
    }
}

function wrapSpan(text) {
    return "<span class='top-player-place'>" + text + "</span>";
}



function initUi() {
    console.log("initUi");
    window.scroll(0, 0);
    chatMsg.focus();
    $("#chat-msg-input").attr('placeholder', localize('chat'));

    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#reconnect").click(connectToServer);
    $("#playContent").click(sendPlayRequest);
    $("#send").click(sendChat);
    $("#userName").on("mouseup", showRenameWindow);

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
}

function preloadFirstImages() {
    imgLobbyScreen.src = IMG_PREFIX + '/img/components/lobby_background.png';

    var imgTopPlayer = new Image();
    var imgTopPlayerGold = new Image();
    var imgTopPlayerBronze = new Image();
    var imgTopPlayerSilver = new Image();
    var imgUserInfo = new Image();
    var imgRatingLabel = new Image();
    imgTopPlayer.src = IMG_PREFIX + '/img/components/top_player.png';
    imgTopPlayerGold.src = IMG_PREFIX + '/img/components/top_player_gold.png';
    imgTopPlayerSilver.src = IMG_PREFIX + '/img/components/top_player_silver.png';
    imgTopPlayerBronze.src = IMG_PREFIX + '/img/components/top_player_bronze.png';
    imgUserInfo.src = IMG_PREFIX + '/img/components/user_info.png';
    imgRatingLabel.src = IMG_PREFIX + '/img/components/rating_label.png';
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

function showArrowHelper() {
    if (userInfo.wins === 0 && arrowHelperTimerId === undefined) {
        $("#arrowHelper").css({top: ARROW_HELPER_ANIMATION_MIN_Y + 'px'});
        $("#arrowHelper").css({left: ARROW_HELPER_ANIMATION_INIT_X + 'px'});

        arrowHelperTimerId = setInterval(
            function() {
                animateArrowHelper();
            },
            ARROW_HELPER_TIMER_INTERVAL);

        $("#arrowHelper").show();
    }
}

function animateArrowHelper() {
    var top = parseInt($('#arrowHelper').css('top'));
    var left = parseInt($('#arrowHelper').css('left'));

    top += arrowHelperDirect;
    left += arrowHelperDirect * -1;
    if (top < ARROW_HELPER_ANIMATION_MIN_Y || top > ARROW_HELPER_ANIMATION_MAX_Y) {
        arrowHelperDirect *= -1;
    }

    $("#arrowHelper").css({top: top + 'px', left: left + 'px'});
}

function concatGameStats(user) {
    return localize('wins') + ':' + user.wins + ' ' + localize('defeats') + ":" + user.defeats + ' ' + localize('draws') + ":" + user.draws;
}

function getAppId() {
    return $('#social-network-app-id').text();
}

function onSocialNetworkAuthed() {
    console.log("onSocialNetworkAuthed");
    isAuthed = true;

    queryParams = parseQueryString();
    // preloadFirstImages();
    // connectToServer();
    // initLocalization();
    // initUi();
    // initGameUi();
}


/* === Generic Social Network methods ===  */
function getState() {
    return VK_TYPE;

    // var socialNetworkType = $('#social-network-type');
    // if (socialNetworkType.text() === VK_TYPE) {
    //     return VK_TYPE;
    // } else if (socialNetworkType.text() === YA_TYPE) {
    //     return YA_TYPE;
    // } else if (socialNetworkType.text() === SB_TYPE) {
    //     return SB_TYPE;
    // } else {
    //     return FB_TYPE;
    // }
}

function runApp() {
    if (getState() === VK_TYPE) {
        console.log('starting vk SDK...');
        startVkSdk();
    } else if (getState() === YA_TYPE) {
        console.log('starting yandex SDK...');
        startYandexSdk();
    } else if (getState() === SB_TYPE) {
        console.log('starting sb...');
        onSocialNetworkAuthed();
    } else {
        console.log('starting facebook SDK...');
        startFbSdk();
    }
}

function getSocialNetworkPayload() {
    var authPayload = {};

    if (getState() === VK_TYPE) {
        authPayload.type = VK_TYPE;
        authPayload.authKey = queryParams["auth_key"];
        authPayload.userId = queryParams["viewer_id"];
    } else if (getState() === YA_TYPE) {
        authPayload.type = YA_TYPE;
        authPayload.userId = storeUuid();
    } else if (getState() === SB_TYPE) {
        authPayload.type = SB_TYPE;
        authPayload.userId = storeUuid();
    } else {
        authPayload.type = FB_TYPE;
        authPayload.accessToken = fbLoginStatus.authResponse.accessToken;
        authPayload.userId = fbLoginStatus.authResponse.userID;
    }

    return authPayload;
}

function storeUuid() {
    try {
        var uuid = localStorage.getItem('uuid');
        if (uuid === null) {
            uuid = getRandomInt(1000000000, 1000000000000);
            localStorage.setItem('uuid', uuid);
            justCreatedAccount = true;
        }
        return uuid;
    } catch (e) {
        console.log("error while store to localStorage");
        console.log(e);
        throw e;
    }
}


/* === Adds === */
async function showAdds(chancePercent) {
    if (!isPercentFired(chancePercent)) {
        return;
    }

    if (getState() === VK_TYPE) {
        window.vkBridge.send("VKWebAppCheckNativeAds", {"ad_format": "interstitial"});
        window.vkBridge.send("VKWebAppShowNativeAds", {ad_format:"interstitial"});
    } else if (getState() === YA_TYPE) {
        ysdk.adv.showFullscreenAdv({
            callbacks: {
                onClose: function(wasShown) {
                    console.log(wasShown);
                },
                onError: function(error) {
                    console.log(error);
                }
            }
        })
    }
}

function isPercentFired(chancePercent) {
    if (!chancePercent) {
        return true;
    }

    if (chancePercent >= getRandomInt(1, 100)) {
        return true;
    }

    return false;
}

/* === Facebook Methods === */
function startFbSdk() {
    window.fbAsyncInit = fbAsyncInit;

    console.log('loading facebook SDK...');
    loadFbSdk();
}

function fbAsyncInit() {
    initFb();

    // FB.AppEvents.logPageView();
    // FB.Event.subscribe('auth.authResponseChange', fbStatusChanged);

    tryLoginFb();
}

function loadFbSdk() {
    (function (d, s, id) {
        var js, fjs = d.getElementsByTagName(s)[0];
        if (d.getElementById(id)) {
            return;
        }
        js = d.createElement(s);
        js.id = id;
        js.src = "//connect.facebook.net/en_US/sdk.js";
        fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk'));
}

function initFb() {
    FB.init({
        appId            : getAppId(),
        version          : 'v2.12',
        autoLogAppEvents : true,
        xfbml            : false,
        cookie           : true,
        status           : false
    });
}

function tryLoginFb() {
    FB.getLoginStatus(function(response) {
        if (response.status === 'connected') {
            fbStatusChanged(response);
        } else {
            FB.login(
                function(response) {
                    fbStatusChanged(response);
                },
                {scope: 'public_profile'});
        }
    });
}

function fbStatusChanged(logStatus) {
    if (logStatus.status != 'connected' || isAuthed) {
        return;
    }

    fbLoginStatus = logStatus;
    onSocialNetworkAuthed();
}


/* === VK Methods === */
function startVkSdk() {
    if (isMobileVk) {
        // window.name = VK_IFRAME_WINDOW_NAME;
        console.log('loading mobile vk SDK...');
        loadScript(VK_MOBILE_SDK_URL, initVk);
    } else {
        console.log('loading vk SDK...');
        loadScript(VK_SDK_URL, initVk);
    }

    console.log('Loading vk bridge...');
    window.vkBridge.send('VKWebAppInit');
}

function initVk() {
    VK.init(function(d) {
        // API initialization succeeded. Your code here
        console.log("VK API initialization succeeded.");

        onSocialNetworkAuthed();
    }, function(d) {
        // API initialization failed. Can reload page here
        console.log("API initialization failed. Try to reload page.");
    }, '5.131');
}

function isMobileVk() {
    return window.name !== VK_IFRAME_WINDOW_NAME;
}


/* === Yandex Methods === */
function startYandexSdk() {
    (function(d) {
        var t = d.getElementsByTagName('script')[0];
        var s = d.createElement('script');
        s.src = 'https://yandex.ru/games/sdk/v2';
        s.async = true;
        t.parentNode.insertBefore(s, t);
        s.onload = initSDK;
    })(document);
}

function initSDK() {
    YaGames
    .init()
    .then(ysdk => {
        console.log('Yandex SDK initialization succeeded.');
        window.ysdk = ysdk;
        onSocialNetworkAuthed();
    });
}


/* === Other === */
function loadScript(url, callback) {
    // Adding the script tag to the head as suggested before
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;

    // Then bind the event to the callback function.
    // There are several events for cross browser compatibility.
    script.onreadystatechange = callback;
    script.onload = callback;

    // Fire the loading
    head.appendChild(script);
}

function loadScriptDynamically(url) {
    var script = document.createElement("script");  // create a script DOM node
    script.src = url;  // set its src to the provided URL

    document.head.appendChild(script);  // add it to the end of the head section of the page (could change 'head' to 'body' to add it to the end of the body section instead)
}

function parseQueryString() {
    var str = window.location.search;
    var objURL = {};

    str.replace(
        new RegExp( "([^?=&]+)(=([^&]*))?", "g" ),
        function( $0, $1, $2, $3 ){
            objURL[ $1 ] = $3;
        }
    );
    return objURL;
}

/**
 * max not in
 */
function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
}

function getPlatform() {
    var result = getMobileOperatingSystem();
    if (result !== "UNKNOWN") {
        return result;
    }

    return getOs();
}

/**
 * Determine the mobile operating system.
 */
function getMobileOperatingSystem() {
    var userAgent = navigator.userAgent || navigator.vendor || window.opera;

    // Windows Phone must come first because its UA also contains "Android"
    if (/windows phone/i.test(userAgent)) {
        return "WINDOWS_PHONE";
    }

    if (/android/i.test(userAgent)) {
        return "ANDROID";
    }

    // iOS detection from: http://stackoverflow.com/a/9039885/177710
    if (/iPad|iPhone|iPod/.test(userAgent) && !window.MSStream) {
        return "I_OS";
    }

    return "UNKNOWN";
}

function getOs() {
    var result = "UNKNOWN";
    if (navigator.appVersion.indexOf("Win")!=-1) result="WINDOWS";
    if (navigator.appVersion.indexOf("Mac")!=-1) result="MAC_OS";
    if (navigator.appVersion.indexOf("X11")!=-1) result="UNIX";
    if (navigator.appVersion.indexOf("Linux")!=-1) result="LINUX";

    return result;
}


/* === Run App === */
runApp();
