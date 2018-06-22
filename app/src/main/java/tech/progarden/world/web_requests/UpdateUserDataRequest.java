package tech.progarden.world.web_requests;

import android.app.Activity;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tech.progarden.world.R;
import tech.progarden.world.app.AppConfig;
import tech.progarden.world.app.AppController;
import tech.progarden.world.app.SessionManager;
import tech.progarden.world.callback_interfaces.WebRequestCallbackInterface;
import tech.progarden.world.dialogs.ProgressDialogCustom;

/**
 * Created by 1 on 2/12/2016.
 */
public class UpdateUserDataRequest {
    private Context context;
    private ProgressDialogCustom progressDialog;
    private WebRequestCallbackInterface webRequestCallbackInterface;

    private JSONObject[] jsonObjects;
    private SessionManager session;

    public UpdateUserDataRequest(Activity context) {
        this.context = context;
        progressDialog = new ProgressDialogCustom(context);
        progressDialog.setCancelable(false);
        webRequestCallbackInterface = null;
        session = new SessionManager(context.getApplicationContext());
    }

    public void setCallbackListener(WebRequestCallbackInterface listener) {
        this.webRequestCallbackInterface = listener;
    }

    public void updateUserData() {
        String tag_string_req = "req_add_sensor";
        progressDialog.showDialog(context.getString(R.string.progress_update_user_data));

        final String p1 = session.getUID();
        final String p2 = session.getGeneralName();
        final String p3 = session.getName();
        final String p4 = session.getLastName();
        final String p5 = session.getAddress();
        final String p6 = session.getZip();
        final String p7 = session.getCity();
        final String p8 = session.getPhone();
        final String p9 = session.getMobile();
        final String p10 = session.getEmail();
        final String p11 = session.getUsername();
        final String p12 = String.valueOf(session.getUserType());
        final String p13 = session.getFirmName();
        final String p14 = session.getFirmId();
        final String p15 = session.getFirmPIB();
        final String p16 = session.getFirmAddress();

        String url = String.format(AppConfig.URL_UPDATE_USER_DATA_GET, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.hideDialog();
                if (response != null) {
                    AppConfig.logDebug("RESPONSE", "Nije null");
                    AppConfig.logDebug("RESPONSE", response);
                } else {
                    AppConfig.logDebug("RESPONSE", "NULL RESPONSE");
                }

                try {

                    JSONObject jObj = new JSONObject(response);

                    boolean success = jObj.getBoolean("success");

                    if (success) {
                        webRequestCallbackInterface.webRequestSuccess(true, jsonObjects);
                    } else {
                        webRequestCallbackInterface.webRequestSuccess(false, jsonObjects);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                webRequestCallbackInterface.webRequestError(error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Post params
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", p1);
                params.put("KomitentNaziv", p2);
                params.put("KomitentIme", p3);
                params.put("KomitentPrezime", p4);
                params.put("KomitentAdresa", p5);
                params.put("KomitentPosBroj", p6);
                params.put("KomitentMesto", p7);
                params.put("KomitentTelefon", p8);
                params.put("KomitentMobTel", p9);
                params.put("email", p10);
                params.put("KomitentUserName", p11);
                params.put("KomitentTipUsera", p12);
                params.put("KomitentFirma", p13);
                params.put("KomitentMatBr", p14);
                params.put("KomitentPIB", p15);
                params.put("KomitentFirmaAdresa", p16);
                return params;
            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.DEFAULT_TIMEOUT_MS, AppConfig.DEFAULT_MAX_RETRIES, AppConfig.DEFAULT_BACKOFF_MULT));
        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
}