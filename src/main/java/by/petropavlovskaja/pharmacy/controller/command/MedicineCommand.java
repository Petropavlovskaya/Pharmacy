package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

public class MedicineCommand implements IFrontCommand {
    private static Logger logger = LoggerFactory.getLogger(MedicineCommand.class);
    private static CommonService service = CommonService.getInstance();


    private static class MedicineHolder {
        public static final MedicineCommand MEDICINE_COMMAND = new MedicineCommand();
    }

    @Override
    public IFrontCommand getInstance() {
        return MedicineHolder.MEDICINE_COMMAND;
    }

    @Override
    public ExecuteResult execute(SessionContext sc) {
        ExecuteResult executeResult = new ExecuteResult();
        Set<Medicine> medicineList = service.getAllMedicine();
        sc.getSession().setAttribute("medicineList", medicineList);

        if (sc.getRequestMethod().equals("GET")) {
            executeResult.setJsp("/WEB-INF/jsp/medicine.jsp");
        }
        return executeResult;
    }
}
