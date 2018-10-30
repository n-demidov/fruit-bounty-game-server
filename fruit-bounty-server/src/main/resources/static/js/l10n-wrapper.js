"use strict";

var DEFAULT_LOCALE = "en";
var DEFAULT_PAGE_NAME = "app-ui";

var browserLocale;
var tips = {};

function initLocalization() {
  console.log("initLocalization");

  l100n.add_page(DEFAULT_PAGE_NAME, getLocalization());

  browserLocale = l100n.get_browser_locale();
  l100n.locale = browserLocale;
  l100n.default_locale = DEFAULT_LOCALE;

  l100n.localize_all_pages();
  initTips();
}

function localize(valueName) {
  return l100n.localize_string(valueName, DEFAULT_PAGE_NAME);
}

function initTips() {
  tips['en'] = enTips;
  tips['ru'] = ruTips;
}

function getFirstTip() {
  return tips[browserLocale][0];
}

function getRandomTip() {
  return tips[browserLocale][getRandomInt(0, tips[browserLocale].length)];
}
