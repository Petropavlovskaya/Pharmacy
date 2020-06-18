package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;

public interface IFrontCommand {
     ExecuteResult execute(SessionContext sc);
     IFrontCommand getInstance();
}
