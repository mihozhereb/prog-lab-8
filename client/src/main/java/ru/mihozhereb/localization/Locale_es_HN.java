package ru.mihozhereb.localization;

import java.util.ListResourceBundle;

public class Locale_es_HN extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                { "addButton",               "Agregar" },
                { "printAscendingButton",    "Imprimir por número de participantes en orden ascendente" },
                { "countLessButton",         "Contar elementos con género inferior al especificado" },
                { "clearButton",             "Limpiar" },
                { "addIfButton",             "Agregar si es el máximo" },
                { "removeGreaterButton",     "Eliminar los mayores" },
                { "removeLoverButton",       "Eliminar los menores" },
                { "filterButton",            "Filtrar por nombre" },
                { "infoButton",              "Información" },
                { "helpButton",              "Ayuda" },
                { "executeScriptButton",     "Ejecutar script" },

                { "actionColumn",            "Acciones" },
                { "currentUserLabel",        "Usuario: " },
                { "appTitle",                "Aplicación de colección MusicBand" },

                { "BandInfo.Info",           "Información" },
                { "BandInfo.Band",           "Banda" },
                { "BandInfo.Frontman",       "Solista" },
                { "BandInfo.Edit",           "Editar" },
                { "BandInfo.Delete",         "Eliminar" },

                { "EditBand.Band",           "Banda" },
                { "EditBand.Frontman",       "Solista" },
                { "EditBand.Cancel",         "Cancelar" },
                { "EditBand.Send",           "Enviar" },
                { "Update band",             "Actualizar banda" },

                { "Alerts.Connection error",                             "Error de conexión" },
                { "Alerts.Try again later...",                           "Inténtalo de nuevo más tarde..." },
                { "Alerts.Successfully",                                 "Exitosamente" },
                { "Alerts.Error",                                        "Error" },
                { "Alerts.Insufficient permissions to delete the object","Permisos insuficientes para eliminar el objeto" },
                { "Alerts.Wrong argument",                               "Argumento incorrecto" },
                { "Alerts.Check the data!",                              "¡Verifica los datos!" },
                { "Alerts.Invalid login or password",                    "Nombre de usuario o contraseña inválidos" },
                { "Alerts.Failed to register a user",                    "No se pudo registrar el usuario" },

                { "selectGenre.Choose a genre", "Elija un género" },
                { "selectGenre.Send",           "Enviar" },
                { "selectGenre.Cancel",         "Cancelar" },

                { "enterName.Enter a name",     "Ingrese un nombre" },
                { "enterName.Send",             "Enviar" },
                { "enterName.Cancel",           "Cancelar" },

                { "selectFile.Select a file",   "Seleccione un archivo" },
                { "selectFile.File path",       "Ruta del archivo" },
                { "selectFile.Review...",       "Examinar…" },
                { "selectFile.Send",            "Enviar" },
                { "selectFile.Cancel",          "Cancelar" },
        };
    }
}