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

/**
 * Class for end processing front request. Has next properties:
 * <b>responseAttributes</b>, <b>cookieSet</b>, <b>updateCookie</b> and <b>jsp</b>
 */
public final class ExecuteResult {
    private static Logger logger = LoggerFactory.getLogger(ExecuteResult.class);

    /**
     * Property - response attributes
     */
    private Map<String, Object> responseAttributes = new HashMap<>();
    /**
     * Property - cookie set
     */
    private Map<String, String> cookieSet = new HashMap<>();
    /**
     * Property - update cookie
     */
    private boolean needUpdateCookie = false;
    /**
     * Property - jsp
     */
    private static String jsp;

    /**
     * The method finishes processing the entire front request
     *
     * @param request  - http servlet request
     * @param response - http servlet response
     */
    public void complete(HttpServletRequest request, HttpServletResponse response) {
        if (!responseAttributes.isEmpty()) {
            Set<String> set = responseAttributes.keySet();
            for (String s : set) {
                request.setAttribute(s, responseAttributes.get(s));
            }
        }

        if (needUpdateCookie) {
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
                if (request.getAttribute("updateLang") != "null") {
                    boolean updateLang = Boolean.parseBoolean((String) request.getSession().getAttribute("updateLang"));
                    if (updateLang) {
                        logger.info("Need to update LANG. New Lang for cookie = " + request.getSession().getAttribute("lang"));
                        Cookie cookie = new Cookie("lang", (String) request.getSession().getAttribute("lang"));
                        response.addCookie(cookie);
                        cookie.setMaxAge(259200);
                        request.getSession().setAttribute("updateLang", "false");
                    }
                }
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

    /**
     * The method checks is request jsp exist. If jsp doesn't exist set NotFound page
     *
     * @param request - http servlet request
     */
    private void checkJSP(HttpServletRequest request) {
        String realPath = request.getServletContext().getRealPath("") + jsp;
        File file = new File(realPath);
        if (!file.exists()) {
            jsp = "/WEB-INF/jsp/404.jsp";
            logger.error("File " + realPath + " wasn't found!");
        }
    }

    /**
     * The method for setting the jsp {@link ExecuteResult#jsp}
     *
     * @param jsp - jsp
     * @see ExecuteResult#setJsp(String[])
     */
    public void setJsp(String jsp) {
        ExecuteResult.jsp = jsp;
    }

    /**
     * TThe method for setting the jsp {@link ExecuteResult#jsp}
     *
     * @param arrayUri - uri
     * @see ExecuteResult#setJsp(String)
     */
    public void setJsp(String[] arrayUri) {
        String jsp = "/WEB-INF/jsp/";
        for (int i = 1; i < arrayUri.length - 1; i++) {
            jsp = jsp + arrayUri[i] + "/";
        }
        jsp = jsp + arrayUri[arrayUri.length - 2] + "_" + arrayUri[arrayUri.length - 1] + ".jsp";

        ExecuteResult.jsp = jsp;
    }

    /**
     * The method for setting the response attributes {@link ExecuteResult#responseAttributes}
     *
     * @param key   - key
     * @param value - value
     */
    public void setResponseAttributes(String key, Object value) {
        responseAttributes.put(key, value);
    }

    /**
     * The method for setting the cookies {@link ExecuteResult#cookieSet}
     *
     * @param accountId   - account ID
     * @param accountRole - account role
     * @param lang        - language
     * @param login       - account login
     * @param sessionId   - session ID
     */
    public void setCookie(String sessionId, String login, String accountId, String accountRole, String lang) {
        cookieSet.put("sessionId", sessionId);
        cookieSet.put("accountLogin", login);
        cookieSet.put("accountRole", accountRole.toLowerCase());
        cookieSet.put("accountId", accountId);

        cookieSet.put("lang", lang);
        logger.info("Ex result, set cookie locale = " + lang);
    }

    /**
     * The method for setting the needUpdateCookie {@link ExecuteResult#needUpdateCookie}
     *
     * @param updateCookie - is cookie need to update value
     */
    public void isNeedUpdateCookie(boolean updateCookie) {
        this.needUpdateCookie = updateCookie;
    }

}
