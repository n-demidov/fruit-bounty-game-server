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

    "top-players-rating": {
      selector: "#top-players-rating",
      en: "Rating",
      ru: "Рейтинг"
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
      en: "Terms of Service · ",
      ru: "Условия использования · "
    },
    "authors": {
      selector: "#authors",
      en: "Authors & Feedback",
      ru: "Авторы и Обратная связь"
    },

    "reconnect-button": {
      selector: "#reconnect",
      en: "Refresh the page",
      ru: "Подключиться"
    },
    "reconnect-label": {
      selector: "#discnctPopup p",
      en: "Sorry, the connection is lost...",
      ru: "Соединение было потеряно..."
    },

    /* Non HTML text */
    "matching-players": {
      en: "Matching...",
      ru: "Поиск..."
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
    "defeat": {
      en: "Defeat",
      ru: "Поражение"
    },
    "captureFruit": {
      en: "Capture the fruit",
      ru: "Захватите фрукт"
    },
    "fruitIsOccupied": {
      en: "Fruit is occupied by opponent",
      ru: "Фрукт занят оппонентом"
    },
    "opponentTurn": {
      en: "Wait for your turn",
      ru: "Дождитесь своего хода"
    },
    "mobile-layout": {
      en: "Please, flip your device horizontally.",
      ru: "Пожалуйста, расположите ваше устройство горизонтально."
    },
    "chat": {
      en: "chat",
      ru: "чат"
    },
    "captureAllFruits": {
      en: "Whoever grabs the most fruits wins",
      ru: "Победит тот, кто захватит больше всего фруктов"
    },
    "enter-name": {
      en: "Enter a name (min: 4)",
      ru: "Введите имя (мин: 4)"
    },

    /*Game tutor */
    "tutor.1.1": {
      en: "You have started in the",
      ru: "Вы начали играть в"
    },
    "tutor.1.2l": {
      en: "left top",
      ru: "левом верхнем"
    },
    "tutor.1.2r": {
      en: "right bottom",
      ru: "правом нижнем"
    },
    "tutor.1.3": {
      en: "corner.",
      ru: "углу."
    },
    "tutor.1.4": {
      en: "Click on the neighbor cell",
      ru: "Щелкните на одной из"
    },
    "tutor.1.5": {
      en: "to seize it.",
      ru: "соседних клеток,"
    },
    "tutor.1.6": {
      en: "",
      ru: "чтобы захватить её."
    },
    "tutor.2.1": {
      en: "Continue to grab",
      ru: "Продолжайте захватывать"
    },
    "tutor.2.2": {
      en: "neighbor fruits",
      ru: "соседние фрукты"
    },
    "tutor.3.1": {
      en: "A winner is determined by",
      ru: "Победит тот, кто захватит"
    },
    "tutor.3.2": {
      en: "the captured cells count",
      ru: "больше всего клеток"
    },
    "tutor.4.1": {
      en: "Nearby the same kind fruits",
      ru: "Соседние фрукты такого же"
    },
    "tutor.4.2": {
      en: "- also will be taken",
      ru: "типа - тоже будут взяты"
    },
    "tutor.5.1": {
      en: "You can't capture a fruit",
      ru: "Нельзя взять фрукт, если"
    },
    "tutor.5.2": {
      en: "if an opponent owns it",
      ru: "он сейчас у оппонента"
    },
    "tutor.6": {
      en: "Try to think ahead",
      ru: "Старайтесь думать наперёд"
    }
  };
}
