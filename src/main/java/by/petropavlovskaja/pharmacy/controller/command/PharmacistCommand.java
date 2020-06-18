package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.service.CommonService;
import by.petropavlovskaja.pharmacy.service.PharmacistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class PharmacistCommand implements IFrontCommand {
    private static Logger logger = LoggerFactory.getLogger(PharmacistCommand.class);
    private static PharmacistService pharmacistService = PharmacistService.getInstance();

    private static class PharmacistHolder {
        public static final PharmacistCommand PHARMACIST_COMMAND = new PharmacistCommand();
    }

    @Override
    public IFrontCommand getInstance() {
        return PharmacistHolder.PHARMACIST_COMMAND;
    }

    @Override
    public ExecuteResult execute(SessionContext sc) {
        ExecuteResult executeResult = new ExecuteResult();
        Map<String, Object> reqParameters = sc.getRequestParameters();
        String fullUri = (String) sc.getSession().getAttribute("fullUri");
        String[] arrayUri = fullUri.substring(1).split("/");

        Set<Medicine> medicineList = CommonService.getInstance().getAllMedicine();
        sc.getSession().setAttribute("medicineList", medicineList);

        executeResult.setJsp(arrayUri);

        if (sc.getRequestMethod().equals("POST")) {
            String command = (String) reqParameters.get("medicineCommand");
            switch (command) {
                case "create": {
                    // delete
                    logger.info(" Command create Medicine is received.");
                    boolean result = pharmacistService.addMedicineIntoDB(reqParameters, executeResult);
                    if (result) {
                        executeResult.setJsp(fullUri);
                        logger.info(" Create in DAO was successful");
                    }
                    break;
                }
                case "setChanges": {
                    logger.info(" Command edit Medicine is received.");
                    boolean result = pharmacistService.changeMedicineInDB(reqParameters, executeResult);
                    if (result) {
                        executeResult.setJsp(fullUri);
                    }
                    break;
                }
                case "medicineForEdit": {
                    logger.info(" Command medicineForEdit is received.");
                    Medicine medicine = pharmacistService.findMedicineById(reqParameters, executeResult);
                    if (medicine.getId() != -1) {
                        executeResult.setResponseAttributes("editMedicine", medicine);
                    }
                    break;
                }
                case "medicineForDelete": {
                    logger.info(" Command delete Medicine is received.");
                    boolean result = pharmacistService.deleteMedicineFromDB(reqParameters, executeResult);
                    if (result) {
                        executeResult.setJsp(fullUri);
                    }
                    break;
                }
                default: {
                    logger.error("Command " + command + " is not defined.");
                    executeResult.setResponseAttributes("message", "Command is not defined.");
                }
            }
        }

        return executeResult;
    }


}