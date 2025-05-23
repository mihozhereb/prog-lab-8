package ru.mihozhereb.controllers;

import ru.mihozhereb.Main;
import ru.mihozhereb.collection.model.MusicBand;
import ru.mihozhereb.control.Request;
import ru.mihozhereb.control.Response;
import ru.mihozhereb.control.UserData;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;

public class CollectionUpdater implements Runnable {
    private void sleep() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ignored) {}
    }

    private final MainController main;
    private final GraphController graph;

    public CollectionUpdater(MainController main, GraphController graph) {
        this.main = main;
        this.graph = graph;
    }

    @Override
    public void run() {
        Response res;

        while (true) {
            try {
                res = Main.getHandler().getClient().sendRequest(
                        new Request("show", null, null,
                                UserData.getUserLogin(), UserData.getUserPassword())
                );
            } catch (IOException e) {
                sleep();
                continue;
            }

            if (res == null || res.elements() == null) {
                sleep();
                continue;
            }

            for (Iterator<MusicBand> it = main.getTableView().getItems().iterator(); it.hasNext(); ) {
                MusicBand i = it.next();
                if (!res.elements().contains(i)) {
                    // remove object
                    it.remove();
                    graph.eraseObject(i);
                }
            }

            res.elements().sort(Comparator.comparing(MusicBand::getId));

            for (MusicBand i : res.elements()) {
                if (!main.getTableView().getItems().contains(i)) {
                    // add object
                    main.getTableView().getItems().add(i);
                    graph.drawObject(i);
                }
            }

            sleep();
        }
    }
}
