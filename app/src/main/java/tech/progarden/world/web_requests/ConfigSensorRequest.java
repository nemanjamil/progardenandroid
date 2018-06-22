package tech.progarden.world.web_requests;

import android.app.Activity;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import tech.progarden.world.R;
import tech.progarden.world.app.AppConfig;
import tech.progarden.world.app.AppController;
import tech.progarden.world.callback_interfaces.WebRequestCallbackInterface;
import tech.progarden.world.dialogs.ProgressDialogCustom;

/**
 * Created by milan on 2/6/2016.
 */
public class ConfigSensorRequest {
    private Context context;
    private ProgressDialogCustom progressDialog;
    private WebRequestCallbackInterface webRequestCallbackInterface;
    private JSONObject[] jsonObjects;

    public ConfigSensorRequest(Activity context) {
        this.context = context;
        progressDialog = new ProgressDialogCustom(context);
        progressDialog.setCancelable(false);
        webRequestCallbackInterface = null;
    }

    public void setCallbackListener(WebRequestCallbackInterface listener) {
        this.webRequestCallbackInterface = listener;
    }

    /**
     * function to pull sensor list form web server
     */
    public void configSensor(final String ssid, final String pass) {
        // Tag used to cancel the request
        String tag_string_req = "req_config_sensor";
        progressDialog.showDialog(context.getString(R.string.sensor_config_msg));

        String url = String.format(AppConfig.URL_CONFIG_SENSOR, ssid, pass);

        StringRequest strReq  = new StringRequest(Request.Method.GET,
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
                boolean success = true;

                if (success) {

                    webRequestCallbackInterface.webRequestSuccess(true, jsonObjects);

                } else {
                    webRequestCallbackInterface.webRequestSuccess(false, jsonObjects);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                String msg = error.getMessage();
                if ((msg == null) || msg.equals("")) {
                    msg = "Nije uspelo podešavanje senzora.\nResetujte senzor.";
                }
                /*TODO make a custom request to handle 200 code*/
                if (msg.contains("timed")) {
                    webRequestCallbackInterface.webRequestSuccess(true, jsonObjects);
                } else
                    webRequestCallbackInterface.webRequestError(msg);
            }
        }) {
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.DEFAULT_TIMEOUT_MS, AppConfig.DEFAULT_MAX_RETRIES, AppConfig.DEFAULT_BACKOFF_MULT));
        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
