package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;

import java.sql.SQLException;

public interface IFrontCommand {
     ExecuteResult execute(SessionContext sc);
     IFrontCommand getInstance();

}
