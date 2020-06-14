package by.petropavlovskaja.pharmacy.controller.cookie;

import by.petropavlovskaja.pharmacy.dao.AccountDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class BrowserCookie {
    private static Logger logger = LoggerFactory.getLogger(BrowserCookie.class);

    private BrowserCookie() {
    }

    private static class BrowserCookieHolder {
        public static final BrowserCookie BROWSER_COOKIE = new BrowserCookie();
    }

    public static BrowserCookie getInstance() {
        return BrowserCookieHolder.BROWSER_COOKIE;
    }

    public void setCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        response.addCookie(cookie);
    }

    public Map<String, String> getCookiesInMap(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Map<String, String> cookiesMap = new HashMap<>();
        if (cookies != null) {
            for (Cookie c : cookies) {
                cookiesMap.put(c.getName(), c.getValue());
            }
        }
        return cookiesMap;
    }

    public Cookie findCookieByName(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        Cookie foundCookie = null;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (cookieName.equals(c.getName())) {
                    foundCookie = c;
                    break;
                }
            }
        }
        return foundCookie;
    }
}