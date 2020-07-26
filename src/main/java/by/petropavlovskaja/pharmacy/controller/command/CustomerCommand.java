package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.dao.OrderDAO;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import by.petropavlovskaja.pharmacy.service.CommonService;
import by.petropavlovskaja.pharmacy.service.CustomerService;
import by.petropavlovskaja.pharmacy.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Class for processing the customer front requests commands, implements {@link IFrontCommand}
 */
public final class CustomerCommand implements IFrontCommand {
    private static Logger logger = LoggerFactory.getLogger(CustomerCommand.class);
    private static CustomerService customerService = CustomerService.getInstance();
    private static CommonService commonService = CommonService.getInstance();
    private static RecipeService recipeService = RecipeService.getInstance();
    private static OrderDAO orderDAO = OrderDAO.getInstance();


    /**
     * Nested class create instance of the class
     */
    private static class CustomerCommandHolder {
        public static final CustomerCommand CUSTOMER_COMMAND = new CustomerCommand();
    }

    /**
     * The override method for get instance of the class
     *
     * @return - class instance
     */
    @Override
    public IFrontCommand getInstance() {
        return CustomerCommandHolder.CUSTOMER_COMMAND;
    }

    /**
     * The override method process customer's GET and POST front requests
     *
     * @param sc - Session context {@link SessionContext}
     * @return - class instance {@link ExecuteResult}
     */
    @Override
    public ExecuteResult execute(SessionContext sc) {
        ExecuteResult executeResult = new ExecuteResult();
        Map<String, Object> reqParameters = sc.getRequestParameters();
        String fullUri = (String) sc.getSession().getAttribute("fullUri");
        String[] arrayUri = fullUri.substring(1).split("/");
        int accountId = Integer.parseInt(String.valueOf(sc.getSession().getAttribute("accountId")));
        Customer customer = setCustomerSessionAttribute(sc, accountId);
        if (sc.getSession().getAttribute("successTextSet") != null) {
            if (sc.getSession().getAttribute("successTextSet").equals("yes")) {
                sc.getSession().setAttribute("successTextSet", "no");
            } else {
                sc.getSession().removeAttribute("successMessage");
            }
        }

        if (sc.getRequestMethod().equals("GET")) {
            String lastUri = arrayUri[arrayUri.length - 1];
            switch (lastUri) {
                case "cart": {
                    customerService.updateCartWithDetails(customer);
                    sc.getSession().setAttribute("cart", customer.getCart());
                    sc.getSession().setAttribute("medicineInCart", customer.getMedicineInCart());
                    break;
                }
                case "history": {
                    customerService.getAllOrdersWithDetails(customer);
                    sc.getSession().setAttribute("orders", customer.getOrdersWithDetails());
                    break;
                }
                case "recipe": {
                    recipeService.getRecipes(customer);
                    sc.getSession().setAttribute("recipe", customer.getRecipes());
                    break;
                }
                case "profile": {
                    customerService.getBalance(customer);
                    executeResult.setResponseAttributes("account", customer);
                    break;
                }
            }
        }

        executeResult.setJsp(arrayUri);


        if (sc.getRequestMethod().equals("POST")) {
            String command = (String) reqParameters.get("frontCommand");

            switch (command) {
                case "medicineForCart": {
                    logger.info(" Command medicineForCart is received.");
                    boolean validData = customerService.addMedicineInOrder(customer, reqParameters);
                    if (!validData) {
                        executeResult.setResponseAttributes("errorMessage", "Quantity of medicine for buy is incorrect. Please, try again.");
                        executeResult.setJsp("/WEB-INF/jsp/customer/medicine/medicine_list.jsp");
                    } else {
                        sc.getSession().setAttribute("successMessage", "The medicine has added successfully.");
                        sc.getSession().setAttribute("successTextSet", "yes");
                        executeResult.setJsp(fullUri);
                    }
                    break;
                }
                case "changeQuantityInCart": {
                    boolean validData = customerService.updateQuantityInCart(customer, reqParameters);
                    if (!validData) {
                        executeResult.setResponseAttributes("errorMessage", "Quantity of medicine for buy is incorrect. Please, try again.");
                        executeResult.setJsp("/WEB-INF/jsp/customer/cabinet/cabinet_cart.jsp");
                    } else {
                        sc.getSession().setAttribute("successMessage", "The medicine quantity has changed successfully.");
                        sc.getSession().setAttribute("successTextSet", "yes");
                        sc.getSession().setAttribute("medicineInCart", customer.getMedicineInCart());
                        sc.getSession().setAttribute("cart", customer.getCart());
                        executeResult.setJsp(fullUri);
                    }
                    break;
                }
                case "deleteFromCart": {
                    sc.getSession().setAttribute("successMessage", "The medicine has deleted successfully.");
                    sc.getSession().setAttribute("successTextSet", "yes");
                    customerService.deleteMedicineFromCart(customer, reqParameters);
                    sc.getSession().setAttribute("cart", customer.getCart());
                    sc.getSession().setAttribute("medicineInCart", customer.getMedicineInCart());
                    executeResult.setJsp(fullUri);
                    break;
                }
                case "requestRecipe": {
                    boolean resultInsert = recipeService.customerInsertRecipe(executeResult, customer, reqParameters, fullUri);
                    if (resultInsert) {
                        sc.getSession().setAttribute("successMessage", "The request for recipe has sent to Doctor successfully.");
                        sc.getSession().setAttribute("successTextSet", "yes");
                    }
                    sc.getSession().setAttribute("recipe", customer.getRecipes());
                    break;
                }
                case "extendRecipe": {
                    recipeService.setNeedExtensionByID(customer, reqParameters);
                    sc.getSession().setAttribute("recipe", customer.getRecipes());
                    executeResult.setJsp(fullUri);
                    break;
                }
                case "deleteRecipe": {
                    recipeService.deleteRecipe(customer, reqParameters);
                    sc.getSession().setAttribute("recipe", customer.getRecipes());
                    executeResult.setJsp(fullUri);
                    break;
                }
                case "deleteOrder": {
                    String frontOrderId = (String) reqParameters.get("orderId");
                    boolean number = commonService.isNumber(frontOrderId);
                    if (number) {
                        int orderId = Integer.parseInt(frontOrderId);
                        boolean resultDelete = orderDAO.deleteOrder(orderId);
                        if (resultDelete) {
                            executeResult.setJsp(fullUri);
                            sc.getSession().setAttribute("successMessage", "The order has deleted successfully.");
                            sc.getSession().setAttribute("successTextSet", "yes");
                        } else {
                            executeResult.setResponseAttributes("errorMessage", "Something went wrong. Please, contact with site administrator ");
                        }
                    } else {
                        logger.error("Incorrect data of orderId.");
                        executeResult.setResponseAttributes("errorMessage", "Something went wrong. Please, contact with site administrator ");
                    }

                    break;
                }
                case "buyInCredit":
                case "buy": {
                    boolean updateResult = customerService.createOrder(customer);
                    if (updateResult) {
                        sc.getSession().setAttribute("successMessage", "The purchase medicines has done successfully.");
                        sc.getSession().setAttribute("successTextSet", "yes");
                    }
                    sc.getSession().setAttribute("cart", customer.getCart());
                    sc.getSession().setAttribute("medicineInCart", customer.getMedicineInCart());
                    executeResult.setJsp(fullUri);
                    break;
                }
                case "increaseBill": {
                    boolean increaseResult = customerService.increaseBalance(customer, reqParameters, executeResult);
                    if (increaseResult) {
                        executeResult.setJsp(fullUri);
                        sc.getSession().setAttribute("successMessage", "The bill increasing has done successfully.");
                        sc.getSession().setAttribute("successTextSet", "yes");
                    } else {
                        executeResult.setJsp(arrayUri);
                    }
                    break;
                }
                case "changeAccountData": {
                    boolean successfulUpdate = commonService.changeAccountData(customer, reqParameters, accountId);
                    if (!successfulUpdate) {
                        executeResult.setResponseAttributes("errorMessage", "Something went wrong, please contact with the application administrator.");
                    }
                    executeResult.setJsp(fullUri);
                    break;
                }
                case "changeAccountPassword": {
                    String login = (String) sc.getSession().getAttribute("accountLogin");
                    boolean result = commonService.changePassword(reqParameters, executeResult, login, sc);
                    if (result) {
                        executeResult.setJsp(fullUri);
                    } else {
                        executeResult.setJsp("/WEB-INF/jsp/customer/cabinet/cabinet_profile.jsp");
                    }
                    break;
                }
                default: {
                    logger.error("Command " + command + " is not defined.");
                    executeResult.setJsp("/WEB-INF/jsp/404.jsp");
                    executeResult.setResponseAttributes("errorMessage", "Command is not defined.");
                }
            }
        }
        return executeResult;
    }

    /**
     * The method gets customer instance from database and set it to session attribute
     *
     * @param sc        - Session context {@link SessionContext}
     * @param accountId - customer id
     * @return - customer instance {@link Customer}
     */
    private Customer setCustomerSessionAttribute(SessionContext sc, int accountId) {
        Customer customer;
        if (sc.getSession().getAttribute("customer") == null) {
            customer = commonService.getCustomer(accountId);
            sc.getSession().setAttribute("customer", customer);
        } else {
            customer = (Customer) sc.getSession().getAttribute("customer");
        }
        return customer;
    }
}
