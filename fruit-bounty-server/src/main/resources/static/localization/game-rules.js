"use strict";

var enRulesHtml = '<p class="lead">' +
    '              The rules of Fruit\'s Bounty game:' +
    '            </p>' +
    '            <p>' +
    '              The <strong>goal</strong> of the game - is to capture as many fruits as possible.' +
    '            </p>' +
    '            <p>' +
    '              <strong>Players</strong> start in opposite corners of the game board. Moves are made alternately. A timeout for a move is about 20 seconds.' +
    '            </p>' +
    '            <p>' +
    '              You can grab only those fruits that <strong>adjoin</strong> yours.' +
    '            </p>' +
    '            <p>' +
    '              If the captured fruit contacts other fruit(s) of the same kind, they also will be taken. Due to this, you can significantly increase the speed of conquest of the playing field.' +
    '            </p>' +
    '            <p>' +
    '              Also, you can\'t capture a fruit if an opponent currently owns the same kind of the fruit. Using this feature, you could hamper your opponent.' +
    '            </p>' +
    '            <p>' +
    '              Fruit\'s Bounty - is an <strong>intellectual</strong> game with the addition of a randomness element.' +
    '            </p>' +
    '            <p>' +
    '              At the end of a game, the <strong>winner</strong> is determined by the largest number of captured fruits.' +
    '            </p>' +
    '            <p>' +
    '              A <strong>defeat</strong> can happen in three cases:' +
    '              <br>' +
    '              - A player missed 3 turns in a row.' +
    '              <br>' +
    '              - A player gave up.' +
    '              <br>' +
    '              - At the end of the game, a player collected less fruits than an opponent.' +
    '            </p>' +
    '            <p>' +
    '              A <strong>draw</strong> can occur in those rare cases when both players have captured the same number of fruits.' +
    '            </p>' +
    '            <p>' +
    '              The <strong>rating system</strong> is similar to the one used in chess. The <strong>calculation of scores</strong> depends on the difference in the ranking of both opponents: if a player defeats a stronger opponent, he/she will receive more points. In case a high rated player defeats the weaker one, he/she(the winner) will get an insignificant amount of rating (or nothing at all if the difference in rating is too big).' +
    '            </p>' +
    '            <p>' +
    '              The <strong>rating table</strong> (on the main game window) shows the top rated players. The rating table takes into account the rating only starting from 600 points and it is updated every few seconds.' +
    '            </p>' +
    '            <br>' +
    '            <p class="lead">' +
    '              Hot keys:' +
    '            </p>' +
    '            <p>' +
    '              To move focus in the input <strong>chat</strong> text area - press "Enter" key or "Alt" + "C" keys or "Ctrl" + "~" keys.' +
    '            </p>' +
    '            <p>' +
    '              Hold down the "Ctrl" key and press "+" to zoom in.' +
    '              <br>' +
    '              Holds down the "Ctrl" key and press "-" to zoom out.' +
    '              <br>' +
    '              Hold down the "Ctrl" key and press "0" (zero) to reset to 100%.' +
    '            </p>';

var ruRulesHtml = '<p class="lead">' +
    '              Правила игры Fruit\'s Bounty:' +
    '            </p>' +
    '            <p>' +
    '              <strong>Цель</strong> игры - захватить максимальное количество фруктов.' +
    '            </p>' +
    '            <p>' +
    '              <strong>Игроки</strong> начинают игру в противоположных углах игрового поля. Ходы делаются поочерёдно. На ход даётся 20 секунд.' +
    '            </p>' +
    '            <p>' +
    '              Вы можете захватывать только <strong>соседние</strong> клетки.' +
    '            </p>' +
    '            <p>' +
    '              Если захватываемый фрукт соприкасается с другими такого же вида, то они тоже будут захвачены. Используя это вы можете значительно ускорить захват игровой доски.' +
    '            </p>' +
    '            <p>' +
    '              Обратите внимание: вы не сможете захватить плод, если в данный момент противник владеет таким же. Используя эту фишку, вы можете "подрезать" ваших оппонентов.' +
    '            </p>' +
    '            <p>' +
    '              Fruit\'s Bounty - <strong>интеллектуальная</strong> игра с элементами случайности.' +
    '            </p>' +
    '            <p>' +
    '              <strong>Победитель</strong> определяется по наибольшему числу захваченных даров природы.' +
    '            </p>' +
    '            <p>' +
    '              <strong>Поражение</strong> может наступить в 3 случаях:' +
    '              <br>' +
    '              - Игрок пропустил 3 хода подряд.' +
    '              <br>' +
    '              - Игрок сдался.' +
    '              <br>' +
    '              - В конце игры игрок набрал меньше фруктов, чем его оппонент.' +
    '            </p>' +
    '            <p>' +
    '              <strong>Ничья</strong> возможна в тех редких случаях, когда все игроки захватили одинаковое количество клеток.' +
    '            </p>' +
    '            <p>' +
    '              Система <strong>игрового рейтинга</strong> похожа на ту, что используется в шахматах. <strong>Расчёт очков</strong> зависит от разницы в рейтинге обоих оппонентов: если игрок победит более сильного противника, то он получит больше очков. В случае, если игрок с более высоким рейтингом побеждает более слабого, то он/она получит незначительное количество рейтинга (или вообще ничего, если разница в рейтинге слишком велика).' +
    '            </p>' +
    '            <p>' +
    '              <strong>Таблица рейтинга</strong> (на главном игровом экране) показывает топ самых крутых игроков. Рейтинговая таблица учитывает рейтинг начиная с 600 баллов. Она обновляется каждые несколько секунд.' +
    '            </p>' +
    '            <br>' +
    '            <p class="lead">' +
    '              Горячие клавиши:' +
    '            </p>' +
    '            <p>' +
    '              Установить фокус в <strong>чат</strong>: "Enter" или "Alt" + "C" или "Ctrl" + "~".' +
    '            </p>' +
    '            <p>' +
    '              Увеличить масштаб: "Ctrl" + "Плюс".' +
    '              <br>' +
    '              Уменьшить масштаб: "Ctrl" + "Минус".' +
    '              <br>' +
    '              Вернуть масштаб до 100%: "Ctrl" + "0".' +
    '            </p>';
