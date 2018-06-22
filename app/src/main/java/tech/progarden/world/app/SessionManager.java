package tech.progarden.world.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import tech.progarden.world.R;

public class SessionManager {

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_UID = "userID";

    private static final String KEY_GENERAL_NAME = "KomitentNaziv";
    private static final String KEY_NAME = "KomitentIme";
    private static final String KEY_LAST_NAME = "KomitentPrezime";
    private static final String KEY_ADDRESS = "KomitentAdresa";
    private static final String KEY_ZIP_CODE = "KomitentPosBroj";
    private static final String KEY_CITY = "KomitentMesto";
    private static final String KEY_PHONE = "KomitentTelefon";
    private static final String KEY_MOBILE = "KomitentMobTel";
    private static final String KEY_EMAIL = "KomitentEmail";
    private static final String KEY_USERNAME = "KomitentUserName";
    private static final String KEY_USER_TYPE = "KomitentTipUsera";
    private static final String KEY_FIRM_NAME = "KomitentFirma";
    private static final String KEY_FIRM_ID = "KomitentMatBr";
    private static final String KEY_FIRM_PIB = "KomitentPIB";
    private static final String KEY_FIRM_ADDRESS = "KomitentFirmaAdresa";

    // Shared Preferences
    SharedPreferences pref;
    Editor editor;
    Context _context;

    public SessionManager(Context context) {
        this._context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(_context);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        // commit changes
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public String getUID() {
        return pref.getString(KEY_UID, "");
    }

    public void setUID(String uid) {
        editor.putString(KEY_UID, uid);
        // commit changes
        editor.commit();
    }

    public String getGeneralName() {
        return pref.getString(_context.getString(R.string.KEY_GENERAL_NAME), "");
    }

    public void setGeneralName(String s) {
        editor.putString(_context.getString(R.string.KEY_GENERAL_NAME), s);
        // commit changes
        editor.commit();
    }

    public String getName() {
        return pref.getString(_context.getString(R.string.KEY_NAME), "");
    }

    public void setName(String s) {
        editor.putString(_context.getString(R.string.KEY_NAME), s);
        // commit changes
        editor.commit();
    }

    public String getLastName() {
        return pref.getString(_context.getString(R.string.KEY_LAST_NAME), "");
    }

    public void setLastName(String s) {
        editor.putString(_context.getString(R.string.KEY_LAST_NAME), s);
        // commit changes
        editor.commit();
    }

    public String getAddress() {
        return pref.getString(_context.getString(R.string.KEY_ADDRESS), "");
    }

    public void setAddress(String s) {
        editor.putString(_context.getString(R.string.KEY_ADDRESS), s);
        // commit changes
        editor.commit();
    }

    public String getZip() {
        return pref.getString(_context.getString(R.string.KEY_ZIP_CODE), "");
    }

    public void setZip(String s) {
        editor.putString(_context.getString(R.string.KEY_ZIP_CODE), s);
        // commit changes
        editor.commit();
    }

    public String getCity() {
        return pref.getString(_context.getString(R.string.KEY_CITY), "");
    }

    public void setCity(String s) {
        editor.putString(_context.getString(R.string.KEY_CITY), s);
        // commit changes
        editor.commit();
    }

    public String getPhone() {
        return pref.getString(_context.getString(R.string.KEY_PHONE), "");
    }

    public void setPhone(String s) {
        editor.putString(_context.getString(R.string.KEY_PHONE), s);
        // commit changes
        editor.commit();
    }

    public String getMobile() {
        return pref.getString(_context.getString(R.string.KEY_MOBILE), "");
    }

    public void setMobile(String s) {
        editor.putString(_context.getString(R.string.KEY_MOBILE), s);
        // commit changes
        editor.commit();
    }

    public String getEmail() {
        return pref.getString(_context.getString(R.string.KEY_EMAIL), "");
    }

    public void setEmail(String s) {
        editor.putString(_context.getString(R.string.KEY_EMAIL), s);
        // commit changes
        editor.commit();
    }

    public String getUsername() {
        return pref.getString(_context.getString(R.string.KEY_USERNAME), "");
    }

    public void setUsername(String s) {
        editor.putString(_context.getString(R.string.KEY_USERNAME), s);
        // commit changes
        editor.commit();
    }

    public Integer getUserType() {
        return pref.getInt(_context.getString(R.string.KEY_USER_TYPE), -1);
    }

    public void setUserType(Integer i) {
        editor.putInt(_context.getString(R.string.KEY_USER_TYPE), i);
        // commit changes
        editor.commit();
    }

    public String getFirmName() {
        return pref.getString(_context.getString(R.string.KEY_FIRM_NAME), "");
    }

    public void setFirmName(String s) {
        editor.putString(_context.getString(R.string.KEY_FIRM_NAME), s);
        // commit changes
        editor.commit();
    }

    public String getFirmId() {
        return pref.getString(_context.getString(R.string.KEY_FIRM_ID), "");
    }

    public void setFirmId(String s) {
        editor.putString(_context.getString(R.string.KEY_FIRM_ID), s);
        // commit changes
        editor.commit();
    }

    public String getFirmPIB() {
        return pref.getString(_context.getString(R.string.KEY_FIRM_PIB), "");
    }

    public void setFirmPIB(String s) {
        editor.putString(_context.getString(R.string.KEY_FIRM_PIB), s);
        // commit changes
        editor.commit();
    }

    public String getFirmAddress() {
        return pref.getString(_context.getString(R.string.KEY_FIRM_ADDRESS), "");
    }

    public void setFirmAddress(String s) {
        editor.putString(_context.getString(R.string.KEY_FIRM_ADDRESS), s);
        // commit changes
        editor.commit();
    }

}