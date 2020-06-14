package by.petropavlovskaja.pharmacy.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebFilter(urlPatterns = "/*")
public class UrlFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(UrlFilter.class);

    private static final Set<String> BUSINESS_URIS = new HashSet<>();
    public static final String COMMAND_ATTRIBUTE = "command";

    static {
/*        CommonService service = CommonService.getInstance();
        medicineList = service.findAllMedicine();
        logger.info("Medicine list loaded successfully.");*/
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
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
        HttpSession session = req.getSession();
        RequestDispatcher dispatcher;

        String[] arrayUri = req.getRequestURI().substring(1).split("/");
        String lastPartUri = arrayUri[arrayUri.length - 1];
// DELETE IT
        System.out.println("Uri[] in urlFilter = " + Arrays.toString(arrayUri) + " Size is: " + arrayUri.length + " ********************************************");
        System.out.println("In URL filter. fullUri =  " + req.getRequestURI());

        if(!BUSINESS_URIS.contains(lastPartUri)){
            lastPartUri = "error";
        }

        switch (lastPartUri) {
            case "error": {
                System.out.println("--------------------------- URL FILTER---------------- lastPartUri.equals(\"error\") ------");
                dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/404.jsp");
                dispatcher.forward(servletRequest, servletResponse);
                break;
            }
            case "access": {
                System.out.println("--------------------------- URL FILTER---------------- access ------");
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
                    System.out.println("--------------------------- URL FILTER---------------- active login ------");
                    resp.sendRedirect("/pharmacy/" + accountRole/*.toLowerCase()*/ + "/main");
                } else {
                    System.out.println("--------------------------- URL FILTER---------------- not active login ------");
                    servletRequest.getRequestDispatcher("/WEB-INF/jsp/main.jsp").forward(servletRequest, servletResponse);
                }
                break;
            }
            case "login":
            case "logout":
            case "medicine":
            case "signup": {
                System.out.println("--------------------------- URL FILTER---------------- URI COLLECTION ------");
                req.setAttribute(COMMAND_ATTRIBUTE, lastPartUri);
                servletRequest.getRequestDispatcher("/app").forward(servletRequest, servletResponse);
                break;
            }
            default: {
                System.out.println("--------------------------- URL FILTER---------------- filterChain ------");
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }

    @Override
    public void destroy() {
    }

    public static void getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        System.out.println("Try get cookies...");
        if (cookies != null) {
            for (Cookie c : cookies) {
                request.getSession().setAttribute(c.getName(), c.getValue().toLowerCase());
                System.out.println("URI filter. Get cookie: " + c.getName() + " = " + c.getValue());
            }

        }
    }
}