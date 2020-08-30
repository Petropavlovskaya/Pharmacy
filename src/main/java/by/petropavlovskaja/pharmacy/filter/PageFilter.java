package by.petropavlovskaja.pharmacy.filter;

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

import static by.petropavlovskaja.pharmacy.controller.AttributeConstant.*;

/**
 * Web filter for pagination. Implements {@link Filter#init(FilterConfig)}
 */
@WebFilter(urlPatterns = {"/medicine/*", "/customer/medicine/list/*", "/medicine/list/*",
        "/pharmacist/medicine/list/*",  "/pharmacist/medicine/expired/*","/doctor/medicine/list/*"})
public class PageFilter implements Filter {

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
        String[] arrayUri = fullUri.substring(1).split("/");
        String lastPartUri = arrayUri[arrayUri.length - 1];
        if (req.getSession().getAttribute(SUCCESS_MSG_CHECK) != null) {
            if (req.getSession().getAttribute(SUCCESS_MSG_CHECK).equals("yes")) {
                req.getSession().setAttribute(SUCCESS_MSG_CHECK, "no");
            } else {
                req.getSession().removeAttribute(SUCCESS_MSG);
            }
        }


        if (lastPartUri.matches("[1-9][0-9]*")) {
            int page = Integer.parseInt(lastPartUri);
            if (req.getSession().getAttribute("numOfPages") != null) {
                int maxPage = Integer.parseInt(String.valueOf(req.getSession().getAttribute("numOfPages")));
                if (page > maxPage) {
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/404.jsp");
                    dispatcher.forward(servletRequest, servletResponse);
                    return;
                }
            }
            req.getSession().setAttribute("requestPage", page);
            req.getSession().setAttribute("fullUri", getUri(arrayUri));
            req.getRequestDispatcher("/page").forward(req, resp);

        } else if (lastPartUri.equals("list") || lastPartUri.equals("medicine") || lastPartUri.equals("expired")) {
            req.getSession().setAttribute("requestPage", 1);
            req.getSession().setAttribute("fullUri", fullUri);
            req.getRequestDispatcher("/page").forward(req, resp);
        } else {
            resp.sendRedirect("/pharmacy/error");
        }

    }

    /**
     * The method create uri from string array
     *
     * @param arrayUri - string array of uri
     * @return - uri
     */
    private String getUri(String[] arrayUri) {
        StringBuilder uri = new StringBuilder();
        for (int i = 0; i < arrayUri.length - 1; i++) {
            uri.append(arrayUri[i]);
            uri.append("/");
        }
        return uri.toString();
    }
}
