package ru.mihozhereb.command;

import ru.mihozhereb.collection.CollectionManager;
import ru.mihozhereb.collection.model.MusicBand;
import ru.mihozhereb.control.Request;
import ru.mihozhereb.control.Response;
import ru.mihozhereb.control.UserManager;

import java.sql.SQLException;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

public class RemoveGreaterCommand implements Command {
    @Override
    public Response execute(Request r) {
        int userId = UserManager.getInstance().getUserId(r.login(), r.password());

        List<MusicBand> toDelete = CollectionManager.getInstance()
                .getCollection().stream()
                .filter(i -> r.element().compareTo(i) < 0)
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
        return "remove_greater {element} | remove all items from the collection that exceed the specified value";
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ENTER;
    }
}
