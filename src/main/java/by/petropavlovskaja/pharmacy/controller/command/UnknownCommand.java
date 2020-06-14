package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;

import java.util.Map;

public class UnknownCommand implements IFrontCommand {

    @Override
    public IFrontCommand getInstance() {
        return null;
    }

    @Override
    public ExecuteResult execute(SessionContext session) {
        Map<String, Object> map = null;
        return new ExecuteResult();
    }
}
