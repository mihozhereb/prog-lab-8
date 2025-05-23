package ru.mihozhereb.localization;

import java.util.ListResourceBundle;

public class Locale_it_IT extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                { "addButton",               "Aggiungi" },
                { "printAscendingButton",    "Stampa per numero di partecipanti in ordine crescente" },
                { "countLessButton",         "Conta quelli con genere minore" },
                { "clearButton",             "Pulisci" },
                { "addIfButton",             "Aggiungi se massimo" },
                { "removeGreaterButton",     "Rimuovi quelli maggiori" },
                { "removeLoverButton",       "Rimuovi quelli minori" },
                { "filterButton",            "Filtra per nome" },
                { "infoButton",              "Info" },
                { "helpButton",              "Aiuto" },
                { "executeScriptButton",     "Esegui script" },

                { "actionColumn",            "Azioni" },
                { "currentUserLabel",        "Utente: " },
                { "appTitle",                "Applicazione collezione MusicBand" },

                { "BandInfo.Info",           "Informazioni" },
                { "BandInfo.Band",           "Gruppo" },
                { "BandInfo.Frontman",       "Frontman" },
                { "BandInfo.Edit",           "Modifica" },
                { "BandInfo.Delete",         "Elimina" },

                { "EditBand.Band",           "Gruppo" },
                { "EditBand.Frontman",       "Frontman" },
                { "EditBand.Cancel",         "Annulla" },
                { "EditBand.Send",           "Invia" },
                { "Update band",             "Aggiorna gruppo" },

                { "Alerts.Connection error",                             "Errore di connessione" },
                { "Alerts.Try again later...",                           "Riprova più tardi..." },
                { "Alerts.Successfully",                                 "Operazione riuscita" },
                { "Alerts.Error",                                        "Errore" },
                { "Alerts.Insufficient permissions to delete the object","Permessi insufficienti per eliminare l'oggetto" },
                { "Alerts.Wrong argument",                               "Argomento non valido" },
                { "Alerts.Check the data!",                              "Controlla i dati!" },
                { "Alerts.Invalid login or password",                    "Login o password non validi" },
                { "Alerts.Failed to register a user",                    "Registrazione utente fallita" },

                { "selectGenre.Choose a genre", "Scegli un genere" },
                { "selectGenre.Send",           "Invia" },
                { "selectGenre.Cancel",         "Annulla" },

                { "enterName.Enter a name",     "Inserisci un nome" },
                { "enterName.Send",             "Invia" },
                { "enterName.Cancel",           "Annulla" },

                { "selectFile.Select a file",   "Seleziona un file" },
                { "selectFile.File path",       "Percorso file" },
                { "selectFile.Review...",       "Sfoglia…" },
                { "selectFile.Send",            "Invia" },
                { "selectFile.Cancel",          "Annulla" },
        };
    }
}