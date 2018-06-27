package tech.progarden.world;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import tech.progarden.world.app.AppConfig;
import tech.progarden.world.app.RequestQueueSingleton;
import tech.progarden.world.app.SessionManager;
import tech.progarden.world.dialogs.ProgressDialogCustom;
import tech.progarden.world.wifi.AddUsertoServer;

public class Step2Connection extends AppCompatActivity {

    Button button2, button2garden, button4garden;
    TextView ipstatus2, ssidstatus2;
    SessionManager session;
    ProgressDialogCustom pDialog;
    WifiManager wifiManager;
    String userId;
    AddUsertoServer auts;

    String ssid_current;
    String bssid_current;
    int id_current_wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2_connection);

        pDialog = new ProgressDialogCustom(Step2Connection.this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        button2 = (Button) findViewById(R.id.buttonCheckStatus2);
        button2garden = (Button) findViewById(R.id.button2garden);
        button4garden = (Button) findViewById(R.id.button4garden);
        ipstatus2 = (TextView) findViewById(R.id.ipstatus2);
        ssidstatus2 = (TextView) findViewById(R.id.ssidstatus2);


        session = new SessionManager(getApplicationContext());
        userId = session.getUID();

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                validateStatus2();
            }
        });
        button2garden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotostep3();
            }
        });
        button4garden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step4(ssid_current, bssid_current);
            }
        });

        setUpToolBar();
        auts = new AddUsertoServer(Step2Connection.this);


    }

    private void gotostep3() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        ssid_current = wifiInfo.getSSID().replace("\"", "").toUpperCase();
        bssid_current = wifiInfo.getBSSID().replace(":", "").toUpperCase();
        id_current_wifi = wifiManager.getConnectionInfo().getNetworkId();

        Log.d("testmiki ssid_current ", ssid_current);
        Log.d("testmiki bssid_curr : ", bssid_current);
        Log.d("testmiki id_wifi : ", String.valueOf(id_current_wifi));

        Toast.makeText(getApplicationContext(), "Now - We will disconnect from Garden-WiFi and Connect to Home-Wifi", Toast.LENGTH_LONG).show();
        wifiManager.disconnect();

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    //check if connected!
                    while (!isConnected(Step2Connection.this)) {
                        //Wait to connect
                        Thread.sleep(1000);
                    }

                    Toast.makeText(getApplicationContext(), "OK, go to Step 4", Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                }
            }
        };
        t.start();

        button4garden.setVisibility(View.VISIBLE);

    }

    private void step4(final String ssid_current, final String bssid_current) {

        auts.addSensor(new VolleyCallback() {
            @Override
            public void onSuccess(String resultmoj) {
                Log.d("testmiki Success od ",resultmoj);
                Intent i = new Intent(Step2Connection.this, DrawerActivity.class);
                startActivity(i);
            }

            @Override
            public void onError(VolleyCallback error) {
                Log.d("testmiki VolleyCall", String.valueOf(error));
                pDialog.showDialog(String.valueOf(error));
            }

            @Override
            public void onError(String error) {
                Log.d("testmiki E String ", String.valueOf(error));
                pDialog.showDialog(String.valueOf(error));
            }

        }, userId, bssid_current, ssid_current, "1");

    }


    public interface VolleyCallback {
        void onSuccess(String result);

        void onError(Step2Connection.VolleyCallback error);

        void onError(String error);
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }

    private void setUpToolBar() {

        // Setting up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarmain);
        myToolbar.setTitle(R.string.title_steptwo);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void validateStatus2() {

        String tag_string_req = "validateStatus";
        String url = "http://192.168.4.1/validateConn";
        pDialog.showDialog("Validating status...");
        Log.d("testmiki url ", url);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("testmiki validate ", response);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {

                        String ipstatusstring = jsonObject.getString("ip");
                        String ssidstatusstring = jsonObject.getString("ssid");

                        ipstatus2.setText("IP address : " + ipstatusstring);
                        ipstatus2.setVisibility(View.VISIBLE);

                        ssidstatus2.setText("Garden is connected to : " + ssidstatusstring);
                        ssidstatus2.setVisibility(View.VISIBLE);

                        button2garden.setVisibility(View.VISIBLE);
                        pDialog.hideDialog();

                    } else {
                        pDialog.hideDialog();
                        pDialog.showDialog("Status is not OK. Please Try Again. Click on Button");
                        Log.d("testmiki Status  ", "Status not ok");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("testmiki errval  ", String.valueOf(error));
            }
        });

        //strReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        RequestQueue requestQueue = RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue();
        strReq.setTag("Step2Connection");
        requestQueue.add(strReq);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(Step2Connection.this, SearchNetworks.class);
                startActivity(intent);
                finish();
        }
        return true;
    }
}
