package tech.progarden.world;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
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

import java.util.ArrayList;
import java.util.List;

import tech.progarden.world.app.SessionManager;
import tech.progarden.world.dialogs.ProgressDialogCustom;
import tech.progarden.world.wifi.CustomNetworkAdapter;

public class SearchNetworks extends AppCompatActivity {

    WifiManager mainWifiObj;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_networks);

        pDialog = new ProgressDialogCustom(SearchNetworks.this);

        /*LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;


        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            pDialog.showDialog("Enable Location services");
        }*/


        listadapter = (ListView) findViewById(R.id.networklist);

        boolean isLocEnabled = isLocationEnabled(getApplicationContext());
        if (!isLocEnabled) {
            pDialog.showDialog("Please, to continue, enable Location services in dropdown menu");
            return;
        }


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

        mainWifiObj = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        registerReceiver(wifiReciever, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if (mainWifiObj.isWifiEnabled() == false) {
            mainWifiObj.setWifiEnabled(true);
        }

        mainWifiObj.startScan();
        Log.d("testmiki", "start scan...");


    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
            //return false;
        }


    }

    // https://www.includehelp.com/code-snippets/android-application-to-display-available-wifi-network-and-connect-with-specific-network.aspx
    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {


            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
            ListaWifiNetWorks = new ArrayList<ScanResult>();

            for (int i = 0; i < wifiScanList.size(); i++) {
                ListaWifiNetWorks.add(wifiScanList.get(i));
                //Log.d("testmiki", String.valueOf(wifiScanList.get(i).SSID));
            }

            // Set adapter
            adapternetwork = new CustomNetworkAdapter(getApplicationContext(), ListaWifiNetWorks);
            listadapter.setAdapter(adapternetwork);
            pDialog.hideDialog();
            listadapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    WifiInfo wifiInfo = mainWifiObj.getConnectionInfo();
                    ssid_current = wifiInfo.getSSID();
                    //Log.d("testmiki", "Trenutni ssid : " + ssid_current);
                    wasNetworkId = mainWifiObj.getConnectionInfo().getNetworkId();
                    //Log.d("testmiki", "Trenutni wasNetworkId : " + wasNetworkId);
                    // mainWifiObj.enableNetwork(wasNetworkId, true); kada hocemo da se vratimo na proslu mrezu

                    ScanResult dataModel = ListaWifiNetWorks.get(position);

                    //Log.d("testmiki dataModel", String.valueOf(dataModel));
                    // String kojijeWifi = wifiInfo.getSSID().replace("\"", "");

                    //Log.d("testmiki kojaMreza ", String.valueOf(ssid_current.replace("\"", "")) + " " + dataModel.SSID);

                    if (!ssid_current.replace("\"", "").equals(dataModel.SSID)) {
                        connectToWifi(dataModel.SSID, dataModel.BSSID, session.getUID(), ssid_current);
                    } else {
                        Log.d("testmiki","vec je konekotavan na tu mrezu");
                        mainWifiObj.disconnect();
                        Intent intent = new Intent(getApplicationContext(), DrawerActivity.class);
                        startActivity(intent);

                    }

                }
            });


        }
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
                finallyConnect(SSID, checkPassword, ssid_current, userId);


            }
        });
        dialog.show();
    }

    private void finallyConnect(final String SSID, final String networkPass, final String ssid_current, final String userId) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", SSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", "password011");

        // remember id
        int netId = mainWifiObj.addNetwork(wifiConfig);
        mainWifiObj.updateNetwork(wifiConfig);
        mainWifiObj.saveConfiguration();
        //mainWifiObj.disconnect();
        mainWifiObj.enableNetwork(netId, true);
        //mainWifiObj.reconnect();

       /* WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"\"" + SSID + "\"\"";
        conf.preSharedKey = "\"" + "password011" + "\"";
        mainWifiObj.addNetwork(conf);*/

        pDialog.showDialog("Connecting to NodeMcu");

        Thread t = new Thread() {

            @Override
            public void run() {
                try {


                    while (!checkIfIsConnected(SSID)) {
                        //Log.d("testmiki chc TF  ", String.valueOf(checkIfIsConnected(SSID)));
                        Thread.sleep(2000);
                    }

                    //Log.d("testmiki chc 2 ", String.valueOf(checkIfIsConnected(SSID)));

                    //sentInfoToNodeMcu(ssid_current, networkPass, userId);
                    Intent intent = new Intent(getApplicationContext(), ConnectNodeMcuToWifi.class);
                    intent.putExtra("ssid", SSID);
                    intent.putExtra("networkPass", networkPass);
                    intent.putExtra("ssid_current",ssid_current.replace("\"", ""));
                    startActivity(intent);
                    //pDialog.hideDialog();


                } catch (Exception e) {
                }
            }
        };

        // dialog.dismiss();


        t.start();


    }



    private boolean checkIfIsConnected(String SSID) {


        if (mainWifiObj.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = mainWifiObj.getConnectionInfo();
            //if( wifiInfo.getNetworkId() == -1 ){

            String kojijeWifi = wifiInfo.getSSID().replace("\"", "");

            //Log.d("testmiki getSSID ", kojijeWifi);
            //Log.d("testmiki CIFC ", String.valueOf(SSID));

            if (!kojijeWifi.equals(SSID)) {
                //Log.d("testmiki false ", String.valueOf(SSID) + " " + kojijeWifi);
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

}
