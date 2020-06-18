package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;

public class UnknownCommand implements IFrontCommand {

    @Override
    public IFrontCommand getInstance() {
        return null;
    }

    @Override
    public ExecuteResult execute(SessionContext session) {
        return new ExecuteResult();
    }
}
