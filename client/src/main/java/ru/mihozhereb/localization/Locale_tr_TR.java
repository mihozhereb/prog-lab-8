package ru.mihozhereb.localization;

import java.util.ListResourceBundle;

public class Locale_tr_TR extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                { "addButton",               "Ekle" },
                { "printAscendingButton",    "Katılımcı sayısına göre artan sırada yazdır" },
                { "countLessButton",         "Seçilen türden az olanları say" },
                { "clearButton",             "Temizle" },
                { "addIfButton",             "Maksimumsa ekle" },
                { "removeGreaterButton",     "Daha büyükleri sil" },
                { "removeLoverButton",       "Daha küçükleri sil" },
                { "filterButton",            "İsme göre filtrele" },
                { "infoButton",              "Bilgi" },
                { "helpButton",              "Yardım" },
                { "executeScriptButton",     "Betik çalıştır" },

                { "actionColumn",            "Eylemler" },
                { "currentUserLabel",        "Kullanıcı: " },
                { "appTitle",                "MusicBand koleksiyon uygulaması" },

                { "BandInfo.Info",           "Bilgi" },
                { "BandInfo.Band",           "Grup" },
                { "BandInfo.Frontman",       "Solist" },
                { "BandInfo.Edit",           "Düzenle" },
                { "BandInfo.Delete",         "Sil" },

                { "EditBand.Band",           "Grup" },
                { "EditBand.Frontman",       "Solist" },
                { "EditBand.Cancel",         "İptal" },
                { "EditBand.Send",           "Gönder" },
                { "Update band",             "Grubu güncelle" },

                { "Alerts.Connection error",                            "Bağlantı hatası" },
                { "Alerts.Try again later...",                          "Daha sonra tekrar deneyin..." },
                { "Alerts.Successfully",                                "Başarılı" },
                { "Alerts.Error",                                       "Hata" },
                { "Alerts.Insufficient permissions to delete the object","Nesneyi silme izniniz yok" },
                { "Alerts.Wrong argument",                              "Geçersiz argüman" },
                { "Alerts.Check the data!",                             "Verileri kontrol edin!" },
                { "Alerts.Invalid login or password",                   "Geçersiz kullanıcı adı veya şifre" },
                { "Alerts.Failed to register a user",                   "Kullanıcı kaydı başarısız oldu" },

                { "selectGenre.Choose a genre", "Tür seçin" },
                { "selectGenre.Send",           "Gönder" },
                { "selectGenre.Cancel",         "İptal" },

                { "enterName.Enter a name",     "Bir isim girin" },
                { "enterName.Send",             "Gönder" },
                { "enterName.Cancel",           "İptal" },

                { "selectFile.Select a file",   "Bir dosya seçin" },
                { "selectFile.File path",       "Dosya yolu" },
                { "selectFile.Review...",       "Gözat…" },
                { "selectFile.Send",            "Gönder" },
                { "selectFile.Cancel",          "İptal" },
        };
    }
}