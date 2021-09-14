"use strict";

var MOBILE_SCREEN = "M";
var TABLET_SCREEN = "T";

var TABLET_SCREEN_WIDTH = 768;

var canvasTipsMapper = {};
canvasTipsMapper[MOBILE_SCREEN] = {};
canvasTipsMapper[TABLET_SCREEN] = {};

canvasTipsMapper[MOBILE_SCREEN].fontSize = '20px';
canvasTipsMapper[MOBILE_SCREEN].addingTipHeight = 40;
canvasTipsMapper[MOBILE_SCREEN].lineWidth = 5;

canvasTipsMapper[TABLET_SCREEN].fontSize = '30px';
canvasTipsMapper[TABLET_SCREEN].addingTipHeight = 50;
canvasTipsMapper[TABLET_SCREEN].lineWidth = 7;

function getScreenType() {
  if (window.innerWidth >= TABLET_SCREEN_WIDTH) {
    return TABLET_SCREEN;
  } else {
    return MOBILE_SCREEN;
  }
}

function getCanvasTipsParams() {
  return canvasTipsMapper[getScreenType()];
}
