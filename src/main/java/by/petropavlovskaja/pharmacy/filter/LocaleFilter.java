package by.petropavlovskaja.pharmacy.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Web filter for locale request
 */
@WebFilter(filterName = "LocaleFilter", urlPatterns = {"/*"})
public class LocaleFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(LocaleFilter.class);

    /**
     * The override method for do filter {@link Filter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     *
     * @param chain    - filter chain
     * @param request  - servlet request
     * @param response - servlet response
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (req.getParameter("locale") != null) {
            String reqLocale = req.getParameter("locale");
            logger.info("Set LANG in Filter from Request. Lang = " + reqLocale);
            if (reqLocale.equals("ru") || reqLocale.equals("en") || reqLocale.equals("pl")) {
                req.getSession().setAttribute("lang", req.getParameter("locale"));
                req.getSession().setAttribute("updateLang", "TRUE");
            } else {
                req.getSession().setAttribute("lang", "ru");
            }
            resp.sendRedirect(req.getRequestURI());
            return;
        }
        if (req.getSession().getAttribute("lang") == null || req.getSession().getAttribute("lang").equals("")) {
            req.getSession().setAttribute("lang", "ru");
        }
        chain.doFilter(request, response);
    }
}
