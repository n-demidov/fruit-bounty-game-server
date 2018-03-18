package com.demidovn.fruitbounty.server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HttpMainController {

    private static final String FB_APP_URL = "/fb-app";
    private static final String MVC_REDIRECT = "redirect:/";
    private static final String APP_HTML_PAGE = "app.html";

    @RequestMapping(value = FB_APP_URL, method = RequestMethod.GET)
    public String indexGet() {
        return APP_HTML_PAGE;
    }

    @RequestMapping(value = FB_APP_URL, method = RequestMethod.POST)
    public String indexPost() {
        return MVC_REDIRECT + APP_HTML_PAGE;
    }

}
