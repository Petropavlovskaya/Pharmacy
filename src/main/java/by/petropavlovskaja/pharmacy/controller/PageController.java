package by.petropavlovskaja.pharmacy.controller;

import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.MedicineInOrder;
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
import java.util.Set;

/**
 * Pagination controller, extends {@link HttpServlet}
 */
@WebServlet(name = "PageServlet", urlPatterns = "/page")
public final class PageController extends HttpServlet {
    private static CommonService commonService = CommonService.getInstance();
    private static CustomerService customerService = CustomerService.getInstance();
    private static MedicineDAO medicineDAO = MedicineDAO.getInstance();
    private String lastUri = "";

    /**
     * The override method doGet
     *
     * @param req  - http servlet request
     * @param resp - http servlet response
     * @throws IOException      - throws IOException
     * @throws ServletException - throws ServletException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher;
        List<Medicine> medicineList;

        lastUri = getLastPartUri(req);

        // requestPage was set in Filter
        int requestPage = 1;
        String requestPageAttribute = String.valueOf(req.getSession().getAttribute("requestPage"));
        if (commonService.isNumber(requestPageAttribute)) {
            requestPage = Integer.parseInt(requestPageAttribute);
        }
        int recordsPerPage = setRecordsPerPage(req);

        int maxPage = countNumOfPages(recordsPerPage, lastUri);
        req.getSession().setAttribute(AttributeConstant.NUMBER_OF_PAGES, maxPage);

        if (maxPage != 0 && requestPage > maxPage) {
            dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/404.jsp");
        } else {
            medicineList = medicineDAO.findMedicine(requestPage, recordsPerPage);
            req.getSession().setAttribute(AttributeConstant.CURRENT_PAGE, requestPage);
            req.getSession().setAttribute(AttributeConstant.RECORDS_PER_PAGE, recordsPerPage);
            req.getSession().setAttribute(AttributeConstant.MEDICINE_LIST, medicineList);
            if (req.getSession().getAttribute(AttributeConstant.ACCOUNT_ROLE) != null
                    && req.getSession().getAttribute(AttributeConstant.ACCOUNT_ROLE) != "") {
                dispatcher = getAccountRequestDispatcher(req, medicineList, requestPage, recordsPerPage);
            } else {
                dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/medicine.jsp");
            }
        }
        dispatcher.forward(req, resp);
    }

    /**
     * The method set recipe and cart details if an account belong to a customer
     *
     * @param req            - HttpServletRequest req
     * @param medicineList   - total list of medicines
     * @param requestPage    - number of request page
     * @param recordsPerPage - number of records per page
     * @return - request dispatcher that defines by account role
     */
    public RequestDispatcher getAccountRequestDispatcher(HttpServletRequest req, List<Medicine> medicineList,
                                                         int requestPage, int recordsPerPage) {
        String accountRole = String.valueOf(req.getSession().getAttribute(AttributeConstant.ACCOUNT_ROLE));
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/" + accountRole + "/medicine/medicine_list.jsp");

        if (accountRole.equals("customer")) {
            int accountId = Integer.parseInt(String.valueOf(req.getSession().getAttribute(AttributeConstant.ACCOUNT_ID)));
            Customer customer = commonService.getCustomer(accountId);
            customerService.checkAvailableRecipe(customer, medicineList);
            customerService.updateCartWithDetails(customer);
            Set<MedicineInOrder> medicineInOrderSet = customer.getMedicineInCart();
            req.getSession().setAttribute(AttributeConstant.MEDICINE_IN_CART, medicineInOrderSet);
            for (Medicine medicine : medicineList) {
                for (MedicineInOrder medicineInOrder : medicineInOrderSet) {
                    if (medicine.getName().equals(medicineInOrder.getMedicine()) &&
                            medicine.getDosage().equals(medicineInOrder.getDosage()) &&
                            medicine.getIndivisibleAmount() == medicineInOrder.getIndivisibleAmount()) {
                        medicine.setCountInCustomerCart(medicineInOrder.getQuantity());
                    }
                }
            }
        }

        if (accountRole.equals("pharmacist") && lastUri.equals("expired")) {
            List<Medicine> expiredMedicineList = medicineDAO.findExpiredMedicine(requestPage, recordsPerPage);
            req.getSession().setAttribute(AttributeConstant.EXPIRED_MEDICINE_LIST, expiredMedicineList);
            dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/pharmacist/medicine/medicine_expired.jsp");
        }

        return dispatcher;
    }

    /**
     * The method for set count of records on a page
     *
     * @param req - HttpServletRequest request
     * @return default value of records per or value that user was set
     */
    public int setRecordsPerPage(HttpServletRequest req) {
        int methodResult = 5;   // default value of records per page
        // check if recordsPerPage is available
        if (req.getSession().getAttribute(AttributeConstant.RECORDS_PER_PAGE) != null) {
            String recordsCount = String.valueOf(req.getSession().getAttribute(AttributeConstant.RECORDS_PER_PAGE));
            if (commonService.isNumber(recordsCount)) {
                methodResult = Integer.parseInt(recordsCount);
            }
        } else {
            req.getSession().setAttribute(AttributeConstant.RECORDS_PER_PAGE, 5);
        }
        return methodResult;
    }

    /**
     * The override method doPost
     *
     * @param req  - http servlet request
     * @param resp - http servlet response
     * @throws IOException - throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fullUri = (String) req.getSession().getAttribute("fullUri");
        lastUri = getLastPartUri(req);

        String currentPageAttribute = String.valueOf(req.getSession().getAttribute(AttributeConstant.REQUEST_PAGE));
        int currentPage = 1;
        if (commonService.isNumber(currentPageAttribute)) {
            currentPage = Integer.parseInt(currentPageAttribute);
        }
        int recordsPerPage = 5;
        String recordsPerPageAttribute = req.getParameter("recordsPerPage");
        if (req.getParameter(AttributeConstant.RECORDS_PER_PAGE) != null && commonService.isNumber(recordsPerPageAttribute)) {
            recordsPerPage = Integer.parseInt(recordsPerPageAttribute);
        }
        req.getSession().setAttribute(AttributeConstant.RECORDS_PER_PAGE, recordsPerPage);
        int numOfPages = countNumOfPages(recordsPerPage, lastUri);
        req.getSession().setAttribute("numOfPages", numOfPages);
        List<Medicine> medicineList = medicineDAO.findMedicine(currentPage, recordsPerPage);
        req.getSession().setAttribute("medicineList", medicineList);

        resp.sendRedirect(fullUri);
    }

    /**
     * The method counts number of pages by records per page
     *
     * @param recordsPerPage - records per page
     * @return - count of pages
     */
    private int countNumOfPages(int recordsPerPage, String lastUri) {
        int rows;
        if (lastUri.equals("expired")) {
            rows = medicineDAO.getNumberOfRows(true);
        } else {
            rows = medicineDAO.getNumberOfRows(false);
        }
        int numOfPages = rows / recordsPerPage;
        if (rows % recordsPerPage > 0) {
            numOfPages++;
        }
        return numOfPages;
    }

    /**
     * The method split full URI and return last part of it
     *
     * @param req - HttpServletRequest
     * @return - last part of request URI
     */
    private String getLastPartUri(HttpServletRequest req) {
        String fullUri = (String) req.getSession().getAttribute("fullUri");
        String[] arrayUri = fullUri.substring(1).split("/");
        return arrayUri[arrayUri.length - 1];
    }
}
