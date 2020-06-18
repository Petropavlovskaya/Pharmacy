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
import java.util.Map;
import java.util.Set;

public class SessionContext {
    private static Logger logger = LoggerFactory.getLogger(SessionContext.class);

    private static String requestMethod;
    private static Map<String, Object> requestParameters;
    private static HttpSession session;

    private static class SessionContextHolder {
        public static final SessionContext SESSION_CONTEXT = new SessionContext();
    }

    public static SessionContext getSessionContextInstance(HttpServletRequest request) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("Can't set character encoding for request. " + e);
        }
        requestParameters = new HashMap<>();
        requestMethod = request.getMethod();
        session = request.getSession();
        if (requestMethod.equals("POST")) {
            setParameterOfRequest(request);
        }
        return SessionContextHolder.SESSION_CONTEXT;
    }

    private static void setParameterOfRequest(HttpServletRequest request) {
        Enumeration<String> keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            requestParameters.put(key, request.getParameter(key));
        }
        logger.info("We set next req param: " + requestParameters.toString());
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setSessionAttributesForAccount(Account account, String login, String sessionId, Set<Medicine> medicineList) {
        session.setAttribute("sessionId", sessionId);
        session.setAttribute("accountId", String.valueOf(account.getId()));
        session.setAttribute("accountRole", account.getAccountRole().toString().toLowerCase());
        session.setAttribute("accountLogin", login);
        session.setAttribute("medicineList", medicineList);
        session.setAttribute("account", account);
    }

    public Map<String, Object> getRequestParameters() {
        return requestParameters;
    }

    public HttpSession getSession() {
        return session;
    }
}
