package by.petropavlovskaja.pharmacy.controller.session;

import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.account.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for processing the front requests commands. Has next properties:
 * <b>requestMethod</b>, <b>requestParameters</b> and <b>session</b>
 */
public final class SessionContext {
    private static Logger logger = LoggerFactory.getLogger(SessionContext.class);

    /**
     * Property - request method
     */
    private static String requestMethod;
    /**
     * Property - request parameters
     */
    private static Map<String, Object> requestParameters;
    /**
     * Property - session
     */
    private static HttpSession session;

    /**
     * Nested class create instance of the class
     */
    private static class SessionContextHolder {
        public static final SessionContext SESSION_CONTEXT = new SessionContext();
    }

    /**
     * The method for get instance of the class
     *
     * @param request - http servlet request
     * @return - class instance
     */
    public static SessionContext getSessionContextInstance(HttpServletRequest request) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("Can't set character encoding for request. ", e);
        }
        requestParameters = new HashMap<>();
        requestMethod = request.getMethod();
        session = request.getSession();
        if (requestMethod.equals("POST")) {
            setParameterOfRequest(request);
        }
        return SessionContextHolder.SESSION_CONTEXT;
    }

    /**
     * The method for setting the request parameters {@link SessionContext#requestParameters}
     *
     * @param request - http servlet request
     */
    private static void setParameterOfRequest(HttpServletRequest request) {
        Enumeration<String> keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            requestParameters.put(key, request.getParameter(key));
        }
        String loggerMessage = "We set next req param: " + requestParameters.toString();
        logger.info(loggerMessage);
    }

    /**
     * The method of getting the request method {@link SessionContext#requestMethod}
     *
     * @return - String value of request method
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * The method for setting the session attributes for account {@link SessionContext#session}
     *
     * @param account      - account
     * @param login        - account login
     * @param sessionId    - session ID
     * @param medicineList - list of medicines
     */
    public void setSessionAttributesForAccount(Account account, String login, String sessionId, List<Medicine> medicineList) {
        session.setAttribute("sessionId", sessionId);
        session.setAttribute("accountId", String.valueOf(account.getId()));
        session.setAttribute("accountRole", account.getAccountRole().toString().toLowerCase());
        session.setAttribute("accountLogin", login);
        session.setAttribute("medicineList", medicineList);
        session.setAttribute("account", account);
    }

    /**
     * The method of getting the request parameters {@link SessionContext#requestParameters}
     *
     * @return - map of request parameters
     */
    public Map<String, Object> getRequestParameters() {
        return requestParameters;
    }

    /**
     * The method of getting the session {@link SessionContext#session}
     *
     * @return - HttpSession
     */
    public HttpSession getSession() {
        return session;
    }
}
