package tech.progarden.world.wifi;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tech.progarden.world.Step2Connection;
import tech.progarden.world.app.AppConfig;
import tech.progarden.world.app.RequestQueueSingleton;
import tech.progarden.world.dialogs.ProgressDialogCustom;

public class AddUsertoServer {

    private Context mContext;

    private ProgressDialogCustom progressDialog;
    private JSONObject[] jsonObjects;

    public AddUsertoServer(Step2Connection step2Connection) {
    }

    /**
     * function to pull sensor list form web server
     */
    public void addSensor(final Step2Connection.VolleyCallback volleyCallback, final String userId, final String bssid_current, final String ssid_current, final String kind) {
        // Tag used to cancel the request
        String tag_string_req = "req_add_sensor";
        final boolean vratiodg = false;


        //progressDialog.showDialog(context.getString(R.string.progress_add_sensor_list));

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADD_SENSOR_POST_GARDEN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                //progressDialog.hideDialog();
                if (response != null) {
                    AppConfig.logDebug("RESPONSE", "Nije null");
                    AppConfig.logDebug("RESPONSE", response);
                } else {
                    AppConfig.logDebug("RESPONSE", "NULL RESPONSE");
                }

                try {

                    JSONObject jObj = new JSONObject(response);

                    boolean success = jObj.getBoolean("success");
                    jsonObjects = new JSONObject[1];
                    jsonObjects[0] = jObj;

                    if (success) {
                        volleyCallback.onSuccess("ok");
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Log.d("testmiki err", errorMsg);
                        volleyCallback.onError(errorMsg);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("testmiki ORR", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "dodajSenzorId");
                params.put("id", userId);
                params.put("ssid_current", ssid_current);
                params.put("bssid_current", bssid_current);
                params.put("br", kind);
                return params;
            }

        };

        //strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.DEFAULT_TIMEOUT_MS, AppConfig.DEFAULT_MAX_RETRIES, AppConfig.DEFAULT_BACKOFF_MULT));
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        RequestQueue requestQueue = RequestQueueSingleton.getInstance(this.mContext).getRequestQueue();
        strReq.setTag("AddSensorRequest");
        requestQueue.add(strReq);

    }
}
