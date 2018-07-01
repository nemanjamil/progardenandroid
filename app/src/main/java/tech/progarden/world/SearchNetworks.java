package tech.progarden.world;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.progarden.world.app.RequestQueueSingleton;
import tech.progarden.world.app.SessionManager;
import tech.progarden.world.dialogs.ProgressDialogCustom;
import tech.progarden.world.wifi.CustomNetworkAdapter;

public class SearchNetworks extends AppCompatActivity {

    WifiManager wifiManager;
    WifiScanReceiver wifiReciever;
    int wasNetworkId;
    String wifis[];
    ListView list;
    EditText pass;
    String ssid_current;
    SessionManager session;

    List<ScanResult> apList;
    ArrayList<ScanResult> ListaWifiNetWorks;

    private static CustomNetworkAdapter adapternetwork;
    ListView listadapter;
    ProgressDialogCustom pDialog;

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    LocationManager manager;
    int countgarden = 0;
    // https://www.programcreek.com/java-api-examples/index.php?source_dir=routerkeygenAndroid-master/android_donation/app/src/main/java/org/doublecheck/wifiscanner/WifiScanReceiver.java#
    // https://www.programcreek.com/java-api-examples/?class=android.net.wifi.WifiManager&method=removeNetwork

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_networks);

        pDialog = new ProgressDialogCustom(SearchNetworks.this);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        statusCheck();

        listadapter = (ListView) findViewById(R.id.networklist);

        if (pDialog.isShowing())
            pDialog.dismiss();

        pDialog.showDialog("Waiting for network list");

        session = new SessionManager(getApplicationContext());
        // Setting up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarmain);
        myToolbar.setTitle(R.string.list_of_networks);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        statusCheck();

    }

    public void statusCheck() {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            initWifi();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void initWifi() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        enableWifi();
        getWifiPermissions();
    }

    private void enableWifi() {
        if (false == wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
    }

    private void getWifiPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        } else {
            handleWifiPermissions();
        }
    }

    private void handleWifiPermissions() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        boolean startScan = wifiManager.startScan();
        /*if (startScan) {
            List<ScanResult> results = wifiManager.getScanResults();
            Toast.makeText(getApplicationContext(), "SSID list size: " + results.size(), Toast.LENGTH_SHORT).show();
            Log.d("testmiki ","handleWifiPermissions");
        }*/
    }


    // https://www.includehelp.com/code-snippets/android-application-to-display-available-wifi-network-and-connect-with-specific-network.aspx
    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        int local = 0;

        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifiManager.getScanResults();

            if (wifiScanList.isEmpty()) {
                Toast.makeText(getBaseContext(), "nema nista", Toast.LENGTH_LONG).show();
                Log.d("testmiki ", "nema nista");
            }

            ListaWifiNetWorks = new ArrayList<ScanResult>();
            for (int i = 0; i < wifiScanList.size(); i++) {
                ListaWifiNetWorks.add(wifiScanList.get(i));
                Log.d("testmiki ", String.valueOf(wifiScanList.get(i).SSID));
            }

            // Set adapter
            adapternetwork = new CustomNetworkAdapter(getApplicationContext(), ListaWifiNetWorks);
            listadapter.setAdapter(adapternetwork);
            pDialog.hideDialog();
            listadapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    ssid_current = wifiInfo.getSSID();
                    wasNetworkId = wifiManager.getConnectionInfo().getNetworkId();
                    ScanResult dataModel = ListaWifiNetWorks.get(position);

                    if (ssid_current.replace("\"", "").startsWith(getString(R.string.sensorprefix))){
                        connectToWifiGarden(dataModel.SSID, dataModel.BSSID, session.getUID(), ssid_current);
                    } else {
                        pDialog.hideDialog();
                        pDialog.showDialog("Step 1 : Please Connect to Garden-Wifi on you mobile phone");
                        //Log.d("testmiki ", "Please Connect to Garden-Wifi on you mobile phone");
                        //connectToWifi(dataModel.SSID, dataModel.BSSID, session.getUID(), ssid_current);
                    }

                }
            });


        }
    }

    private void connectToWifiGarden(final String klikutssid, final String kliknutbssid,final String uid,final String ssid_current) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.connectnetwork);
        dialog.setTitle("Connect to Network");
        TextView textSSID = (TextView) dialog.findViewById(R.id.textSSID1);

        Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
        pass = (EditText) dialog.findViewById(R.id.textPassword);
        textSSID.setText("Put password of your (" + ssid_current + ") internet, to connect " + klikutssid);

        // if button is clicked, connect to the network;
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkPassword = pass.getText().toString();
                dialog.dismiss();
                finallyConnectGarden(klikutssid, checkPassword, ssid_current, uid, kliknutbssid);
            }
        });
        dialog.show();
    }

    private void finallyConnectGarden(final String klikutssid, final String checkPassword, final String ssid_current, String uid, String kliknutbssid) {
        sentInfoToNodeMcuGarden(klikutssid, checkPassword, session.getUID(),countgarden);
    }

    private void sentInfoToNodeMcuGarden(final String klikutssid, final String checkPassword, String uid, final int countgarden) {

        pDialog.hideDialog();
        pDialog.showDialog("Sent information to NodeMcu : "+countgarden);

        if (countgarden==5) {
            Toast.makeText(getApplicationContext(), "Please Try Again; Disconnect from Garden-Wifi or restart NodeMcu", Toast.LENGTH_LONG).show();

            WifiInfo infogarden = wifiManager.getConnectionInfo();
            int idgarden = infogarden.getNetworkId();
            wifiManager.disconnect();
            wifiManager.disableNetwork(idgarden);
            wifiManager.removeNetwork(idgarden);

            Intent intent = new Intent(getApplicationContext(), SearchNetworks.class);
            startActivity(intent);

        }

        String tag_string_req = "sentDatatoNodeMcuGarden";
        String url = "http://192.168.4.1/konekcija?username="+klikutssid+"&password="+checkPassword+"&userid="+uid;

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        String uidres = jsonObject.getString("userid");
                        pDialog.hideDialog();
                        pDialog.showDialog("Data IS SENT!!! OK");
                        Intent intent = new Intent(getApplicationContext(), Step2Connection.class);
                        /*intent.putExtra("ssid", SSID);
                        intent.putExtra("networkPass", networkPass);
                        intent.putExtra("ssid_current", ssid_current.replace("\"", ""));*/
                        startActivity(intent);

                    } else {
                        pDialog.showDialog("Data is not correct. Please try again");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                int i = countgarden+1;
                pDialog.hideDialog();
                pDialog.showDialog("Count "+ countgarden +". Establish connection to NodeMcu");
                sentInfoToNodeMcuGarden(klikutssid, checkPassword, session.getUID(), i);
                if (null != error.networkResponse) {
                    Log.d("testmiki" + ": ", "Error Response code: " + error.networkResponse.statusCode);
                }
            }
        });

        RequestQueue requestQueue = RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue();
        strReq.setTag("ConnectNodeMcuToWifiDva");
        requestQueue.add(strReq);


    }

    private void connectToWifi(final String SSID, final String BSSID, final String userId, final String ssid_current) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.connectnetwork);
        dialog.setTitle("Connect to Network");
        TextView textSSID = (TextView) dialog.findViewById(R.id.textSSID1);

        Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
        pass = (EditText) dialog.findViewById(R.id.textPassword);
        textSSID.setText("Put password of your (" + ssid_current + ") internet, to connect " + SSID);

        // if button is clicked, connect to the network;
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkPassword = pass.getText().toString();
                dialog.dismiss();
                finallyConnect(SSID, checkPassword, ssid_current, userId, BSSID);
            }
        });
        dialog.show();
    }

    private void finallyConnect(final String SSID, final String networkPass, final String ssid_current, final String userId, String BSSID) {

        WifiInfo info = wifiManager.getConnectionInfo();
        int id = info.getNetworkId();
        wifiManager.disableNetwork(id);
        wifiManager.removeNetwork(id);
        //wifiManager.saveConfiguration();


        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", SSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", "password011");

        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.updateNetwork(wifiConfig);
        wifiManager.saveConfiguration();
        wifiManager.enableNetwork(netId, true);


        pDialog.showDialog("Connecting to NodeMcu");



    }





    private boolean checkIfIsConnected(String SSID) {


        if (wifiManager.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            //if( wifiInfo.getNetworkId() == -1 ){

            String kojijeWifi = wifiInfo.getSSID().replace("\"", "");

            //Log.d("testmiki getSSID ", kojijeWifi);
            //Log.d("testmiki CIFC ", String.valueOf(SSID));

            if (!kojijeWifi.equals(SSID)) {
                Log.d("testmiki false ", String.valueOf(SSID) + " " + kojijeWifi);
                return false;
            }
            return true;
        } else {
            return false; // Wi-Fi adapter is OFF
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maintanace, menu);
        return true;
    }

    public void onBackPressed() {
        this.startActivity(new Intent(SearchNetworks.this, DrawerActivity.class));
        finish();
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //super.onBackPressed();
                Intent intent = new Intent(SearchNetworks.this, DrawerActivity.class);
                startActivity(intent);
                finish();
                //return true;
        }
        //super.onOptionsItemSelected(item);
        return true;
    }

    /*@Override
    public void onResume() {
        super.onResume();
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getWifiPermissions();
        }
        Log.d("testmiki ","onResume");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d("testmiki ","onPostResume");
    }

    @Override
    protected void onStop()
    {
        Log.d("testmiki ", "onStop");
        unregisterReceiver(wifiReciever);
        super.onStop();
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION:
                handleWifiPermissions();
                break;
        }
    }

}
