package tech.progarden.world;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.progarden.world.app.AppConfig;
import tech.progarden.world.app.SessionManager;
import tech.progarden.world.array_adapters.KultureAdapter;
import tech.progarden.world.array_adapters.SensorListAdapter;
import tech.progarden.world.callback_interfaces.WebRequestCallbackInterface;
import tech.progarden.world.dialogs.ProgressDialogCustom;
import tech.progarden.world.dialogs.SelectPlantDialog;
import tech.progarden.world.dialogs.SensorDeleteConfirmationDialog;
import tech.progarden.world.dialogs.SensorScanConfirmationDialog;
import tech.progarden.world.settings.RetroCreator;
import tech.progarden.world.settings.SettingsActivity;
import tech.progarden.world.web_requests.DeleteSensorRequest;
import tech.progarden.world.web_requests.PullSensorListRequest;
import tech.progarden.world.web_requests.PullSensorPlantsRequest;
import tech.progarden.world.web_requests.UpdateSensorRequest;
import tech.progarden.world.web_requests.UpdateUserDataRequest;
import tech.progarden.world.web_requests.kulture.Kulture;
import tech.progarden.world.web_requests.kulture.KultureInterface;
import tech.progarden.world.web_requests.kulture.KultureResponse;
import tech.progarden.world.web_requests.kulture.KulutureParams;
import tech.progarden.world.wifi.SensorAPActivity_old;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener, SelectPlantDialog.OnDialogDataPass {

    private final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 232;
    private ListView listView;
    private SensorListAdapter customArrayAdapter;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private List<Kulture> listaKultura;
    private KultureAdapter adapter;
    private ActionMode actionBarReference;
    private SessionManager session;

    private PullSensorListRequest psl;
    private PullSensorPlantsRequest psp;
    private DeleteSensorRequest dsr;
    private UpdateSensorRequest usr;
    private UpdateUserDataRequest uudr;

    private String uid;

    private int sensor_count = 0; // For debugging only!!! Should be deleted when finished.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        session = new SessionManager(getApplicationContext());
        uid = session.getUID();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* TODO implement sensor adding action */

//                // Dummy code
//                sensor_count++;
//                //Add new sensor
//                if (sensor_count ==1)
//                    asr.addSensor(uid, "5CCF7F747A7", "4");
//                if (sensor_count ==2)
//                    asr.addSensor(uid, "5CCF7F752BA", "4");
//                if (sensor_count ==3)
//                    asr.addSensor(uid, "18FE349DB7E6", "4");
//                //end dummy code
                if (ContextCompat.checkSelfPermission(DrawerActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(DrawerActivity.this,
//                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                        // Show an explanation to the user *asynchronously* -- don't block
//                        // this thread waiting for the user's response! After the user
//                        // sees the explanation, try again to request the permission.
//                    } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(DrawerActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
//                    }
                } else {
                    // Permission has already been granted
                    goToSensorActivity();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this);

        session = new SessionManager(getApplicationContext());


        psl = new PullSensorListRequest(this);
        psl.setCallbackListener(new WebRequestCallbackInterface() {
            @Override
            public void webRequestSuccess(boolean success, JSONObject[] jsonObjects) {
                // reload sensor list

                if (success) {
                    //successfully loaded sensor list
                    customArrayAdapter = new SensorListAdapter(DrawerActivity.this, jsonObjects);
                    listView.setAdapter(customArrayAdapter);

                } else {
                    showSnack("Nema dodatih senzora.\nKlikni na + da dodaš senzor.");
                    customArrayAdapter = new SensorListAdapter(DrawerActivity.this, new JSONObject[0]);
                    listView.setAdapter(customArrayAdapter);
                }
            }

            @Override
            public void webRequestError(String error) {
                showSnack("Lista Senzora: Greška u komunikaciji sa serverom.");
            }
        });

        psp = new PullSensorPlantsRequest(this);
        psp.setCallbackListener(new WebRequestCallbackInterface() {
            @Override
            public void webRequestSuccess(boolean success, JSONObject[] jsonObjects) {
                //
            }

            @Override
            public void webRequestError(String error) {
                //
            }
        });

        dsr = new DeleteSensorRequest(this);
        dsr.setCallbackListener(new WebRequestCallbackInterface() {
            @Override
            public void webRequestSuccess(boolean success, JSONObject[] jsonObjects) {
                /*TODO create one final success callback, not after each individual sensor delete*/
                if (success) {
                    // refresh sensor list
                    psl.pullSensorList(uid);
                } else {
                    showSnack("Brisanje nije uspelo.");
                    psl.pullSensorList(uid);
                }
            }

            @Override
            public void webRequestError(String error) {
                // refresh sensor list
                showSnack("Brisanje: greška u komunikaciji sa serverom.");
                psl.pullSensorList(uid);
            }
        });

        uudr = new UpdateUserDataRequest(this);
        uudr.setCallbackListener(new WebRequestCallbackInterface() {
            @Override
            public void webRequestSuccess(boolean success, JSONObject[] jsonObjects) {
                if (success) {
                    // success
                    showSnack("Podešavanja uspešno promenjena.");
                } else {
                    showSnack("Podešavanja: server nije dozvolio promenu.");
                }
            }

            @Override
            public void webRequestError(String error) {
                // fail
                showSnack("Podešavanja: greška u komunikaciji sa serverom.");
            }
        });

        psl.pullSensorList(uid);
        psp.pullPlantList("YADA YADA"); // has to have real sensor ID
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    goToSensorActivity();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void goToSensorActivity() {
        final SensorScanConfirmationDialog sensorScanConfirmationDialog = new SensorScanConfirmationDialog(DrawerActivity.this);
        sensorScanConfirmationDialog.setPositiveButtonListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DrawerActivity.this, SensorAPActivity_old.class);
                startActivity(intent);
            }
        });
        sensorScanConfirmationDialog.setNegativeButtonListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        sensorScanConfirmationDialog.create().show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {

            //If the session is logged in log out
            if (session.isLoggedIn()) {
                /*TODO make actual log out from the server */
                session.setLogin(false);
                Intent intent = new Intent(DrawerActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

        } else if (id == R.id.nav_add) {
            fab.callOnClick();
        } else if (id == R.id.nav_settings) {
            // start settings activity
            Intent intent = new Intent(DrawerActivity.this, SettingsActivity.class);
            startActivityForResult(intent, AppConfig.ACTIVITY_REQ_SETTINGS);
            //finish();
        } else if (id == R.id.sensor_list) {
        // start settings activity
            Intent intent = new Intent(DrawerActivity.this, DrawerActivity.class);
            startActivity(intent);
        //finish();
    } else if (id == R.id.network_state) {
            Intent intent = new Intent(DrawerActivity.this, SearchNetworks.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        ListAdapter la = listView.getAdapter();
        SensorListAdapter sla = (SensorListAdapter) la;
        GetKultureList(sla.getSenzorId(position), sla.getSensorMAC(position));
    }

    private void GetKultureList(String id, final String SenzorMac) {
        final ProgressDialogCustom pDialog = new ProgressDialogCustom(DrawerActivity.this);
        pDialog.show();

        KultureInterface service = RetroCreator.getService().create(KultureInterface.class);
        KulutureParams params = new KulutureParams();
        params.id = id;
        final Call<KultureResponse> repos = service.getKulture(params);
        repos.enqueue(new Callback<KultureResponse>() {
            @Override
            public void onResponse(Call<KultureResponse> call, Response<KultureResponse> response) {
                pDialog.dismiss();

                final Dialog dialog = new Dialog(DrawerActivity.this);
                dialog.setContentView(R.layout.activity_kulture);

                try {
                    if (response.body().success) {
                        listaKultura = new ArrayList<>();
                        listaKultura.addAll(response.body().podaci);
                        ListView lvListKultura = (ListView) dialog.findViewById(R.id.lvListKultura);
                        adapter = new KultureAdapter(DrawerActivity.this, listaKultura);
                        lvListKultura.setAdapter(adapter);
                        lvListKultura.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                //Intent intent = new Intent(DrawerActivity.this, GraphActivity.class);
                                Intent intent = new Intent(DrawerActivity.this, SensorDetailActivity.class);
                                intent.putExtra("SensorMAC", SenzorMac);
                                intent.putExtra("KulturaId", listaKultura.get(i).IdKulture);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else {
                        showSnack(response.body().error_msg);
                    }
                } catch (Exception e) {
                    showSnack(response.message());
                }
            }

            @Override
            public void onFailure(Call<KultureResponse> call, Throwable t) {
                t.printStackTrace();
                pDialog.dismiss();
            }
        });
    }

    //Multichoice mode listener methods for list view

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        // Here you can do something when items are selected/de-selected,
        // such as update the title in the CAB

        ListAdapter la = listView.getAdapter();
        SensorListAdapter sla = (SensorListAdapter) la;
        sla.selectView(position, checked);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate the menu for the CAB
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sensor_list_cab, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // Here you can perform updates to the CAB due to
        // an invalidate() request

        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        // Respond to clicks on the actions in the CAB
        switch (item.getItemId()) {
            case R.id.action_delete:
                //deleteSelectedItems();
                // create Alert dialog to confirm sensor delete
                SensorDeleteConfirmationDialog sensorDeleteConfirmationDialog = new SensorDeleteConfirmationDialog(DrawerActivity.this);
                sensorDeleteConfirmationDialog.setPositiveButtonListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dsr.deleteSensorListRequest(uid);

                        if (actionBarReference != null)// Action picked, so close the CAB
                            actionBarReference.finish();
                    }
                });
                sensorDeleteConfirmationDialog.setNegativeButtonListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                sensorDeleteConfirmationDialog.create().show();
                actionBarReference = mode;

                //mode.finish(); // Action picked, so close the CAB
                return true;
            case R.id.action_change:
                // Show pick list
                SelectPlantDialog spd = new SelectPlantDialog();
                spd.show(getFragmentManager(), "pick_plant_TAG");
                actionBarReference = mode;
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // Here you can make any necessary updates to the activity when
        // the CAB is removed. By default, selected items are deselected/unchecked.
        // customArrayAdapter.removeSelection();
        ListAdapter la = listView.getAdapter();
        SensorListAdapter sla = (SensorListAdapter) la;
        sla.removeSelection();
    }

    private void showSnack(String msg) {
        Snackbar.make(fab, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onDialogDataPass(String data) {
        // Update sensor
        AppConfig.logInfo("onDialogDataPass", data);
        usr = new UpdateSensorRequest(this);
        usr.setCallbackListener(new WebRequestCallbackInterface() {
            @Override
            public void webRequestSuccess(boolean success, JSONObject[] jsonObjects) {
                if (success) {
                    if (actionBarReference != null)// Action picked, so close the CAB
                        actionBarReference.finish();
                    //reload sensor list
                    psl.pullSensorList(uid);
                } else {
                    showSnack("Izmena kulture nije uspela.");
                }
            }

            @Override
            public void webRequestError(String error) {
                // inform user
                showSnack("Nije uspelo. Greška prilikom komunikacije sa serverom");
            }
        });
        usr.updateSensorListRequest(uid, data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        AppConfig.logInfo("DrawerActivity", "ActivityResult");
        AppConfig.logInfo("DrawerActivity", String.valueOf(requestCode));
        AppConfig.logInfo("DrawerActivity", String.valueOf(resultCode));
        if (requestCode == AppConfig.ACTIVITY_REQ_SETTINGS) {
            // Make sure the request was successful
            AppConfig.logInfo("DrawerActivity", "RightRequest");
            if (resultCode == AppConfig.ACTIVITY_RESP_SETTINGS_UPDATE) {
                AppConfig.logInfo("DrawerActivity", "Updating...");
                uudr.updateUserData();
            }
        }

    }



}
