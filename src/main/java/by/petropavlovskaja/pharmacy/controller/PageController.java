package by.petropavlovskaja.pharmacy.controller;

import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import by.petropavlovskaja.pharmacy.service.CommonService;
import by.petropavlovskaja.pharmacy.service.CustomerService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** Pagination controller, extends {@link HttpServlet} */
@WebServlet(name = "PageServlet", urlPatterns = "/page")
public final class PageController extends HttpServlet {
    private static CommonService commonService = CommonService.getInstance();
    private static CustomerService customerService = CustomerService.getInstance();
    private static MedicineDAO medicineDAO = MedicineDAO.getInstance();

    /**
     * The override method doGet
     * @param req  - http servlet request
     * @param resp - http servlet response
     * @throws IOException      - throws IOException
     * @throws ServletException - throws ServletException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher;
        List<Medicine> medicineList;

        // requestPage was set in Filter
        int requestPage = Integer.parseInt(String.valueOf(req.getSession().getAttribute("requestPage")));
        int recordsPerPage;

        // check if recordsPerPage is available
        if (req.getSession().getAttribute("recordsPerPage") != null) {
            recordsPerPage = Integer.parseInt(String.valueOf(req.getSession().getAttribute("recordsPerPage")));
        } else {
            recordsPerPage = 5;
            req.getSession().setAttribute("recordsPerPage", 5);
        }

        int maxPage = countNumOfPages(recordsPerPage);
        req.getSession().setAttribute("numOfPages", maxPage);

        if (maxPage != 0 && requestPage > maxPage) {
            dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/404.jsp");
        } else {
            medicineList = medicineDAO.findMedicine(requestPage, recordsPerPage);
            req.getSession().setAttribute("currentPage", requestPage);
            req.getSession().setAttribute("recordsPerPage", recordsPerPage);
            req.getSession().setAttribute("medicineList", medicineList);
            if (req.getSession().getAttribute("accountRole") != null && req.getSession().getAttribute("accountRole") != "") {
                String accountRole = String.valueOf(req.getSession().getAttribute("accountRole"));
                if (accountRole.equals("customer")) {
                    int accountId = Integer.parseInt(String.valueOf(req.getSession().getAttribute("accountId")));
                    Customer customer = commonService.getCustomer(accountId);
                    customerService.checkAvailableRecipe(customer, medicineList);
                }
                dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/" + accountRole + "/medicine/medicine_list.jsp");
            } else {
                dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/medicine.jsp");
            }
        }
        dispatcher.forward(req, resp);
    }

    /**
     * The override method doPost
     * @param req  - http servlet request
     * @param resp - http servlet response
     * @throws IOException - throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fullUri = (String) req.getSession().getAttribute("fullUri");
        int currentPage = Integer.parseInt(String.valueOf(req.getSession().getAttribute("requestPage")));
        int recordsPerPage = 0;
        if (req.getParameter("recordsPerPage") != null) {
            recordsPerPage = Integer.parseInt(req.getParameter("recordsPerPage"));
        }
        req.getSession().setAttribute("recordsPerPage", recordsPerPage);
        int numOfPages = countNumOfPages(recordsPerPage);
        req.getSession().setAttribute("numOfPages", numOfPages);
        List<Medicine> medicineList = medicineDAO.findMedicine(currentPage, recordsPerPage);
        req.getSession().setAttribute("medicineList", medicineList);

        resp.sendRedirect(fullUri);
//        resp.sendRedirect((String) req.getSession().getAttribute("fullUri"));
    }

    /**
     * The method counts number of pages by records per page
     * @param recordsPerPage  - records per page
     * @return - count of pages
     */
    private int countNumOfPages(int recordsPerPage) {
        int rows = medicineDAO.getNumberOfRows();
        int numOfPages = rows / recordsPerPage;
        if (numOfPages % recordsPerPage > 0) {
            numOfPages++;
        }
        return numOfPages;
    }
}
