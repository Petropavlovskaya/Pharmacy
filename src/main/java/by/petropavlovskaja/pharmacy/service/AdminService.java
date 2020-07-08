package by.petropavlovskaja.pharmacy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for administrator services. The role of the administrator is in the future development of the program */
public class AdminService {
    private static Logger logger = LoggerFactory.getLogger(AdminService.class);
    private CustomerService customerService = CustomerService.getInstance();

    private AdminService() {
    }

    private static class AdminServiceHolder {
        public static final AdminService ADMIN_SERVICE = new AdminService();
    }

    public static AdminService getInstance() {
        return AdminServiceHolder.ADMIN_SERVICE;
    }



    public CustomerService getCustomerService() {
        return customerService;
    }
}
