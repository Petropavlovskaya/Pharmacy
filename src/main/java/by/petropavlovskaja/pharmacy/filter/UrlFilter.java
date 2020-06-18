package by.petropavlovskaja.pharmacy.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@WebFilter(urlPatterns = "/*")
public class UrlFilter implements Filter {

    private static final Set<String> BUSINESS_URIS = new HashSet<>();
    public static final String COMMAND_ATTRIBUTE = "command";

    @Override
    public void init(FilterConfig filterConfig) {
        BUSINESS_URIS.add("signup");
        BUSINESS_URIS.add("login");
        BUSINESS_URIS.add("logout");
        BUSINESS_URIS.add("medicine");
        BUSINESS_URIS.add("main");
        BUSINESS_URIS.add("pharmacy");
        BUSINESS_URIS.add("access");
        BUSINESS_URIS.add("error");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        RequestDispatcher dispatcher;

        String[] arrayUri = req.getRequestURI().substring(1).split("/");
        String lastPartUri = arrayUri[arrayUri.length - 1];

        if (!BUSINESS_URIS.contains(lastPartUri) && !lastPartUri.contains(".gif") && !lastPartUri.contains(".jpg")) {
            lastPartUri = "error";
        }

        switch (lastPartUri) {
            case "error": {
                dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/404.jsp");
                dispatcher.forward(servletRequest, servletResponse);
                break;
            }
            case "access": {
                dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/no_access.jsp");
                dispatcher.forward(servletRequest, servletResponse);
                break;
            }
            case "pharmacy":
            case "main": {
                getCookie(req);
                String accountRole = String.valueOf(req.getSession().getAttribute("accountRole"));
                String sessionId = String.valueOf(req.getSession().getAttribute("sessionId"));
                if (!accountRole.equals("") && !sessionId.equals("null")) {
                    resp.sendRedirect("/pharmacy/" + accountRole/*.toLowerCase()*/ + "/main");
                } else {
                    servletRequest.getRequestDispatcher("/WEB-INF/jsp/main.jsp").forward(servletRequest, servletResponse);
                }
                break;
            }
            case "login":
            case "logout":
            case "medicine":
            case "signup": {
                req.setAttribute(COMMAND_ATTRIBUTE, lastPartUri);
                servletRequest.getRequestDispatcher("/app").forward(servletRequest, servletResponse);
                break;
            }
            default: {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }

    @Override
    public void destroy() {
    }

    public static void getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                request.getSession().setAttribute(c.getName(), c.getValue().toLowerCase());
            }
        }
    }
}