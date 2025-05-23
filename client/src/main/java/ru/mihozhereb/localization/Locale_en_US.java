package ru.mihozhereb.localization;

import java.util.ListResourceBundle;

public class Locale_en_US extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                {"addButton", "add"},
                {"printAscendingButton", "print field ascending number of participants"},
                {"countLessButton", "count less than genre"},
                {"clearButton", "clear"},
                {"addIfButton", "add if max"},
                {"removeGreaterButton", "remove greater"},
                {"removeLoverButton", "remove lower"},
                {"filterButton", "filter contains name"},
                {"infoButton", "info"},
                {"helpButton", "help"},
                {"executeScriptButton", "execute script"},
                {"actionColumn", "Actions"},
                {"currentUserLabel", "User: "},
                {"appTitle", "MusicBand collection app"},
                {"BandInfo.Info", "Info"},
                {"BandInfo.Band", "Band"},
                {"BandInfo.Frontman", "Frontman"},
                {"BandInfo.Edit", "Edit"},
                {"BandInfo.Delete", "Delete"},
                {"EditBand.Band", "Band"},
                {"EditBand.Frontman", "Frontman"},
                {"EditBand.Cancel", "Cancel"},
                {"EditBand.Send", "Send"},
                {"Update band", "Update band"},
                {"Alerts.Connection error", "Connection error"},
                {"Alerts.Try again later...", "Try again later..."},
                {"Alerts.Successfully", "Successfully"},
                {"Alerts.Error", "Error"},
                {"Alerts.Insufficient permissions to delete the object", "Insufficient permissions to delete the object"},
                {"Alerts.Wrong argument", "Wrong argument"},
                {"Alerts.Check the data!", "Check the data!"},
                {"Alerts.Invalid login or password", "Invalid login or password"},
                {"Alerts.Failed to register a user", "Failed to register a user"},
                {"selectGenre.Choose a genre", "Choose a genre"},
                {"selectGenre.Send", "Send"},
                {"selectGenre.Cancel", "Cancel"},
                {"enterName.Enter a name", "Enter a name"},
                {"enterName.Send", "Send"},
                {"enterName.Cancel", "Cancel"},
                {"selectFile.Select a file", "Select a file"},
                {"selectFile.File path", "File path"},
                {"selectFile.Review...", "Review..."},
                {"selectFile.Send", "Send"},
                {"selectFile.Cancel", "Cancel"}
        };
    }
}
