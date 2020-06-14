package by.petropavlovskaja.pharmacy.controller;

import by.petropavlovskaja.pharmacy.controller.command.IFrontCommand;
import by.petropavlovskaja.pharmacy.controller.command.UnknownCommand;
import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.db.impl.ConnectionPool;
import by.petropavlovskaja.pharmacy.filter.UrlFilter;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet(name = "MainServlet", urlPatterns = "/app")
public class CommonController extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Override
    public void init() throws ServletException {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession().isNew()){
            Set<Medicine> medicineList = CommonService.getInstance().getAllMedicine();
            req.getSession().setAttribute("medicineList", medicineList);
        }
        process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) {
        IFrontCommand frontCommand = getFrontCommandClass(req.getAttribute("command").toString());
        SessionContext sessionContext = SessionContext.getSessionContextInstance(req, resp);
        ExecuteResult executeResult = frontCommand.execute(sessionContext);
        executeResult.complete(req, resp);

    }

    private IFrontCommand getFrontCommandClass(String uri) {
        System.out.println("CommController2 - getFrontCommandClass, uri = " + uri);
        try {
            String newUri = uri.substring(0, 1).toUpperCase() + uri.substring(1);
            Package p = IFrontCommand.class.getPackage();
            Class<IFrontCommand> commandClass = (Class<IFrontCommand>) Class.forName(p.getName() + "." + newUri+"Command");
            System.out.println("Class " + newUri + " get successful");

            return ((IFrontCommand) commandClass.asSubclass(IFrontCommand.class).newInstance()).getInstance();

        } catch (ClassNotFoundException e) {
            logger.error("Can't find class " + uri);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return new UnknownCommand();
    }


    @Override
    public void destroy() {
        ConnectionPool.ConnectionPool.closeRealConnection();
    }


}
