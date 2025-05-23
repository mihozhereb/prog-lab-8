package ru.mihozhereb.command;

import ru.mihozhereb.control.Request;
import ru.mihozhereb.control.Response;
import ru.mihozhereb.control.UserManager;

public class CheckUserCommand implements Command{
    @Override
    public Response execute(Request r) {
        return new Response("Done.", null);
    }

    @Override
    public String getHelp() {
        return "";
    }
}
