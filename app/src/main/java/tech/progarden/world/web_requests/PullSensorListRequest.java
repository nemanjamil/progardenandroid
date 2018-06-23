package tech.progarden.world.web_requests;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;

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
import tech.progarden.world.callback_interfaces.WebRequestCallbackInterface;
import tech.progarden.world.dialogs.ProgressDialogCustom;
import tech.progarden.world.app.RequestQueueSingleton;

/**
 * Created by 1 on 1/29/2016.
 */
public class PullSensorListRequest {
    WebRequestCallbackInterface webRequestCallbackInterface;
    ListView listView;
    private Context context;
    private ProgressDialogCustom progressDialog;
    private JSONObject[] jsonObjects;

    public PullSensorListRequest(Activity context) {
        this.context = context;
        progressDialog = new ProgressDialogCustom(context);
        progressDialog.setCancelable(false);
        listView = (ListView) context.findViewById(R.id.listView);
        webRequestCallbackInterface = null;
    }

    public void setCallbackListener(WebRequestCallbackInterface listener) {
        this.webRequestCallbackInterface = listener;
    }

    /**
     * function to pull sensor list form web server
     */
    public void pullSensorList(final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_pull_sensors";
        progressDialog.showDialog(context.getString(R.string.progress_update_sensor_list));

        String url = String.format(AppConfig.URL_SENSOR_LIST_GET, uid);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SENSOR_LIST_POST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.hideDialog();
                if (response != null) {
                    AppConfig.logInfo("pullSensorResp:", "Nije null");
                    AppConfig.logInfo("pullSensorResp:", response);
                } else {
                    AppConfig.logDebug("pullSensorResp", "NULL RESPONSE");
                }

                try {

                    JSONObject jObj = new JSONObject(response);

                    boolean success = jObj.getBoolean("success");

                    if (success) {
                        //create Array of JSON objects
                        JSONArray jArr = jObj.getJSONArray("podaci");

                        jsonObjects = new JSONObject[jArr.length()];
                        for (int i = 0; i < jArr.length(); i++) {
                            jsonObjects[i] = jArr.getJSONObject(i);
                        }

                        webRequestCallbackInterface.webRequestSuccess(true, jsonObjects);


                    } else {
                        String errorMsg = jObj.getString("error_msg");
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
                progressDialog.hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Post params
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "listaSenzoraPoKomitentu");
                params.put("id", uid);
                return params;
            }
        };

        //strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.DEFAULT_TIMEOUT_MS, AppConfig.DEFAULT_MAX_RETRIES, AppConfig.DEFAULT_BACKOFF_MULT));
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        RequestQueue requestQueue = RequestQueueSingleton.getInstance(context.getApplicationContext()).getRequestQueue();
        strReq.setTag("PullSensorListRequest");
        requestQueue.add(strReq);

    }
}
