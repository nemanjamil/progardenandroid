package tech.progarden.world.web_requests;

import android.app.Activity;
import android.content.Context;
import android.webkit.WebView;

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
import tech.progarden.world.app.WebAppInterface;
import tech.progarden.world.callback_interfaces.WebRequestCallbackInterface;
import tech.progarden.world.dialogs.ProgressDialogCustom;

/**
 * Created by 1 on 1/29/2016.
 */
public class PullGraphDataRequest {

    private WebView browser;
    private WebAppInterface webAppInterface;
    private WebRequestCallbackInterface webRequestCallbackInterface;
    private Context context;
    private ProgressDialogCustom progressDialog;
    private JSONObject[] jsonObjects;

    public PullGraphDataRequest(Activity context) {
        this.context = context;
        progressDialog = new ProgressDialogCustom(context);
        progressDialog.setCancelable(false);
        browser = (WebView) context.findViewById(R.id.webView);
        webAppInterface = new WebAppInterface(context);
        webRequestCallbackInterface = null;
    }

    public void setCallbackListener(WebRequestCallbackInterface listener) {
        this.webRequestCallbackInterface = listener;
    }

    /**
     * function to pull data for showing graphs form web server
     */
    public void pullGraphData(final String uid, final String mac, final String br) {
        // Tag used to cancel the request
        String tag_string_req = "req_pull_graphs";
        progressDialog.showDialog("Učitavam grafike...");

        String url = String.format(AppConfig.URL_GRAPHS_DATA_GET, uid, mac, br);

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
                    jsonObjects = new JSONObject[1];
                    jsonObjects[0] = jObj;

                    if (success) {
                        webAppInterface.setJsonObject(jObj);
                        browser.addJavascriptInterface(webAppInterface, "Android");
                        browser.loadUrl("file:///android_asset/www/graphs.htm");

                        webRequestCallbackInterface.webRequestSuccess(true, jsonObjects);
                    } else {
                        // login error
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
                params.put("action", "povuciPodatkeSenzorId");
                params.put("id", uid);
                params.put("string", mac);
                params.put("br", br); //TODO id kulture
                return params;
            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.DEFAULT_TIMEOUT_MS, AppConfig.DEFAULT_MAX_RETRIES, AppConfig.DEFAULT_BACKOFF_MULT));
        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
