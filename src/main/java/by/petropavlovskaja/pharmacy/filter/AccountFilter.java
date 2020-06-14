package by.petropavlovskaja.pharmacy.filter;

import by.petropavlovskaja.pharmacy.service.CommonService;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@WebFilter(urlPatterns = {"/pharmacist/*", "/doctor/*", "/admin/*", "/customer/*"})
public class AccountFilter implements Filter {
    private static final Set<String> BUSINESS_ACCOUNT_URIS = new HashSet<>();
    public static final String COMMAND_ATTRIBUTE = "command";


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        BUSINESS_ACCOUNT_URIS.add("main");
        BUSINESS_ACCOUNT_URIS.add("medicine");
        BUSINESS_ACCOUNT_URIS.add("cabinet");
        BUSINESS_ACCOUNT_URIS.add("recipe");
        BUSINESS_ACCOUNT_URIS.add("account");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        System.out.println("In ACCOUNT filter ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
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
        System.out.println("accountAccess = "+accountAccess);
//        String role = String.valueOf(req.getSession().getAttribute("accountRole"))/*.toLowerCase()*/;
//        System.out.println("UriRole = " + uriRole + ", role = " + role + ". They are: " + uriRole.equals(role));
//        RequestDispatcher dispatcher;

        UrlFilter.getCookie(req);
        String accountRole = String.valueOf(req.getSession().getAttribute("accountRole"));
        String sessionId = String.valueOf(req.getSession().getAttribute("sessionId"));


        System.out.println("In ACCOUNT filter. fullUri =  " + fullUri);
//        System.out.println("req.getSession().getAttribute(\"accountRole\") = " + req.getSession().getAttribute("accountRole"));

        if ((accountRole.equals("") && sessionId.equals("null")) || (accountRole.equals("") && sessionId.equals(""))) {
            System.out.println("--------------------------- ACCOUNT FILTER---------------- role == null ------");
            System.out.println("In ACCOUNT filter. User isn't login. Role = Null");
            resp.sendRedirect("/pharmacy/login");

/*        } else if (arrayUri[arrayUri.length - 1].contains(".gif") || arrayUri[arrayUri.length - 1].contains(".jpg")) {
            System.out.println("--------------------------- ACCOUNT FILTER---------------- IMG ------");
            resp.sendRedirect("/pharmacy/images/" + arrayUri[arrayUri.length - 1]);*/
        } else if (BUSINESS_ACCOUNT_URIS.contains(accountAccess) && uriRole.equals(accountRole)) {
            System.out.println("--------------------------- ACCOUNT FILTER---------------- BUSINESS_ACCOUNT_URIS.contains(accountAccess) && uriRole.equals(role) ------");
            req.getSession().setAttribute("fullUri", fullUri);
            System.out.println("In ACCOUNT filter. uriRole == role");

            req.setAttribute(COMMAND_ATTRIBUTE, accountRole);
            req.getRequestDispatcher("/app").forward(servletRequest, servletResponse);

        } else if (!uriRole.equals(accountRole)) {
            System.out.println("--------------------------- ACCOUNT FILTER---------------- !uriRole.equals(role) ------");
            System.out.println("In ACCOUNT filter. Role: " + accountRole + " != page");
            resp.sendRedirect("/pharmacy/access");
        } else if (!BUSINESS_ACCOUNT_URIS.contains(accountAccess)) {
            System.out.println("--------------------------- ACCOUNT FILTER---------------- error ------");
            resp.sendRedirect("/pharmacy/error");
        } else {
            System.out.println("--------------------------- ACCOUNT FILTER---------------- filterChain ------");
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
