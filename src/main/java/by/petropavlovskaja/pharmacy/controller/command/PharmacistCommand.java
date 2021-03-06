package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.AttributeConstant;
import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.service.CommonService;
import by.petropavlovskaja.pharmacy.service.PharmacistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Class for processing the pharmacist front requests commands, implements {@link IFrontCommand}
 */
public final class PharmacistCommand implements IFrontCommand {
    private static Logger logger = LoggerFactory.getLogger(PharmacistCommand.class);
    private static PharmacistService pharmacistService = PharmacistService.getInstance();
    private static CommonService commonService = CommonService.getInstance();
    private static MedicineDAO medicineDAO = MedicineDAO.getInstance();

    /**
     * Nested class create instance of the class
     */
    private static class PharmacistHolder {
        private static final PharmacistCommand PHARMACIST_COMMAND = new PharmacistCommand();
    }

    /**
     * The override method for get instance of the class
     *
     * @return - class instance
     */
    @Override
    public IFrontCommand getInstance() {
        return PharmacistHolder.PHARMACIST_COMMAND;
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

        commonService.checkSuccessMessageSet(sc);

        if (sc.getRequestMethod().equals("GET")) {
            String minDate = commonService.getStringDate(2);
            sc.getSession().setAttribute(AttributeConstant.MIN_DATE, minDate);
        }
        executeResult.setJsp(arrayUri);

        if (sc.getRequestMethod().equals("POST")) {
            String command = (String) reqParameters.get("frontCommand");
            switch (command) {
                case "create": {
                    // delete
                    logger.info(" Command create Medicine is received.");
                    pharmacistService.addMedicineIntoDB(reqParameters, executeResult, sc, fullUri);
                    break;
                }
                case "setChanges": {
                    logger.info(" Command edit Medicine is received.");
                    int currentPage = 1;
                    int recordsPerPage = 5;
                    if (sc.getSession().getAttribute(AttributeConstant.CURRENT_PAGE) != null
                            && sc.getSession().getAttribute(AttributeConstant.RECORDS_PER_PAGE) != null) {
                        currentPage = Integer.parseInt(String.valueOf(sc.getSession().getAttribute(AttributeConstant.REQUEST_PAGE)));
                        recordsPerPage = Integer.parseInt(String.valueOf(sc.getSession().getAttribute(AttributeConstant.RECORDS_PER_PAGE)));
                    }
                    boolean result = pharmacistService.changeMedicineInDB(reqParameters, executeResult);
                    if (result) {
                        sc.getSession().setAttribute(AttributeConstant.SUCCESS_MSG, "The changes have saved successfully.");
                        sc.getSession().setAttribute(AttributeConstant.SUCCESS_MSG_CHECK, "yes");
                        medicineDAO.findMedicine(currentPage, recordsPerPage);
                        executeResult.setJsp(fullUri);
                    }
                    break;
                }
                case "medicineForEdit": {
                    logger.info(" Command medicineForEdit is received.");
                    Medicine medicine = pharmacistService.findMedicineById(reqParameters, executeResult);
                    if (medicine.getId() != -1) {
                        executeResult.setResponseAttributes(AttributeConstant.EDIT_MEDICINE, medicine);
                    }
                    break;
                }
                case "cancelChanges": {
                    logger.info(" Command medicineCancel is received.");
                    executeResult.setJsp(fullUri);
                    break;
                }
                case "medicineForDelete": {
                    logger.info("Command delete Medicine is received.");
                    boolean result = pharmacistService.deleteMedicineFromDB(reqParameters, executeResult);
                    if (result) {
                        sc.getSession().setAttribute(AttributeConstant.SUCCESS_MSG, "The medicine has deleted successfully.");
                        sc.getSession().setAttribute(AttributeConstant.SUCCESS_MSG_CHECK, "yes");
                        executeResult.setJsp(fullUri);
                    }
                    break;
                }
                default: {
                    String loggerMessage = "Command " + command + " is not defined.";
                    logger.error(loggerMessage);
                    executeResult.setJsp("/WEB-INF/jsp/404.jsp");
                    executeResult.setResponseAttributes(AttributeConstant.ERROR_MSG, "Command is not defined.");
                }
            }
        }
        return executeResult;
    }
}