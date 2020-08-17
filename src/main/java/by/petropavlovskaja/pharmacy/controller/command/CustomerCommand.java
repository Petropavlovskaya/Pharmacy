package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.AttributeConstant;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
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

        commonService.checkSuccessMessageSet(sc);

        executeResult.setJsp(arrayUri);
        if (sc.getRequestMethod().equals("GET")) {
            String lastUri = arrayUri[arrayUri.length - 1];
            switch (lastUri) {
                case "cart": {
                    customerService.updateCartWithDetails(customer);
                    sc.getSession().setAttribute(AttributeConstant.CART, customer.getCart());
                    sc.getSession().setAttribute(AttributeConstant.MEDICINE_IN_CART, customer.getMedicineInCart());
                    break;
                }
                case "history": {
                    customerService.getAllOrdersWithDetails(customer);
                    sc.getSession().setAttribute(AttributeConstant.ORDERS, customer.getOrdersWithDetails());
                    break;
                }
                case "recipe": {
                    recipeService.getRecipes(customer);
                    sc.getSession().setAttribute(AttributeConstant.RECIPE, customer.getRecipes());
                    break;
                }
                case "profile": {
                    customerService.getBalance(customer);
                    executeResult.setResponseAttributes(AttributeConstant.ORDERS, customer);
                    break;
                }
                case "main": {
                    break;
                }
                default: {
                    String error = "Uri " + lastUri + " was not define.";
                    logger.error(error);
                    executeResult.setJsp("/WEB-INF/jsp/404.jsp");
                    executeResult.setResponseAttributes(AttributeConstant.ERROR_MSG, error);
                }
            }
        }


        if (sc.getRequestMethod().equals("POST")) {
            String command = (String) reqParameters.get("frontCommand");
            executeResult.setJsp(fullUri);

            switch (command) {
                case "medicineForCart": {
                    logger.info(" Command medicineForCart is received.");
                    customerService.addMedicineInOrder(customer, reqParameters, executeResult, sc);
                    break;
                }
                case "changeQuantityInCart": {
                    customerService.updateQuantityInCart(customer, reqParameters, executeResult, sc);
                    break;
                }
                case "deleteFromCart": {
                    customerService.deleteMedicineFromCart(customer, reqParameters, sc);
                    sc.getSession().setAttribute(AttributeConstant.CART, customer.getCart());
                    sc.getSession().setAttribute(AttributeConstant.MEDICINE_IN_CART, customer.getMedicineInCart());
                    break;
                }
                case "requestRecipe": {
                    recipeService.customerInsertRecipe(executeResult, customer, reqParameters, sc);
                    sc.getSession().setAttribute(AttributeConstant.RECIPE, customer.getRecipes());
                    break;
                }
                case "extendRecipe": {
                    recipeService.setNeedExtensionByID(customer, reqParameters);
                    sc.getSession().setAttribute(AttributeConstant.RECIPE, customer.getRecipes());
                    break;
                }
                case "deleteRecipe": {
                    recipeService.deleteRecipe(customer, reqParameters, sc);
                    sc.getSession().setAttribute(AttributeConstant.RECIPE, customer.getRecipes());
                    break;
                }
                case "deleteOrder": {
                    String frontOrderId = (String) reqParameters.get("orderId");
                    customerService.deleteOrder(frontOrderId, executeResult, sc);
                    break;
                }
                case "buyInCredit":
                case "buy": {
                    customerService.createOrder(customer, sc);
                    sc.getSession().setAttribute(AttributeConstant.CART, customer.getCart());
                    sc.getSession().setAttribute(AttributeConstant.MEDICINE_IN_CART, customer.getMedicineInCart());
                    break;
                }
                case "increaseBill": {
                    customerService.increaseBalance(customer, reqParameters, executeResult, sc);
                    break;
                }
                case "changeAccountData": {
                    boolean successfulUpdate = commonService.changeAccountData(customer, reqParameters, accountId, sc);
                    if (!successfulUpdate) {
                        executeResult.setResponseAttributes(AttributeConstant.ERROR_MSG, "Insert data was incorrect. Please, try again.");
                        executeResult.setResponseAttributes(AttributeConstant.ACCOUNT, customer);
                        executeResult.setJsp("/WEB-INF/jsp/customer/cabinet/cabinet_profile.jsp");
                    }
                    break;
                }
                case "changeAccountPassword": {
                    String login = (String) sc.getSession().getAttribute(AttributeConstant.ACCOUNT_LOGIN);
                    boolean result = commonService.changePassword(reqParameters, executeResult, login, sc);
                    if (!result) {
                        executeResult.setJsp("/WEB-INF/jsp/customer/cabinet/cabinet_profile.jsp");
                    }
                    break;
                }
                default: {
                    String error = "Command " + command + " is not defined.";
                    logger.error(error);
                    executeResult.setJsp("/WEB-INF/jsp/404.jsp");
                    executeResult.setResponseAttributes(AttributeConstant.ERROR_MSG, "Command is not defined.");
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
        if (sc.getSession().getAttribute(AttributeConstant.CUSTOMER) == null) {
            customer = commonService.getCustomer(accountId);
            sc.getSession().setAttribute(AttributeConstant.CUSTOMER, customer);
        } else {
            customer = (Customer) sc.getSession().getAttribute(AttributeConstant.CUSTOMER);
        }
        return customer;
    }
}
