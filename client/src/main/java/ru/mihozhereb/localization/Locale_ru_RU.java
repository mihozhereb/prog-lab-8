package ru.mihozhereb.localization;

import java.util.ListResourceBundle;

public class Locale_ru_RU extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                { "addButton",    "Добавить" },
                { "printAscendingButton", "Вывести по возрастанию числа участников" },
                { "countLessButton",      "Подсчитать элементы с жанром меньше заданного" },
                { "clearButton",  "Очистить" },
                { "addIfButton",  "Добавить, если максимум" },
                { "removeGreaterButton",  "Удалить превышающие" },
                { "removeLoverButton",    "Удалить меньшие" },
                { "filterButton", "Фильтрация по имени" },
                { "infoButton",   "Информация" },
                { "helpButton",   "Помощь" },
                { "executeScriptButton", "Выполнить скрипт" },

                { "actionColumn",         "Действия" },
                { "currentUserLabel",     "Пользователь: " },
                { "appTitle",             "Приложение коллекции музыкальных групп" },

                { "BandInfo.Info",        "Информация" },
                { "BandInfo.Band",        "Группа" },
                { "BandInfo.Frontman",    "Фронтмен" },
                { "BandInfo.Edit",        "Редактировать" },
                { "BandInfo.Delete",      "Удалить" },

                { "EditBand.Band",        "Группа" },
                { "EditBand.Frontman",    "Фронтмен" },
                { "EditBand.Cancel",      "Отмена" },
                { "EditBand.Send",        "Отправить" },
                { "Update band",          "Обновить группу" },

                { "Alerts.Connection error",                             "Ошибка соединения" },
                { "Alerts.Try again later...",                           "Попробуйте позже..." },
                { "Alerts.Successfully",                                 "Успешно" },
                { "Alerts.Error",                                        "Ошибка" },
                { "Alerts.Insufficient permissions to delete the object","Недостаточно прав для удаления объекта" },
                { "Alerts.Wrong argument",                               "Неверный аргумент" },
                { "Alerts.Check the data!",                              "Проверьте данные!" },
                { "Alerts.Invalid login or password",                    "Неверный логин или пароль" },
                { "Alerts.Failed to register a user",                    "Не удалось зарегистрировать пользователя" },

                { "selectGenre.Choose a genre", "Выберите жанр" },
                { "selectGenre.Send",           "Отправить" },
                { "selectGenre.Cancel",         "Отмена" },

                { "enterName.Enter a name",     "Введите имя" },
                { "enterName.Send",             "Отправить" },
                { "enterName.Cancel",           "Отмена" },

                { "selectFile.Select a file",   "Выберите файл" },
                { "selectFile.File path",       "Путь к файлу" },
                { "selectFile.Review...",       "Обзор…" },
                { "selectFile.Send",            "Отправить" },
                { "selectFile.Cancel",          "Отмена" },
        };
    }
}