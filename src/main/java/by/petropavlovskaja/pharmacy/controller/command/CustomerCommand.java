package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import by.petropavlovskaja.pharmacy.service.CommonService;
import by.petropavlovskaja.pharmacy.service.CustomerService;
import by.petropavlovskaja.pharmacy.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class CustomerCommand implements IFrontCommand {
    private static Logger logger = LoggerFactory.getLogger(CustomerCommand.class);
    private static CustomerService customerService = CustomerService.getInstance();
    private static CommonService commonService = CommonService.getInstance();
    private static RecipeService recipeService = RecipeService.getInstance();

    private static class CustomerCommandHolder {
        public static final CustomerCommand CUSTOMER_COMMAND = new CustomerCommand();
    }

    @Override
    public IFrontCommand getInstance() {
        return CustomerCommandHolder.CUSTOMER_COMMAND;
    }

    @Override
    public ExecuteResult execute(SessionContext sc) {
        ExecuteResult executeResult = new ExecuteResult();
        Map<String, Object> reqParameters = sc.getRequestParameters();
        String fullUri = (String) sc.getSession().getAttribute("fullUri");
        String[] arrayUri = fullUri.substring(1).split("/");
        int accountId = Integer.parseInt(String.valueOf(sc.getSession().getAttribute("accountId")));
        Customer customer = setCustomerSessionAttribute(sc, accountId);

        if (sc.getRequestMethod().equals("GET")) {
            String lastUri = arrayUri[arrayUri.length - 1];
            switch (lastUri) {
                case "list": {
                    Set<Medicine> medicineList = commonService.getAllMedicine();
                    customerService.checkAvailableRecipe(customer, medicineList);
                    sc.getSession().setAttribute("medicineList", medicineList);
                    break;
                }
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
                    break;
                }
/*                case "favorite": {
                    break;
                }*/
            }
        }

        executeResult.setJsp(arrayUri);


        if (sc.getRequestMethod().equals("POST")) {
            String command = (String) reqParameters.get("customerCommand");
            switch (command) {
                case "medicineForCart": {
                    logger.info(" Command medicineForCart is received.");
                    customerService.addMedicineInOrder(customer, reqParameters);
                    break;
                }
                case "changeQuantityInCart": {
                    customerService.updateQuantityInCart(customer, reqParameters);
                    sc.getSession().setAttribute("cart", customer.getCart());
                    sc.getSession().setAttribute("medicineInCart", customer.getMedicineInCart());
                    break;
                }
                case "deleteFromCart": {
                    customerService.deleteMedicineFromCart(customer, reqParameters);
                    sc.getSession().setAttribute("cart", customer.getCart());
                    sc.getSession().setAttribute("medicineInCart", customer.getMedicineInCart());
                    break;
                }
                case "requestRecipe": {
                    recipeService.customerInsertRecipe(customer, reqParameters);
                    sc.getSession().setAttribute("recipe", customer.getRecipes());
                    break;
                }
                case "extendRecipe": {
                    recipeService.setNeedExtensionByID(customer, reqParameters);
                    sc.getSession().setAttribute("recipe", customer.getRecipes());
                    break;
                }
                case "deleteRecipe": {
                    recipeService.deleteRecipe(customer, reqParameters);
                    sc.getSession().setAttribute("recipe", customer.getRecipes());
                    break;
                }
                case "buyInCredit":
                case "buy": {
                    customerService.createOrder(customer);
                    sc.getSession().setAttribute("cart", customer.getCart());
                    sc.getSession().setAttribute("medicineInCart", customer.getMedicineInCart());
                    break;
                }
/*                case "medicineInFavorite": {
                    break;
                }*/
/*                case "medicineOutOfFavorite": {
                    break;
                }*/
                default: {
                    logger.error("Command " + command + " is not defined.");
                }
            }
            executeResult.setJsp(fullUri);
        }
        return executeResult;
    }

    private Customer setCustomerSessionAttribute(SessionContext sc, int accountId) {
        Customer customer = (Customer) sc.getSession().getAttribute("customer");
        if (sc.getSession().getAttribute("customer") == null) {
            customer = commonService.getCustomer(accountId);
            sc.getSession().setAttribute("customer", customer);
        }
        return customer;
    }
}
