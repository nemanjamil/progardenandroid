package tech.progarden.world.app;

import android.util.Log;

/**
 * Created by 1 on 1/15/2016.
 */
public class AppConfig {
    public static final String URL_BASE = "http://masinealati.rs/";

    public static final String URL_CONFIG_SENSOR = "http://192.168.4.1/wifisave?s=%1$s&p=%2$s";

    public static final String URL_CONFIG_SENSOR_BASE = "http://192.168.4.1/";
    public static final String URL_CONFIG_SENSOR_BASE_KONEKCIJA = URL_CONFIG_SENSOR_BASE+"status";
    // Server login url

    public static final String URL_GETINFOPARAMETER_POST = URL_BASE + "parametrigarden.php?action=getsensordetailactivity";
    public static final String URL_CHANGEPUMPSTATUS_POST = URL_BASE + "parametrigarden.php?action=changepumpstatus";
    public static final String URL_GETPUMPSTATUS_POST = URL_BASE + "parametrigarden.php?action=getpumpstatus";

    //"action=povuciPodatkeAndroidKorisnik&email=pera@gjsd.com&p=miki";
    public static final String URL_LOGIN_POST = URL_BASE + "parametri.php?action=povuciPodatkeAndroidKorisnik";
    public static final String URL_LOGIN_GET = URL_LOGIN_POST + "&tag=%1$s&email=%2$s&p=%3$s";
    // Server register url
    public static final String URL_REGISTER_POST = URL_BASE + "parametri.php?action=registrujAndroid";
    //action=registrujAndroid&email=pera@gjsd.com&sifra=miki&komitentime=miki&komitentprezime=miki";
    public static final String URL_REGISTER_GET = URL_REGISTER_POST + "&tag=%1$s&email=%2$s&sifra=%3$s&komitentime=%4$s&komitentprezime=%5$s";
    //http://direktnoizbaste.rs/parametri.php?action=povuciSenzorUid&id=1
    public static final String URL_SENSOR_LIST_POST = URL_BASE + "parametri.php?action=listaSenzoraPoKomitentu";
    public static final String URL_SENSOR_LIST_GET = URL_SENSOR_LIST_POST + "&id=%1$s";
    //http://direktnoizbaste.rs/parametri.php?action=povuciPodatkeSenzorId&string=5CCF7F747A7&id=1
    public static final String URL_GRAPHS_DATA_POST = URL_BASE + "parametri.php?action=povuciPodatkeSenzorId";
    public static final String URL_GRAPHS_DATA_GET = URL_GRAPHS_DATA_POST + "&id=%1$s&string=%2$s&br=%3$s";
    //http://direktnoizbaste.rs/parametri.php?action=obrisiSenzorId&string=pera&id=1
    public static final String URL_DEL_SENSOR_POST = URL_BASE + "parametri.php?action=obrisiSenzorId";
    public static final String URL_DEL_SENSOR_GET = URL_DEL_SENSOR_POST + "&id=%1$s&br=%2$s";
    //http://direktnoizbaste.rs/parametri.php?action=dodajSenzorId&string=pera&id=1&br=4
    public static final String URL_ADD_SENSOR_POST = URL_BASE + "parametri.php?action=dodajSenzorId";
    public static final String URL_ADD_SENSOR_GET = URL_ADD_SENSOR_POST + "&id=%1$s&string=%2$s&br=%3$s";
    //{"kulture":[{"IdKulture":1,"ImeKulture":"Boranija","SlikaKulture":null},{"IdKulture":2,"ImeKulture":"Paprika","SlikaKulture":null},{"IdKulture":3,"ImeKulture":"Paradajz","SlikaKulture":null}]}
    public static final String URL_SENSOR_PLANTS_POST = URL_BASE + "parametri.php?action=podaciKulture";
    public static final String URL_SENSOR_PLANTS_GET = URL_SENSOR_PLANTS_POST;
    //http://direktnoizbaste.rs/parametri.php?action=izmeniPodatkeSenzorId&id=57&string=5ECF7F0747A7&br=3
    public static final String URL_UPDATE_SENSOR_POST = URL_BASE + "parametri.php?action=izmeniPodatkeSenzorId";
    public static final String URL_UPDATE_SENSOR_GET = URL_UPDATE_SENSOR_POST + "&id=%1$s&string=%2$s&br=%3$s";
    //direktnoizbaste.rs/parametri.php?action=izmeniPodatkeKomitent&id=57&KomitentNaziv=NazivKomsd&KomitentIme=Xman
    // &KomitentPrezime=Xavier&KomitentAdresa=Adresa&KomitentPosBroj=11000&KomitentMesto=Beograd&KomitentTelefon=1234tel
    // &KomitentMobTel=1234mobTel&email=x@y.z&KomitentUserName=x&KomitentTipUsera=1&KomitentFirma=Firma
    // &KomitentMatBr=1234MatBr&KomitentPIB=1234&KomitentFirmaAdresa=FrimaAdresa
    public static final String URL_UPDATE_USER_DATA_POST = URL_BASE + "parametri.php?action=izmeniPodatkeKomitent";
    public static final String URL_UPDATE_USER_DATA_GET = URL_UPDATE_USER_DATA_POST + "&id=%1$s&KomitentNaziv=%2$s&KomitentIme=%3$s" +
            "&KomitentPrezime=%4$s&KomitentAdresa=%5$s&KomitentPosBroj=%6$s&KomitentMesto=%7$s&KomitentTelefon=%8$s" +
            "&KomitentMobTel=%9$s&email=%10$s&KomitentUserName=%11$s&KomitentTipUsera=%12$s&KomitentFirma=%13$s" +
            "&KomitentMatBr=%14$s&KomitentPIB=%15$s&KomitentFirmaAdresa=%16$s";

    public static final int ACTIVITY_REQ_SETTINGS = 99;
    public static final int ACTIVITY_RESP_SETTINGS_UPDATE = 98;

    /**
     * The default socket timeout in milliseconds
     */
    public static final int DEFAULT_TIMEOUT_MS = 3000;

    /**
     * The default number of retries
     */
    public static final int DEFAULT_MAX_RETRIES = 4;

    /**
     * The default backoff multiplier
     */
    public static final float DEFAULT_BACKOFF_MULT = 1f;

    public static boolean debug = true;

    public static void logDebug(String tag, String msg) {
        if (debug)
            Log.d(tag, msg);
    }

    public static void logInfo(String tag, String msg) {
        if (debug)
            Log.i(tag, msg);
    }
}
