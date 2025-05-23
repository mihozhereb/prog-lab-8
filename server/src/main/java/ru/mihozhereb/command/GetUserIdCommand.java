package ru.mihozhereb.command;

import ru.mihozhereb.control.Request;
import ru.mihozhereb.control.Response;
import ru.mihozhereb.control.UserManager;

public class GetUserIdCommand implements Command{
    @Override
    public Response execute(Request r) {
        return new Response(String.valueOf(UserManager.getInstance().getUserId(r.login(), r.password())), null);
    }

    @Override
    public String getHelp() {
        return "";
    }
}
