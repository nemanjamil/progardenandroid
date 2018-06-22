package tech.progarden.world.wifi;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.List;

import tech.progarden.world.R;
import tech.progarden.world.app.AppConfig;
import tech.progarden.world.app.AppController;
import tech.progarden.world.array_adapters.SensorAPListAdapter;


public class SensorAPActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    WifiManager wifiManager;
    boolean wasWiFiEnabled;
    boolean wasWiFiEnabledBeforeSensorConfig;
    boolean configuringSensor;
    int wasNetworkId;

    ListView listView;
    Toolbar toolbar;
    WifiScanReceiver wifiReciever = new WifiScanReceiver();

    ProgressDialog progressDialog;

    List<ScanResult> wifiScanList;
    List<ScanResult> wifiSensorAPList;
    List<ScanResult> wifiAPList;
    String ssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_ap);
        listView = (ListView) findViewById(R.id.lv_access_points);
        listView.setOnItemClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // get Wifi service
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wasWiFiEnabled = wifiManager.isWifiEnabled();

        //setting progressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        setSupportActionBar(toolbar);
        // enabling wifi

        configuringSensor = false;
        wifiManager.setWifiEnabled(true);

    }

    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        registerReceiver(wifiReciever, intentFilter);
        AppConfig.logInfo("SensorScan", "onResume");
        super.onResume();
    }

    /*
    function to show dialog
    */
    private void showDialog(String msg) {
        if (!progressDialog.isShowing()) {
            progressDialog.setMessage(msg);
            progressDialog.show();
        }

    }

    /*
    function to hide dialog
     */
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // the list item was clicked

        AlertDialog.Builder builder = new AlertDialog.Builder(SensorAPActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = SensorAPActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.pick_wifi_dialog, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView);

        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner);
        final EditText password = (EditText) dialogView.findViewById(R.id.password_wifi);
//        final TextView tv_ssid = (TextView) dialogView.findViewById(R.id.firstLine);
//        final TextView tv_mac = (TextView) dialogView.findViewById(R.id.secondLine);
//
//        tv_ssid.setText(wifiScanList.get(position).SSID);
//        tv_mac.setText(wifiScanList.get(position).BSSID);

        ssid = wifiScanList.get(position).SSID;

        String[] items = new String[wifiAPList.size()];
        for (int i = 0; i < wifiAPList.size(); i++) {
            items[i] = wifiAPList.get(i).SSID;
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, items);

        spinner.setAdapter(spinnerAdapter);
        // Add action buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                /* TODO save current Wifi state*/
                showDialog("Povezivanje sa senzorom...");
                //pool.execute(new ConnectToSensorRunnable());
                wasNetworkId = wifiManager.getConnectionInfo().getNetworkId();
                wasWiFiEnabledBeforeSensorConfig = wifiManager.isWifiEnabled();
                configuringSensor = true;
                final WifiConfiguration config = new WifiConfiguration();
                config.SSID = "\"" + ssid + "\"";
                config.preSharedKey = "\"12345678\"";
                config.priority = 999;

                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                }
                int networkId = wifiManager.addNetwork(config);
                wifiManager.saveConfiguration();
                //wifiManager.disconnect();
                wifiManager.enableNetwork(networkId, true);
                //wifiManager.reconnect();
                AppConfig.logInfo("OKClicked", "enabling " + ssid);

            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        String title = getResources().getString(R.string.set_wifi_dialog_title);
        title = title + " " + ssid;
        builder.setTitle(title);

        builder.create().show();
    }

    private void showSnack(String msg) {
        Snackbar.make(listView, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    private void configureSensor(String url) {
        final StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AppConfig.logInfo("SensorResponse", response);
                Toast.makeText(getApplicationContext(),
                        response, Toast.LENGTH_LONG).show();
                //restore previous wifi connection
                wifiManager.setWifiEnabled(wasWiFiEnabledBeforeSensorConfig);
                if (wasWiFiEnabledBeforeSensorConfig) {
                    wifiManager.enableNetwork(wasNetworkId, true);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                AppConfig.logInfo("SensorError", error.getMessage());
                //hideDialog();
            }
        });


        strReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to  queue
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppController.getInstance().addToRequestQueue(strReq, "abcd_req");
            }
        }, 2000);

    }

    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            /*TODO Check for intent action*/
            String intentAction = intent.getAction();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intentAction)) {
                AppConfig.logInfo("BroadCastRec qqq", "scan results available");
                if (!configuringSensor) {
                    wifiScanList = wifiManager.getScanResults();
                    wifiSensorAPList = new ArrayList<ScanResult>();
                    wifiAPList = new ArrayList<ScanResult>();

                    for (int i = 0; i < wifiScanList.size(); i++) {
                        if (wifiScanList.get(i).SSID.startsWith(getString(R.string.sensorprefix)))
                            wifiSensorAPList.add(wifiScanList.get(i));
                        else
                            wifiAPList.add(wifiScanList.get(i));
                    }

                    if (wifiSensorAPList.size() == 0)
                        showSnack("qqq Nije pronađen ni jedan senzor.\nDa li ste prebacili senzor u mod za podešavanje?");

                    listView.setAdapter(new SensorAPListAdapter(getApplicationContext(), wifiScanList));
                    hideDialog();
                }
            } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intentAction)) {
                int wifi_state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                if (wifi_state == WifiManager.WIFI_STATE_ENABLING) {
                    AppConfig.logInfo("BroadCastRec qqq", "wifi enabling");

                    // show progress dialog
                    showDialog("Uključujem WiFi...");
                } else if (wifi_state == WifiManager.WIFI_STATE_ENABLED) {
                    // hide progress dialog
                    AppConfig.logInfo("BroadCastRec", "wifi enabled");

                    hideDialog();
                    if (!configuringSensor) {
                        wifiManager.startScan();
                        showDialog("Tražim senzore...");
                    }
                } else if (wifi_state == WifiManager.WIFI_STATE_DISABLED) {
                    // hide progress dialog
                    AppConfig.logInfo("BroadCastRec", "wifi disabled");

                    hideDialog();
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intentAction)) {
                AppConfig.logInfo("BroadCastRec", "connection change action");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                if (info != null) {
                    AppConfig.logInfo("BroadCastRec", info.getState().toString());
                }


                if (info != null && info.isConnected()) {

                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();
                    AppConfig.logInfo("BroadCastRec", ssid);
                    AppConfig.logInfo("BroadCastRec", String.valueOf(configuringSensor));
                    AppConfig.logInfo("BroadCastRec", String.valueOf(ssid.startsWith(getString(R.string.sensorprefix), 1)));

                    if (configuringSensor && ssid.startsWith(getString(R.string.sensorprefix), 1)) {
                        AppConfig.logInfo("BroadCastRec", "Sakrij dialog");
                        hideDialog();
                        showSnack("Povezan sa senzorom");
                        configuringSensor = false;
                        //Send web request to sensor
                        String url = "http://192.168.4.1/read";
                        configureSensor(url);
                    }
                }

                if (info != null && (info.getDetailedState() == NetworkInfo.DetailedState.FAILED)) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();
                    if (configuringSensor && ssid.startsWith(getString(R.string.sensorprefix), 1)) {
                        AppConfig.logInfo("BroadCastRec", "Sakrij dialog");
                        hideDialog();
                        showSnack("Nije uspelo povezivanje sa senzorom");
                        configuringSensor = false;
                    }
                }
            }
        }
    }
}


