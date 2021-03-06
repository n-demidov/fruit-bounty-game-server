"use strict";

function getLocalization() {
  return {
    "game-pane": {
      selector: "#game-pane-a",
      en: "Game",
      ru: "Игра"
    },
    "rules-pane": {
      selector: "#rules-pane-a",
      en: "Rules",
      ru: "Правила"
    },
    "authors-pane": {
      selector: "#authors-pane-a",
      en: "Authors",
      ru: "Авторы"
    },

    "play": {
      selector: "#play-text",
      en: "Start game",
      ru: "Начать игру"
    },
    "feedback-text": {
      selector: "#feedback-text",
      en: 'Please, write your suggestions, feedback and bugs to the <strong>Fruit\'s Bounty Support:</strong><br> <a href="mailto:#">FruitsBounty@gmail.com</a>',
      ru: 'Вы можете сообщать о найденных ошибках, пожеланиях и предложениях на <strong>Fruit\'s Bounty Support:</strong><br> <a href="mailto:#">FruitsBounty@gmail.com</a>'
    },
    "rules-text": {
      selector: "#rules-pane",
      en: enRulesHtml,
      ru: ruRulesHtml
    },

    "privacy-policy": {
      selector: "#privacy-policy",
      en: "Privacy Policy · ",
      ru: "Политика конфиденциальности · "
    },
    "terms-of-service": {
      selector: "#terms-of-service",
      en: "Terms of Service",
      ru: "Условия использования"
    },

    "reconnect-button": {
      selector: "#reconnect",
      en: "Refresh the page",
      ru: "Подключиться"
    },
    "reconnect-label": {
      selector: "#discnctPopup p",
      en: "Sorry, the connection is losszzzzsst...",
      ru: "Соединение было потеряно..."
    },

    /* Non HTML text */
    "matching-players": {
      en: "Matching players...",
      ru: "Поиск оппонента..."
    },
    "score": {
      en: "Score",
      ru: "Рейтинг"
    },
    "tip": {
      en: "Tip",
      ru: "Совет"
    },
    "wins": {
      en: "Wins",
      ru: "Победы"
    },
    "defeats": {
      en: "Defeats",
      ru: "Поражения"
    },
    "draws": {
      en: "Draws",
      ru: "Ничьи"
    },
    "close": {
      en: "Close",
      ru: "Закрыть"
    },
    "concede": {
      en: "Concede",
      ru: "Сдаться"
    },
    "concede-confirmation": {
      en: "Are you sure you want to concede?",
      ru: "Вы уверены, что хотите сдаться?"
    },
    "win": {
      en: "Win",
      ru: "Победа"
    },
    "fruitIsOccupied": {
      en: "Fruit is occupied by opponent",
      ru: "Фрукт занят оппонентом"
    },
    "mobile-layout": {
      en: "Please, flip your device horizontally.",
      ru: "Пожалуйста, расположите ваше устройство горизонтально."
    },

    /*Game tutor */
    "tutor.you-started": {
      en: "You have started in the",
      ru: "Вы начали играть в"
    },
    "tutor.left-top": {
      en: "left top",
      ru: "левом верхнем"
    },
    "tutor.right-bottom": {
      en: "right bottom",
      ru: "правом нижнем"
    },
    "tutor.board-corner": {
      en: "corner.",
      ru: "углу."
    },
    "tutor.grab-adjoin-fruits": {
      en: "You can grab only those fruits that adjoin yours.",
      ru: "Вы можете захватить только соседние фрукты."
    },
    "tutor.capture-any-nearest": {
      en: "So capture any of two neighboring fruits.",
      ru: "Щёлкните по любому из двух ближайших."
    },
    "tutor.capture-several-fruits-1": {
      en: "Neighboring the same kind fruits",
      ru: "Соседние фрукты такого же вида"
    },
    "tutor.capture-several-fruits-2": {
      en: "also will be taken.",
      ru: "тоже будут захвачены."
    },
    "tutor.can-not-capture-opponent-fruit-1": {
      en: "Note: you can't capture a fruit",
      ru: "Имейте в виду, что невозможно взять фрукт"
    },
    "tutor.can-not-capture-opponent-fruit-2": {
      en: "if an opponent owns the same kind.",
      ru: "если оппонент владеет таким же."
    },
    "tutor.try-collect-max-fruits": {
      en: "Try to collect as many fruits as you can.",
      ru: "Победит захвативший больше всего фруктов."
    },
    "tutor.the-max-will-win": {
      en: "A winner is determined by the captured fruits count.",
      ru: "Старайтесь просчитывать ходы наперёд."
    }
  };
}
