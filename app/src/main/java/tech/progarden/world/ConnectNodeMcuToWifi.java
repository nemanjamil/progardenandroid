package tech.progarden.world;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import tech.progarden.world.app.AppController;
import tech.progarden.world.app.RequestQueueSingleton;
import tech.progarden.world.app.SessionManager;
import tech.progarden.world.dialogs.ProgressDialogCustom;

public class ConnectNodeMcuToWifi extends AppCompatActivity {

    String ssid, networkPass, ssid_current;
    WifiManager mainWifiObj;
    SessionManager session;
    ProgressDialogCustom pDialog;
    Button button;
    TextView ipstatus,ssidstatus;

    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_node_mcu_to_wifi);

        button = (Button) findViewById(R.id.buttonCheckStatus);
        ipstatus = (TextView) findViewById(R.id.ipstatus);
        ssidstatus = (TextView) findViewById(R.id.ssidstatus);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
               validateStatus();

            }
        });

        setUpToolBar();
        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialogCustom(ConnectNodeMcuToWifi.this);
        pDialog.showDialog("Waiting for connection to Sensor");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ssid = extras.getString("ssid");
            networkPass = extras.getString("networkPass");
            ssid_current = extras.getString("ssid_current");

        }

        mainWifiObj = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!mainWifiObj.isWifiEnabled()) {
                        Thread.sleep(2000);
                        Log.d("testmiki isWifiEnabled", String.valueOf(mainWifiObj.isWifiEnabled()));
                    }
                    while (validateIpAddress()) {
                        Thread.sleep(1000);
                    }
                    while (validateSsid()) {
                        Thread.sleep(1000);
                    }

                    Thread.sleep(3000);
                    Log.d("testmiki", ssid+" "+networkPass+" "+ssid_current);
                    pDialog.hideDialog();
                    sentInfoToNodeMcu(ssid_current, networkPass, session.getUID(),count);



                } catch (Exception e) {
                }
            }
        };
        t.start();



    }

    private void validateStatus() {

            String tag_string_req = "validateStatus";
            pDialog.showDialog("Validating status...");
            String url = "http://192.168.4.1/validateConn";

            StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d("testmiki validate",response);
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if (success) {

                            String ipstatusstring = jsonObject.getString("ip");
                            String ssidstatusstring = jsonObject.getString("ssid");

                            ipstatus.setText(ipstatusstring);
                            ipstatus.setVisibility(View.VISIBLE);

                            ssidstatus.setText("NodeMcy is connected to : "+ssidstatusstring);
                            ssidstatus.setVisibility(View.VISIBLE);

                            pDialog.hideDialog();

                        } else {
                            pDialog.showDialog("Status is not OK. Please Try Again. Click on Button");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            //strReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));
            //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        RequestQueue requestQueue = RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue();
        strReq.setTag("ConnectNodeMcuToWifi");
        requestQueue.add(strReq);

    }

    private void setUpToolBar() {

        // Setting up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarmain);
        myToolbar.setTitle(R.string.title_nodemcu_conn);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private boolean validateSsid() {
        Log.d("testmiki validateSsid", String.valueOf(mainWifiObj.getConnectionInfo().getSSID().startsWith(getString(R.string.sensorprefix), 1)));
        if (mainWifiObj.getConnectionInfo().getSSID().startsWith(getString(R.string.sensorprefix), 1)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean validateIpAddress() {
        //String ip = Formatter.formatIpAddress(mainWifiObj.getConnectionInfo().getIpAddress());

        String broj = String.valueOf(mainWifiObj.getDhcpInfo().ipAddress);
        Log.d("testmiki broj ", broj);
        if (broj.equals("0")) {
            return true;
        } else {
            return false;
        }


    }


    private void sentInfoToNodeMcu(final String ssid_current, final String networkPass, final String userId, final int count) {

       if (count==5) {
           Toast.makeText(getApplicationContext(), "Please Try Again", Toast.LENGTH_LONG).show();
           mainWifiObj.disconnect();
           Intent intent = new Intent(getApplicationContext(), SearchNetworks.class);
           startActivity(intent);
       }

        String tag_string_req = "sentDatatoNodeMcu";

        pDialog.showDialog("Establish connection to NodeMcu");

        String url = "http://192.168.4.1/konekcija?username="+ssid_current+"&password="+networkPass+"&userid="+userId;
       //String urlssd = String.format(AppConfig.URL_CONFIG_SENSOR_BASE_KONEKCIJA, "a", "asdas");

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        String uidres = jsonObject.getString("userid");
                        pDialog.showDialog("Now, validate status and click on button");
                    } else {
                        pDialog.showDialog("Data is not correct. Please try again");
                        return;
                        //String errorMsg = jsonObject.getString("error_msg");
                        //Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                int i = count+1;
                pDialog.hideDialog();
                pDialog.showDialog("Count "+ count +". Establish connection to NodeMcu");

                sentInfoToNodeMcu(ssid_current, networkPass, session.getUID(), i);

                if (null != error.networkResponse) {
                    Log.d("testmiki" + ": ", "Error Response code: " + error.networkResponse.statusCode);
                }

            }
        });


        //strReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        RequestQueue requestQueue = RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue();
        strReq.setTag("ConnectNodeMcuToWifiDva");
        requestQueue.add(strReq);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //super.onBackPressed();
                Intent intent = new Intent(ConnectNodeMcuToWifi.this, SearchNetworks.class);
                startActivity(intent);
                finish();
                //return true;
        }
        //super.onOptionsItemSelected(item);
        return true;
    }
    /*public void onBackPressed() {
        this.startActivity(new Intent(ConnectNodeMcuToWifi.this, SearchNetworks.class));
        finish();
        return;
    }*/

}

