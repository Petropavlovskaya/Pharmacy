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


@WebFilter(urlPatterns = {"/pharmacist/*", "/doctor/*", "/admin/*", "/customer/*"})
public class AccountFilter implements Filter {
    private static final Set<String> BUSINESS_ACCOUNT_URIS = new HashSet<>();
    public static final String COMMAND_ATTRIBUTE = "command";


    @Override
    public void init(FilterConfig filterConfig) {
        BUSINESS_ACCOUNT_URIS.add("main");
        BUSINESS_ACCOUNT_URIS.add("medicine");
        BUSINESS_ACCOUNT_URIS.add("cabinet");
        BUSINESS_ACCOUNT_URIS.add("recipe");
        BUSINESS_ACCOUNT_URIS.add("account");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String fullUri = req.getRequestURI();
        String[] arrayUri = fullUri.substring(1).split("/");
        String uriRole = arrayUri[1];
        String accountAccess;
        if (arrayUri.length > 2) {
            accountAccess = arrayUri[2];
        } else {
            accountAccess = "";
        }

        UrlFilter.getCookie(req);
        String accountRole = String.valueOf(req.getSession().getAttribute("accountRole"));
        String sessionId = String.valueOf(req.getSession().getAttribute("sessionId"));

        if ((accountRole.equals("") && sessionId.equals("null")) || (accountRole.equals("") && sessionId.equals(""))) {
            resp.sendRedirect("/pharmacy/login");
        } else if (BUSINESS_ACCOUNT_URIS.contains(accountAccess) && uriRole.equals(accountRole)) {
            req.getSession().setAttribute("fullUri", fullUri);
            req.setAttribute(COMMAND_ATTRIBUTE, accountRole);
            req.getRequestDispatcher("/app").forward(servletRequest, servletResponse);
        } else if (!uriRole.equals(accountRole)) {
            resp.sendRedirect("/pharmacy/access");
        } else if (!BUSINESS_ACCOUNT_URIS.contains(accountAccess)) {
            resp.sendRedirect("/pharmacy/error");
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
