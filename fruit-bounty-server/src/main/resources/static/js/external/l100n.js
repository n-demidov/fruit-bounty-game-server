/*
 * l100n 1.0 - unusual JavaScript library for pure client-side localization by css-selectors
 *
 * Full description in Russian: http://vas3k.ru/dev/l100n
 *
 * Licensed under the LGPL licenses:
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */

l100n = {
	locale: "ru",                   // Current locale codename
	default_locale: "ru",           // Default (fallback) locale codename
    always_localize: [],            // Page codenames which are localizes at each localize_page call
	pages: {
        "test-page": {              // Page codename
            "test-string": {        // String codename
                selector: "#test",  // CSS-selector for replace strings
                en: "Testing",      // Locales
                ru: "Тестинг"
            }
        }
	},


    /**
     * Returns user browser locale (two-digits code)
     */
    get_browser_locale : function() {
        return (navigator.language || navigator.systemLanguage || navigator.browserLanguage || navigator.userLanguage || this.default_locale).substr(0, 2).toLowerCase();
    },

    /**
     * Add page localizationn dictionary to localization object
     * @param page_name (required) - page codename
     * @param page_strings (optional) - l10n dictionary
     */
    add_page: function(page_name, page_strings) {
        if (page_name) {
            this.pages[page_name] = page_strings;            
            return true;
        }
        return false;
    },

    /**
     * Localize element by codename
     * @param name (required) - string codename
     * @param custom_page (optional) - page codename, if not provided causes full scan for string codename
     * @param custom_locale (optional) - custom locale codename
     */
    localize: function(name, custom_page, custom_locale) {
        var loc = custom_locale || this.locale || this.default_locale;

        var l10n_object = this.get_l10n_object(name, custom_page);
        if (!l10n_object) return false;

        // Try to get localization
        var l10n_string = l10n_object[loc];
        if (!l10n_string) {
            // In case of failure, try to get default localization
            l10n_string = l10n_object[this.default_locale];
            // If not default localization - fail
            if (!l10n_string) return false;
        }

        // Localizing element
        $(l10n_object.selector).html(l10n_string);
        return true;
    },

    /**
     * Returns localized string for codename
     * @param name (required) - string codename
     * @param custom_page (optional) - page codename, if not provided causes full scan for string codename
     * @param custom_locale (optional) - custom locale codename
     */
    localize_string: function(name, custom_page, custom_locale) {
        var loc = custom_locale || this.locale || this.default_locale;

        var l10n_object = this.get_l10n_object(name, custom_page);
        if (!l10n_object) return "";

        // Try to get localization
        var l10n_string = l10n_object[loc];
        if (!l10n_string) {
            // In case of failure, try to get default localization
            l10n_string = l10n_object[this.default_locale];
            // If not default localization - fail
            if (!l10n_string) return "";
        }

        // Returns localized string
        return l10n_string;
    },

    /**
     * Localize entire page by page codename
     * @param page (required) - page codename
     * @param custom_locale (optional) - custom locale codename
     * @param no_always (optional) - if true, ignore always_localize pages
     */
    localize_page: function(page, custom_locale, no_always) {
        if (!page) return false;
        var loc = custom_locale || this.locale || this.default_locale;

        var strings = this.pages[page];
        for (var string_id in strings) {
            var string = strings[string_id];
            var l10n_string = string[loc];
            if (!l10n_string) {
                l10n_string = string[this.default_locale];
            }
            $(string.selector).html(l10n_string);    
        }

        if (!no_always) {
            for (var page in this.always_localize) {
                this.localize_page(this.always_localize[page], loc, true);
            }
        }

        return true;
    },

    /**
     * Localize entire all pages (may be slow)
     * @param custom_locale (optional) - custom locale codename
     */
    localize_all_pages: function(custom_locale) {
        var loc = custom_locale || this.locale || this.default_locale;

        for (var page_name in this.pages) {
            this.localize_page(page_name, loc, true);
        }

        for (var page in this.always_localize) {
            this.localize_page(this.always_localize[page], loc, true);
        }

        return true;
    },

    /**
     * (private method) Returns localization object by string codename
     * @param name (required) - string codename
     * @param custom_page (optional) - page codename, if not provided causes full scan for string codename
     */
    get_l10n_object: function(name, custom_page) {
        // If custom_page not provided start full search
        // until first occurrence of string codename
        var page = custom_page;
        if (!page) {
            var found = false;
            for (var page_name in this.pages) {
                var strings = this.pages[page_name];
                for (var string_id in strings) {
                    if (string_id == name) {
                        page = page_name;
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
        }

        return this.pages[page][name];
    }
};
