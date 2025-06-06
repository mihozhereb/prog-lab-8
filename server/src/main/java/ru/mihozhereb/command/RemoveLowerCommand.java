package ru.mihozhereb.command;

import ru.mihozhereb.collection.CollectionManager;
import ru.mihozhereb.collection.model.MusicBand;
import ru.mihozhereb.control.Request;
import ru.mihozhereb.control.Response;
import ru.mihozhereb.control.UserManager;

import java.sql.SQLException;
import java.util.List;

public class RemoveLowerCommand implements Command {
    @Override
    public Response execute(Request r) {
        int userId = UserManager.getInstance().getUserId(r.login(), r.password());

        List<MusicBand> toDelete = CollectionManager.getInstance()
                .getCollection().stream()
                .filter(i -> r.element().compareTo(i) > 0)
                .toList();

        for (MusicBand band : toDelete) {
            try {
                CollectionManager.getInstance().remove(userId, band.getId());
            } catch (SQLException e) {
                return new Response("Error. " + e.getMessage(), null);
            }
        }

        return new Response("Done.", null);
    }

    @Override
    public String getHelp() {
        return "remove_lower {element} | remove from the collection all items smaller than the specified value";
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ENTER;
    }
}
