package by.petropavlovskaja.pharmacy.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Web filter for accounts request. Implements {@link Filter#init(FilterConfig)}. Has next properties:
 * <b>BUSINESS_ACCOUNT_URIS</b> and <b>COMMAND_ATTRIBUTE</b>
 */
@WebFilter(urlPatterns = {"/pharmacist/*", "/doctor/*", "/admin/*", "/customer/*"})
public class AccountFilter implements Filter {

    /**
     * Property - business account uris
     */
    private static final Set<String> BUSINESS_ACCOUNT_URIS = new HashSet<>();
    /**
     * Property - command attribute
     */
    public static final String COMMAND_ATTRIBUTE = "command";

    /**
     * The override method for init filter {@link Filter#init(FilterConfig)}
     *
     * @param filterConfig - filter config
     */
    @Override
    public void init(FilterConfig filterConfig) {
        BUSINESS_ACCOUNT_URIS.add("main");
        BUSINESS_ACCOUNT_URIS.add("medicine");
        BUSINESS_ACCOUNT_URIS.add("cabinet");
        BUSINESS_ACCOUNT_URIS.add("recipe");
        BUSINESS_ACCOUNT_URIS.add("account");
    }

    /**
     * The override method for do filter {@link Filter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     *
     * @param filterChain     - filter chain
     * @param servletRequest  - servlet request
     * @param servletResponse - servlet response
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String fullUri = req.getRequestURI();
        req.getSession().setAttribute("fullUri", fullUri);
        String[] arrayUri = fullUri.substring(1).split("/");
        String uriRole = arrayUri[1];
        String accountAccess = getAccountAccess(arrayUri);

        UrlFilter.getCookie(req);
        String accountRole = String.valueOf(req.getSession().getAttribute("accountRole"));
        String sessionId = String.valueOf(req.getSession().getAttribute("sessionId"));


        if (req.getParameter("locale") != null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else if ((accountRole.equals("") && sessionId.equals("null")) || (accountRole.equals("") && sessionId.equals(""))) {
            resp.sendRedirect("/pharmacy/login");
        } else if (BUSINESS_ACCOUNT_URIS.contains(accountAccess) && uriRole.equals(accountRole)) {
            if (fullUri.contains("medicine/list") && req.getParameter("frontCommand") == null) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                req.setAttribute(COMMAND_ATTRIBUTE, accountRole);
                req.getRequestDispatcher("/app").forward(servletRequest, servletResponse);
            }
        } else if (!uriRole.equals(accountRole)) {
            resp.sendRedirect("/pharmacy/access");
        } else if (!BUSINESS_ACCOUNT_URIS.contains(accountAccess)) {
            resp.sendRedirect("/pharmacy/error");
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private String getAccountAccess(String[] arrayUri) {
        return arrayUri.length > 2 ? arrayUri[2] : "";
    }
}
