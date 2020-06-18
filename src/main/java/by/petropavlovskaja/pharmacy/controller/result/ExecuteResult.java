package by.petropavlovskaja.pharmacy.controller.result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExecuteResult {
    private static Logger logger = LoggerFactory.getLogger(ExecuteResult.class);
    private Map<String, Object> responseAttributes = new HashMap<>();
    private Map<String, String> cookieSet = new HashMap<>();
    private boolean updateCookie = false;
    private static String jsp;


    public void complete(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();

        if (!responseAttributes.isEmpty()) {
            Set<String> set = responseAttributes.keySet();
            for (String s : set) {
                System.out.println("ExRez complete set attr: " + s + " and val: " + responseAttributes.get(s));
                request.setAttribute(s, responseAttributes.get(s));
            }
        }

        if (updateCookie) {
            if (!cookieSet.isEmpty()) {
                Set<String> set = cookieSet.keySet();
                for (String s : set) {
                    Cookie cookie = new Cookie(s, cookieSet.get(s));
                    cookie.setMaxAge(259200);
                    response.addCookie(cookie);
                }
            }
        }
        try {

            if (request.getMethod().equals("GET") || !responseAttributes.isEmpty()) {
                checkJSP(request);
                RequestDispatcher dispatcher = request.getRequestDispatcher(jsp);
                dispatcher.forward(request, response);
                logger.info("Execute result is successful for method GET. Jsp = " + jsp);
            } else {
                response.sendRedirect(jsp);
                logger.info("Execute result is successful for method POST. Jsp = " + jsp);
            }
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    private void checkJSP(HttpServletRequest request) {
        String realPath = request.getServletContext().getRealPath("") + jsp;
        File file = new File(realPath);
        if (!file.exists()) {
            jsp = "/WEB-INF/jsp/404.jsp";
        }
    }

    public void setJsp(String jsp) {
        ExecuteResult.jsp = jsp;
    }

    public void setJsp(String[] arrayUri) {
        String jsp = "/WEB-INF/jsp/";
        for (int i = 1; i < arrayUri.length - 1; i++) {
            jsp = jsp + arrayUri[i] + "/";
        }
        jsp = jsp + arrayUri[arrayUri.length - 2] + "_" + arrayUri[arrayUri.length - 1] + ".jsp";

        ExecuteResult.jsp = jsp;
    }

    public void setResponseAttributes(String key, Object value) {
        responseAttributes.put(key, value);
    }

    public void setCookie(String sessionId, String login, String accountId, String accountRole) {
        cookieSet.put("sessionId", sessionId);
        cookieSet.put("accountLogin", login);
        cookieSet.put("accountRole", accountRole.toLowerCase());
        cookieSet.put("accountId", accountId);
    }

    public void setUpdateCookie(boolean updateCookie) {
        this.updateCookie = updateCookie;
    }
}
