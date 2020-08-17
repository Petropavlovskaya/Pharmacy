package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;

/**
 * Command Interface for processing the front requests
 * {@link CustomerCommand}, {@link DoctorCommand}, {@link PharmacistCommand},
 * {@link LoginCommand}, {@link SignupCommand}, {@link LogoutCommand}
 */
public interface IFrontCommand {

    /**
     * The method process GET and POST front requests
     *
     * @param sc - Session context {@link SessionContext}
     * @return - class instance {@link ExecuteResult}
     */
    ExecuteResult execute(SessionContext sc);

    /**
     * The method for get instance of classes which implements the interface
     *
     * @return - class instance
     */
    IFrontCommand getInstance();
}
