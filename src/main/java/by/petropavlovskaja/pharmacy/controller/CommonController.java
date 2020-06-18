package by.petropavlovskaja.pharmacy.controller;

import by.petropavlovskaja.pharmacy.controller.command.IFrontCommand;
import by.petropavlovskaja.pharmacy.controller.command.UnknownCommand;
import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.db.impl.ConnectionPool;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@WebServlet(name = "MainServlet", urlPatterns = "/app")
public class CommonController extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(CommonController.class);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getSession().isNew()) {
            Set<Medicine> medicineList = CommonService.getInstance().getAllMedicine();
            req.getSession().setAttribute("medicineList", medicineList);
        }
        process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) {
        IFrontCommand frontCommand = getFrontCommandClass(req.getAttribute("command").toString());
        SessionContext sessionContext = SessionContext.getSessionContextInstance(req);
        ExecuteResult executeResult = frontCommand.execute(sessionContext);
        executeResult.complete(req, resp);

    }

    private IFrontCommand getFrontCommandClass(String uri) {
        logger.info("Start looking for command. URI = " + uri);
        try {
            String newUri = uri.substring(0, 1).toUpperCase() + uri.substring(1);
            Package p = IFrontCommand.class.getPackage();
            Class<IFrontCommand> commandClass = (Class<IFrontCommand>) Class.forName(p.getName() + "." + newUri + "Command");
            logger.info("Class " + newUri + " get successful");

            return (commandClass.asSubclass(IFrontCommand.class).newInstance()).getInstance();

        } catch (ClassNotFoundException e) {
            logger.error("Can't find class for URI = " + uri);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return new UnknownCommand();
    }


    @Override
    public void destroy() {
        ConnectionPool.ConnectionPool.closeRealConnection();
    }


}
