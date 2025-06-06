package ru.mihozhereb.command;

import ru.mihozhereb.collection.CollectionManager;
import ru.mihozhereb.collection.model.MusicBand;
import ru.mihozhereb.control.Request;
import ru.mihozhereb.control.Response;
import ru.mihozhereb.control.UserManager;

import java.sql.SQLException;
import java.util.Objects;

public class UpdateCommand implements Command {
    @Override
    public Response execute(Request r) {
        int id;
        try {
            id = Integer.parseInt(r.argument());
        } catch (NumberFormatException e) {
            return new Response("Error. Invalid id.", null);
        }

        r.element().setId(id);
        int userId = UserManager.getInstance().getUserId(r.login(), r.password());

        int result;
        try {
            result = CollectionManager.getInstance().update(r.element(), userId);
        } catch (SQLException e) {
            return new Response("Error. " + e.getMessage(), null);
        }

        if (result != 0) {
            return new Response("Done.", null);
        } else {
            return new Response("Error.", null);
        }
    }

    @Override
    public String getHelp() {
        return "update id {element} | update element where id = id";
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ENTER;
    }
}
