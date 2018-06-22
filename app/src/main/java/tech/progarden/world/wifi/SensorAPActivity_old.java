package tech.progarden.world.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.progarden.world.DrawerActivity;
import tech.progarden.world.R;
import tech.progarden.world.app.AppConfig;
import tech.progarden.world.app.SessionManager;
import tech.progarden.world.array_adapters.SensorAPListAdapter;
import tech.progarden.world.callback_interfaces.WebRequestCallbackInterface;
import tech.progarden.world.dialogs.ProgressDialogCustom;
import tech.progarden.world.web_requests.AddSensorRequest;
import tech.progarden.world.web_requests.ConfigSensorRequest;


public class SensorAPActivity_old extends AppCompatActivity implements AdapterView.OnItemClickListener {
    WifiManager wifiManager;
    boolean wasWiFiEnabled;
    boolean wasWiFiEnabledBeforeSensorConfig;
    boolean configuringSensor;
    boolean addingSensor;
    int wasNetworkId;

    ListView listView;
    Toolbar toolbar;
    WifiScanReceiver wifiReciever = new WifiScanReceiver();

    ProgressDialogCustom progressDialog;

    List<ScanResult> wifiScanList;
    List<ScanResult> wifiSensorAPList;
    List<ScanResult> wifiAPList;
    String ssid;
    String bssid;

    int networkId;

    String config_pass;
    String config_ssid;

    private ConfigSensorRequest csr;
    private AddSensorRequest asr;

    private SessionManager session;
    private String uid;

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
        progressDialog = new ProgressDialogCustom(this);
        progressDialog.setCancelable(false);

        setSupportActionBar(toolbar);

        session = new SessionManager(getApplicationContext());
        uid = session.getUID();

        csr = new ConfigSensorRequest(this);
        csr.setCallbackListener(new WebRequestCallbackInterface() {
            @Override
            public void webRequestSuccess(boolean success, JSONObject[] jsonObjects) {
                if (success) {
                    //showSnack("Senzor uspešno konfigurisan.");
                    AlertDialog alertDialog = new AlertDialog.Builder(SensorAPActivity_old.this).create();
                    alertDialog.setTitle("Konfigurisanje senzora");
                    alertDialog.setMessage("Senzor je uspešno konfigurisan.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Launch login activity
                                    //
                                    progressDialog.showDialog("Čekam internet konekciju...");

                                    addingSensor = true;

                                    if (wasWiFiEnabledBeforeSensorConfig){
                                        wifiManager.enableNetwork(wasNetworkId, true);
                                    }else
                                    {
                                        wifiManager.setWifiEnabled(false);
                                    }
                                    //finish();
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    showSnack("Konfigurisanje senzora nije uspelo.");
                }
            }

            @Override
            public void webRequestError(String error) {

                if (wasWiFiEnabledBeforeSensorConfig){
                    wifiManager.enableNetwork(wasNetworkId, true);
                }else
                {
                    wifiManager.setWifiEnabled(false);
                }
                //showSnack(error);
                AlertDialog alertDialog = new AlertDialog.Builder(SensorAPActivity_old.this).create();
                alertDialog.setTitle("Konfigurisanje senzora");
                alertDialog.setMessage(error);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Launch login activity

                                finish();
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }
        });

        asr = new AddSensorRequest(SensorAPActivity_old.this);
        asr.setCallbackListener(new WebRequestCallbackInterface() {
            @Override
            public void webRequestSuccess(boolean success, JSONObject[] jsonObjects) {
                if (success) {
                    AlertDialog alertDialog = new AlertDialog.Builder(SensorAPActivity_old.this).create();
                    alertDialog.setTitle("Dodavanje senzora");
                    alertDialog.setMessage("Senzor je uspešno dodat.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Launch drawer activity
                                    Intent intent = new Intent(getApplicationContext(),
                                            DrawerActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(SensorAPActivity_old.this).create();
                    alertDialog.setTitle("Dodavanje senzora");
                    alertDialog.setMessage("Nije uspelo.\nMožda je senzor već dodat?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Launch login activity
                                    finish();
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }

            @Override
            public void webRequestError(String error) {
                AlertDialog alertDialog = new AlertDialog.Builder(SensorAPActivity_old.this).create();
                alertDialog.setTitle("Dodavanje senzora");
                alertDialog.setMessage("Nije uspelo.\nProverite vezu sa internetom.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Launch login activity
                                finish();
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        configuringSensor = false;
        addingSensor = false;
        // enabling wifi
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
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(wifiReciever, intentFilter);
        AppConfig.logInfo("SensorScan", "onResume");
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // the list item was clicked

        AlertDialog.Builder builder = new AlertDialog.Builder(SensorAPActivity_old.this);
        // Get the layout inflater
        LayoutInflater inflater = SensorAPActivity_old.this.getLayoutInflater();
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

        ssid = wifiSensorAPList.get(position).SSID;
        bssid = wifiSensorAPList.get(position).BSSID;

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
                AppConfig.logInfo("BroadCastRec klikno", "klikno sam na OK ");
                progressDialog.showDialog("Povezivanje sa senzorom...");
                //pool.execute(new ConnectToSensorRunnable());
                wasNetworkId = wifiManager.getConnectionInfo().getNetworkId();
                wasWiFiEnabledBeforeSensorConfig = wifiManager.isWifiEnabled();
                configuringSensor = true;
                AppConfig.logInfo("BroadCastRec klikno", "------------ ");

                final WifiConfiguration config = new WifiConfiguration();
                config.SSID = "\"" + ssid + "\"";
                config.preSharedKey = "\"password011\"";
                config.priority = 1;

                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                }
                networkId = wifiManager.addNetwork(config);
                wifiManager.updateNetwork(config);
                wifiManager.saveConfiguration();
                //wifiManager.disconnect();
                wifiManager.enableNetwork(networkId, true);
                //wifiManager.reconnect();
                AppConfig.logInfo("OKClicked", "enabling " + ssid);

                config_pass = password.getText().toString();
                config_ssid = spinner.getSelectedItem().toString();

                AppConfig.logInfo("BroadCastRec confsen", String.valueOf(configuringSensor));

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


    private class TaskEsp extends AsyncTask<String, Void, String> {

        String server;

        TaskEsp(String server) {
            this.server = server;
        }

        @Override
        protected String doInBackground(String... params) {

//            //http://192.168.4.1/wifisave?s=5ku11&p=123456#-3#1
//            final String p = "http://" + server + "/wifisave?s=" + config_ssid + "&p=" + config_pass;
//            HttpURLConnection urlConnection = null;
//            try {
//                Thread.sleep(1000);
//                URL url = new URL(p);
//
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setConnectTimeout(90000);
//                AppConfig.logInfo("Headers",urlConnection.getHeaderFields().toString());
//
//                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                urlConnection.disconnect();
//            }
//
//
//            String serverResponse = "";
//
//            return serverResponse;
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            csr.configSensor(config_ssid, config_pass);
            return "";
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            /*TODO Check for intent action*/
            String intentAction = intent.getAction();
            AppConfig.logInfo("BroadCastRec intent ", intentAction);  // posle liste



            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intentAction)) {
                AppConfig.logInfo("BroadCastRec", "scan results available");
                if (!configuringSensor && !addingSensor) {
                    AppConfig.logInfo("BroadCastRec","GETTING SCAN RESULTS");

                    wifiScanList = wifiManager.getScanResults();
                    wifiSensorAPList = new ArrayList<ScanResult>();
                    wifiAPList = new ArrayList<ScanResult>();

                    for (int i = 0; i < wifiScanList.size(); i++) {
                        AppConfig.logInfo("BroadCastRec wifiScanList",wifiScanList.get(i).SSID);
//                        if (wifiScanList.get(i).SSID.startsWith(getString(R.string.sensorprefix)))
                            wifiSensorAPList.add(wifiScanList.get(i));

//                        else
//                            wifiAPList.add(wifiScanList.get(i));
                    }

                    if (wifiSensorAPList.size() == 0) {
//                        final Snackbar snackbar = Snackbar
//                                .make(listView, "Nije pronađen ni jedan senzor.\nResetujte senzor.", Snackbar.LENGTH_INDEFINITE);
//                        snackbar.setAction("NAZAD", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                snackbar.dismiss();
//                                finish();
//                            }
//                        });

                        // snackbar.show();
                        if (wasWiFiEnabled){
                            wifiManager.enableNetwork(wasNetworkId, true);
                            AppConfig.logInfo("SensorWasWifi", "enabling network...");
                        }else
                        {
                            wifiManager.setWifiEnabled(false);
                            AppConfig.logInfo("SensorWasWifi", "disabling network...");
                        }
                        AlertDialog alertDialog = new AlertDialog.Builder(SensorAPActivity_old.this).create();
                        alertDialog.setTitle("Pretraga senzora");
                        alertDialog.setMessage("Nije pronađen ni jedan senzor.\n" +
                                "Resetujte senzor.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Launch Drawer activity
                                        Intent intent = new Intent(getApplicationContext(),
                                                DrawerActivity.class);
                                        startActivity(intent);
                                        finish();
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();

                        //showSnack("Nema nijednog senzora? Podesi senzor u mod za konfigurisanje.");
                        progressDialog.hideDialog();
                    }

                    // setujemo adapter
                    listView.setAdapter(new SensorAPListAdapter(getApplicationContext(), wifiSensorAPList));
                    progressDialog.hideDialog();
                }
            }



            else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intentAction)) {
                int wifi_state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                if (wifi_state == WifiManager.WIFI_STATE_ENABLING) {
                    AppConfig.logInfo("BroadCastRec", "wifi enabling");

                    // show progress dialog
                    progressDialog.showDialog("Uključujem WiFi...");
                } else if (wifi_state == WifiManager.WIFI_STATE_ENABLED) {
                    // hide progress dialog
                    AppConfig.logInfo("BroadCastRec", "wifi enabled");

                    progressDialog.hideDialog();
                    if (!configuringSensor && !addingSensor) {
                        wifiManager.startScan();
                        progressDialog.showDialog("Tražim senzore...");
                    }
                } else if (wifi_state == WifiManager.WIFI_STATE_DISABLED) {
                    // hide progress dialog
                    AppConfig.logInfo("BroadCastRec", "wifi disabled");

                    progressDialog.hideDialog();
                }
            }



            else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intentAction)) {
                AppConfig.logInfo("BroadCastRec", "connection change action");
                //NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                //boolean isWiFi = info.getType() == ConnectivityManager.TYPE_WIFI;

                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnected();
                boolean isWiFi = activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

                AppConfig.logInfo("BroadCastRec", " 11 ");

                if (activeNetwork != null) {
                    AppConfig.logInfo("BroadCastRec Conne", activeNetwork.getState().toString());
                }

                if (activeNetwork != null && isConnected) {
                    boolean goAdd = true;
                    if (isWiFi) {
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        String ssid = wifiInfo.getSSID();
                        goAdd = !ssid.startsWith(getString(R.string.sensorprefix), 1);
                    }
                    if (goAdd && addingSensor) {

                        asr.addSensor(uid, bssid.replace(":","").toUpperCase(), "1");
                        addingSensor = false;
                    }
                }

                AppConfig.logInfo("BroadCastRec", " 22 ");
                if (activeNetwork != null && isConnected && isWiFi) {

                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();

                    AppConfig.logInfo("BroadCastRec", ssid);
                    AppConfig.logInfo("BroadCastRec", String.valueOf(configuringSensor));
                    AppConfig.logInfo("BroadCastRec", String.valueOf(ssid.startsWith(getString(R.string.sensorprefix), 1)));

                    if (configuringSensor) { // configuringSensor
                        if (ssid.startsWith(getString(R.string.sensorprefix), 1)) {
                            AppConfig.logInfo("BroadCastRec", "Sakrij dialog");
                            progressDialog.hideDialog();
                            showSnack("Povezan sa senzorom...");
                            configuringSensor = false;
                            //csr.configSensor(config_ssid, config_pass);
                            //Send web request to sensor
//                            String ip = "192.168.4.1";
//                            TaskEsp taskEsp = new TaskEsp(ip);
//                            taskEsp.execute("abc");
                            csr.configSensor(config_ssid, config_pass);
                        } else if (!ssid.equals("<unknown ssid>")) {
                            progressDialog.hideDialog();
//                            showSnack("Nije uspelo pvezivanje sa senzorom.\n" +
//                                    "Resetujte senzor pa probajte ponovo.");
                            AlertDialog alertDialog = new AlertDialog.Builder(SensorAPActivity_old.this).create();
                            alertDialog.setTitle("Povezivanje sa senzorom");
                            alertDialog.setMessage("Nije uspelo povezivanje sa senzorom.\n" +
                                    "Resetujte senzor pa probajte ponovo.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Launch login activity

                                            finish();
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    } else {
                        AppConfig.logInfo("BroadCastRec", "221 false configuringSensor je false");
                    }
                }

                AppConfig.logInfo("BroadCastRec", " 33 ");

                if (activeNetwork != null && (activeNetwork.getDetailedState() == NetworkInfo.DetailedState.FAILED)) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();
                    if (configuringSensor && ssid.startsWith(getString(R.string.sensorprefix), 1)) {
                        AppConfig.logInfo("BroadCastRec", "Sakrij dialog");
                        progressDialog.hideDialog();
//                        showSnack("Nije uspelo pvezivanje sa senzorom.\n" +
//                                "Resetujte senzor pa probajte ponovo.");

                        AlertDialog alertDialog = new AlertDialog.Builder(SensorAPActivity_old.this).create();
                        alertDialog.setTitle("Povezivanje sa senzorom");
                        alertDialog.setMessage("Nije uspelo povezivanje sa senzorom.\n" +
                                "Resetujte senzor pa probajte ponovo.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Launch login activity

                                        finish();
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        configuringSensor = false;
                    }
                }



            }
        }
    }
}


