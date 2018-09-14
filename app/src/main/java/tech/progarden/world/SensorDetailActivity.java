package tech.progarden.world;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tech.progarden.world.app.AppConfig;
import tech.progarden.world.app.AppController;
import tech.progarden.world.app.RequestQueueSingleton;
import tech.progarden.world.app.SessionManager;


public class SensorDetailActivity extends AppCompatActivity {

    private SessionManager session;
    String SensorMAC = null;
    Integer KulturaId = null;
    String userID = null;
    JSONObject jsonObject;
    JSONArray jsonArray;
    JSONArray jsonArrayIn;
    ArrayList arraylist;
    ArrayList<ListaVarijabli>  listaVarijabli;
    private ProgressBar spinner;
//    Button buttonGraph;

    ListView listView;
    ViewAdapterSensorDetail adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sensor_detail);
        // Setting up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.title_activity_sensor_detail);
        setSupportActionBar(myToolbar);

        spinner=(ProgressBar)findViewById(R.id.progressBar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        listaVarijabli = new ArrayList<ListaVarijabli>();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        arraylist = new ArrayList<>();
//        buttonGraph = (Button) findViewById(R.id.buttonGraph);
        listView = (ListView) findViewById(R.id.idListViewKategorije);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            SensorMAC = extras.getString("SensorMAC"); // retrieve the data using keyName
            KulturaId = extras.getInt("KulturaId");
        }
        //Toast.makeText(this, SensorMAC + " " + KulturaId, Toast.LENGTH_SHORT).show();

//        buttonGraph.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showGraph();
//            }
//        });

        session = new SessionManager(getApplicationContext());
        userID = session.getUID();
        getResults(SensorMAC, KulturaId, userID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_bar_sensor_detail, menu);
        return true;
    }

    private void showGraph() {
        Intent intent = new Intent(SensorDetailActivity.this, GraphActivity.class);
        intent.putExtra("SensorMAC", SensorMAC);
        intent.putExtra("KulturaId", KulturaId);
        startActivity(intent);
    }

    private void showMaintenance(){
        Intent intent = new Intent(SensorDetailActivity.this, MainTenance.class);
        intent.putExtra("SensorMAC", SensorMAC);
        //intent.putExtra("KulturaId", KulturaId);
        startActivity(intent);
    }

    private void getResults(final String sensorMAC, final Integer kulturaId, final String uid) {
        String tag_string_req = "req_login";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETINFOPARAMETER_POST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {

                    jsonObject = new JSONObject(response);
                    Log.d("testmiki response", String.valueOf(response));
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {

                        jsonArray = jsonObject.getJSONArray("podaciSenzor");

                        for (int i = 0; i < jsonArray.length(); i++) {


                            JSONObject c = jsonArray.getJSONObject(i);
                            String ImeKulture = c.getString("ImeKulture");
                            Integer IdListaSenzora = c.getInt("IdListaSenzora");
                            Integer IdKulture = c.getInt("IdKulture");
                            Integer IdSenzorTip = c.getInt("IdSenzorTip");
                            String senzorTipIme = c.getString("senzorTipIme");

                            Integer OdPodaciIdeal = c.getInt("OdPodaciIdeal");
                            Integer DoPodaciIdeal = c.getInt("DoPodaciIdeal");
                            Integer OdZutoIdeal = c.getInt("OdZutoIdeal");
                            Integer DoZutoIdeal = c.getInt("DoZutoIdeal");

                           /* HashMap<String, String> grupaPodataka = new HashMap<>();
                            grupaPodataka.put("ImeKulture", ImeKulture);
                            grupaPodataka.put("IdListaSenzora", String.valueOf(IdListaSenzora));
                            grupaPodataka.put("IdSenzorTip", String.valueOf(IdSenzorTip));
                            grupaPodataka.put("senzorTipIme", senzorTipIme);
                            grupaPodataka.put("OdPodaciIdeal", String.valueOf(OdPodaciIdeal));
                            grupaPodataka.put("DoPodaciIdeal", String.valueOf(DoPodaciIdeal));
                            grupaPodataka.put("OdZutoIdeal", String.valueOf(OdZutoIdeal));
                            grupaPodataka.put("DoZutoIdeal", String.valueOf(DoZutoIdeal));*/


                            ListaVarijabli lv = new ListaVarijabli();
                            lv.setImeKulture(ImeKulture);
                            lv.setIdListaSenzora(IdListaSenzora);
                            lv.setIdSenzorTip(IdSenzorTip);
                            lv.setSenzorTipIme(senzorTipIme);
                            lv.setOdPodaciIdeal(OdPodaciIdeal);
                            lv.setDoPodaciIdeal(DoPodaciIdeal);
                            lv.setOdZutoIdeal(OdZutoIdeal);
                            lv.setDoZutoIdeal(DoZutoIdeal);



                            jsonArrayIn = c.getJSONArray("podacizaSenzor");
                            for (int y = 0; y < jsonArrayIn.length(); y++) {

                                JSONObject m = jsonArrayIn.getJSONObject(y);
                                //String vremeSenzor = m.getString("vremeSenzor");

                                String vremeSenzor = m.getString("vremeSenzor");
                                /*DateFormat df = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
                                try {
                                    Date birthDate = df.parse(vremeSenzor);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }*/


                                Integer idSenzorIncr = m.getInt("idSenzorIncr");
                                Integer IdSenNotNotifikacija = m.getInt("IdSenNotNotifikacija");
                                float vrednostSenzor = Float.valueOf(m.getString("vrednostSenzor"));
                                String OpisNotifikacije = m.getString("OpisNotifikacije");

                                /*grupaPodataka.put("vremeSenzor",vremeSenzor);
                                grupaPodataka.put("idSenzorIncr", String.valueOf(idSenzorIncr));
                                grupaPodataka.put("vrednostSenzor", String.valueOf(vrednostSenzor));
                                grupaPodataka.put("OpisNotifikacije", OpisNotifikacije);
                                grupaPodataka.put("IdSenNotNotifikacija", String.valueOf(IdSenNotNotifikacija));*/

                                lv.setVremeSenzor(vremeSenzor);
                                lv.setIdSenzorIncr(idSenzorIncr);
                                lv.setVrednostSenzor(vrednostSenzor);
                                lv.setOpisNotifikacije(OpisNotifikacije);
                                lv.setIdSenNotNotifikacija(IdSenNotNotifikacija);



                            }

                            arraylist.add(lv);



                        }

                        // Get And Sent all data to ListView
                        callListView(arraylist);


                    } else {
                        // login error
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                // progressDialog.hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "getsensordetailactivity");
                params.put("tag", "getdensorbasicinfo");
                params.put("sensormac", sensorMAC);
                params.put("id", uid);
                params.put("kulturaid", String.valueOf(kulturaId));

                //Log.d("testmiki", String.valueOf(params));
                return params;
            }

        };

        //strReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        RequestQueue requestQueue = RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue();
        strReq.setTag("SensorDetailActivity");
        requestQueue.add(strReq);
    }

    private void callListView(ArrayList arraylist) {

        ListaVarijabli llv =  (ListaVarijabli) arraylist.get(0);
        if (arraylist.size() > 0) {

            if (llv.getVrednostSenzor() > 0) {
                adapter = new ViewAdapterSensorDetail(SensorDetailActivity.this, arraylist);
                listView.setAdapter(adapter);


                spinner.setVisibility(View.GONE);
            } else {
                Toast.makeText(getApplicationContext(), "Wating Sensor to sent data to server. There is no data from sensor", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),DrawerActivity.class);
                startActivity(intent);
            }


        } else {
            Toast.makeText(getApplicationContext(), "No data from sensor.", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_show_graph:
                showGraph();
                return true;
            case R.id.action_show_manual:
                showMaintenance();
                return true;
        }
        //return super.onOptionsItemSelected(item);
        super.onOptionsItemSelected(item);
       /* if(item.getItemId() == R.id.facebook){
            Toast.makeText(SensorDetailActivity.this, "Option pressed= facebook",Toast.LENGTH_LONG).show();
        }*/
        return true;
    }

}
