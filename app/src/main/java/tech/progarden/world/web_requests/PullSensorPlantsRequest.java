package tech.progarden.world.web_requests;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tech.progarden.world.R;
import tech.progarden.world.app.AppConfig;
import tech.progarden.world.app.AppController;
import tech.progarden.world.app.RequestQueueSingleton;
import tech.progarden.world.callback_interfaces.WebRequestCallbackInterface;

/**
 * Created by 1 on 2/11/2016.
 */
public class PullSensorPlantsRequest {
    WebRequestCallbackInterface webRequestCallbackInterface;
    private JSONObject[] jsonObjects;
    Context _context;

    public PullSensorPlantsRequest(Context context) {
        this._context = context;
        webRequestCallbackInterface = null;
    }

    public void setCallbackListener(WebRequestCallbackInterface listener) {
        this.webRequestCallbackInterface = listener;
    }

    /**
     * function to pull sensor list form web server
     */
    public void pullPlantList(final String sensorID) {
        // Tag used to cancel the request
        String tag_string_req = "req_pull_sensor_plants";


        String url = AppConfig.URL_SENSOR_PLANTS_GET;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SENSOR_PLANTS_POST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (response != null) {
                    AppConfig.logInfo("pullSensorResp:", "Nije null");
                    AppConfig.logInfo("pullSensorResp:", response);
                } else {
                    AppConfig.logDebug("pullSensorResp", "NULL RESPONSE");
                }

                try {

                    JSONObject jObj = new JSONObject(response);

                    if (!jObj.isNull("kulture")) {
                        JSONArray jsonArray = jObj.getJSONArray("kulture");
                        //create Array of JSON objects

                        jsonObjects = new JSONObject[1];
//                        for (int i = 0; i < jArr.length(); i++) {
//                            jsonObjects[i] = jArr.getJSONObject(i);
//                        }

                        jsonObjects[0] = jObj;

                        // store JsonObject to preferences
                        SharedPreferences pref;
                        SharedPreferences.Editor editor;
                        pref = PreferenceManager.getDefaultSharedPreferences(_context);
                        editor = pref.edit();

                        editor.putString(_context.getString(R.string.KEY_PLANTS), jsonArray.toString());
                        editor.commit();

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
                webRequestCallbackInterface.webRequestError(error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Post params
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", sensorID);
                return params;
            }
        };

        //strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.DEFAULT_TIMEOUT_MS, AppConfig.DEFAULT_MAX_RETRIES, AppConfig.DEFAULT_BACKOFF_MULT));
        // AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        RequestQueue requestQueue = RequestQueueSingleton.getInstance(_context.getApplicationContext()).getRequestQueue();
        strReq.setTag("PullSensorPlantsRequest");
        requestQueue.add(strReq);
    }
}
