"use strict";

var enTips = [
  'Try to collect as many fruits as you can. Read game rules (on the "Rules" tab) for more details.',
  'Do not rush: use the maximum of time to estimate the situation.',
  'Try to estimate the board on the start of game and generate a plan.',
  'If a player misses 3 moves in a row, then he is considered as defeated.',
  'To move focus in the input chat text area - press "Enter" key or "Alt" + "C" keys or "Ctrl" + "~" keys.',
  'Hold down the "Ctrl" key and press "+" to zoom in.<br>Holds down the "Ctrl" key and press "-" to zoom out.<br>Hold down the "Ctrl" key and press "0" (zero) to reset to 100%.',
  'You can share impressions with the world, click: <button id="share" class="btn btn-success btn-sm" type="submit">Share</button>',

  'The goal of the game - is to capture as many fruits as possible.',
  'Players start in opposite corners of the game board. Moves are made alternately. A timeout for a move is about 20 seconds.',
  'You can grab only those fruits that adjoin yours.',
  'If the captured fruit contacts other fruit(s) of the same kind, they also will be taken. Due to this, you can significantly increase the speed of conquest of the playing field.',
  'You can\'t capture a fruit if an opponent currently owns the same kind of the fruit. Using this feature, you could hamper your opponent.',
  'Fruit\'s Bounty - is an intellectual game with the addition of a randomness element.',
  'At the end of a game, the winner is determined by the largest number of captured fruits.',
  'A draw can occur in those rare cases when both players have captured the same number of fruits.',
  'The rating system is similar to the one used in chess. The calculation of scores depends on the difference in the ranking of both opponents: if a player defeats a stronger opponent, he/she will receive more points. In case a high rated player defeats the weaker one, he/she(the winner) will get an insignificant amount of rating (or nothing at all if the difference in rating is too big).',
  'The rating table (on the main game window) shows the top rated players. The rating table takes into account the rating only starting from 600 points and it is updated every few seconds.'
];

var ruTips = [
  'Постарайтесь захватить как можно больше фруктов. Прочитайте игровые правила, чтобы узнать тонкости игры (на табе "Правила").',
  'Делайте как в шахматах: не спешите. Используйте максимально доступное время, чтобы просчитать игровую ситуацию.',
  'В самом начале игры оцените доску и составьте план.',
  'Если игрок пропустит 3 хода подряд, то ему засчитается поражение.',
  'Чтобы установить фокус в чат нажмите "Enter" или "Alt" + "C" или "Ctrl" + "~".',
  'Горячие клавиши:<br>Увеличить масштаб: "Ctrl" + "Плюс".<br>Уменьшить масштаб: "Ctrl" + "Минус".<br>Вернуть масштаб до 100%: "Ctrl" + "0".',

  'Цель игры - захватить максимальное количество фруктов.',
  'Игроки начинают игру в противоположных углах игрового поля. Ходы делаются поочерёдно. На ход даётся 20 секунд.',
  'Вы можете захватывать только соседние клетки.',
  'Если захватываемый фрукт соприкасается с другими такого же вида, то они тоже будут захвачены. Используя это вы можете значительно ускорить захват игровой доски.',
  'Обратите внимание: вы не сможете захватить плод, если в данный момент противник владеет таким же. Используя эту фишку, вы можете "подрезать" ваших оппонентов.',
  'Fruit\'s Bounty - интеллектуальная игра с элементами случайности.',
  'Победитель определяется по наибольшему числу захваченных даров природы.',
  'Ничья возможна в тех редких случаях, когда все игроки захватили одинаковое количество клеток.',
  'Система игрового рейтинга похожа на ту, что используется в шахматах. Расчёт очков зависит от разницы в рейтинге обоих оппонентов: если игрок победит более сильного противника, то он получит больше очков. В случае, если игрок с более высоким рейтингом побеждает более слабого, то он/она получит незначительное количество рейтинга (или вообще ничего, если разница в рейтинге слишком велика).',
  'Таблица рейтинга (на главном игровом экране) показывает топ самых крутых игроков. Рейтинговая таблица учитывает рейтинг начиная с 600 баллов. Она обновляется каждые несколько секунд.'
];
