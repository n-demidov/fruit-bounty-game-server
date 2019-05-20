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
    "feedback-pane": {
      selector: "#feedback-pane-a",
      en: "Feedback",
      ru: "Обратная связь"
    },
    "authors-pane": {
      selector: "#authors-pane-a",
      en: "Authors",
      ru: "Авторы"
    },
    "rating-table-pane": {
      selector: "#rating-table-pane-a",
      en: "Top rated players",
      ru: "Топ игроков"
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
      en: "corner of the game board.",
      ru: "углу игрового поля."
    },
    "tutor.grab-adjoin-fruits": {
      en: "You can grab only those fruits that adjoin yours.",
      ru: "Вы можете захватить только соседние фрукты."
    },
    "tutor.capture-any-nearest": {
      en: "So capture any of two neighboring fruits.",
      ru: "Итак, захватите любой из двух ближайших плодов."
    },
    "tutor.capture-several-fruits-1": {
      en: "If the captured fruit contacts other fruit(s) of the same kind,",
      ru: "Если захватываемый фрукт соприкасается с другими"
    },
    "tutor.capture-several-fruits-2": {
      en: "they also will be taken.",
      ru: "такого же вида - они тоже будут захвачены вами."
    },
    "tutor.can-not-capture-opponent-fruit-1": {
      en: "Also, keep in mind that you can't capture a fruit",
      ru: "Также имейте в виду, что вы не сможете захватить фрукт"
    },
    "tutor.can-not-capture-opponent-fruit-2": {
      en: "if an opponent currently owns the same kind of the fruit.",
      ru: "если оппонент в данный момент владеет таким же."
    },
    "tutor.try-collect-max-fruits": {
      en: "Try to collect as many fruits as you can.",
      ru: "Победит тот, кто захватит больше всего фруктов."
    },
    "tutor.the-max-will-win": {
      en: "A winner is determined by the largest number of captured fruits.",
      ru: "Для победы старайтесь просчитывать ходы наперёд."
    }
  };
}
