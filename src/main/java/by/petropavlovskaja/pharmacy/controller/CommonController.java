package by.petropavlovskaja.pharmacy.controller;

import by.petropavlovskaja.pharmacy.controller.command.IFrontCommand;
import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.db.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Common controller, extends {@link HttpServlet} */
@WebServlet(name = "MainServlet", urlPatterns = "/app")
public final class CommonController extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(CommonController.class);

    /**
     * The override method doGet {@link HttpServlet#doGet(HttpServletRequest, HttpServletResponse)}
     * @param req  - http servlet request
     * @param resp - http servlet response
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        process(req, resp);
    }

    /**
     * The override method doPost {@link HttpServlet#doPost(HttpServletRequest, HttpServletResponse)}
     * @param req  - http servlet request
     * @param resp - http servlet response
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        process(req, resp);
    }

    /**
     * The common method to process all get and post front requests
     * @param req  - http servlet request
     * @param resp - http servlet response
     */
    private void process(HttpServletRequest req, HttpServletResponse resp) {
        IFrontCommand frontCommand = getFrontCommandClass(req.getAttribute("command").toString());
        SessionContext sessionContext = SessionContext.getSessionContextInstance(req);
        ExecuteResult executeResult = frontCommand.execute(sessionContext);
        executeResult.complete(req, resp);
    }

    /**
     * The common method to get the class that implements the interface {@link IFrontCommand}
     * @param uri  - uri
     * @return - class that implements the {@link IFrontCommand}
     */
    private IFrontCommand getFrontCommandClass(String uri) {
        IFrontCommand iFrontCommand = null;
        logger.info("Start looking for command. URI = " + uri);
        try {
            String newUri = uri.substring(0, 1).toUpperCase() + uri.substring(1);
            Package p = IFrontCommand.class.getPackage();
            Class<IFrontCommand> commandClass = (Class<IFrontCommand>) Class.forName(p.getName() + "." + newUri + "Command");
            logger.info("Class " + newUri + " get successful");
            iFrontCommand = (commandClass.asSubclass(IFrontCommand.class).newInstance()).getInstance();


        } catch (ClassNotFoundException e) {
            logger.error("Can't find class for URI = " + uri);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return iFrontCommand;
    }

    /**
     * the override method to destroy controller {@link HttpServlet#destroy()}.
     * Method closes real connections to database
     */
    @Override
    public void destroy() {
        ConnectionPool.ConnectionPool.closeRealConnection();
    }

}
