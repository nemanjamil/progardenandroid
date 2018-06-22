package tech.progarden.world;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tech.progarden.world.app.AppConfig;
import tech.progarden.world.app.AppController;
import tech.progarden.world.app.SessionManager;

public class MainTenance extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup rg1;
    private  RadioButton  offwaterpump,onwaterpump,onwaterpumpwithout;
    private EditText onpumpminutes,offpumpminutes;
    TextView stateofpump;
    private String op1;
    private Button subbuttonpump;
    String SensorMAC = null;
    //Integer KulturaId = null;
    private LinearLayout maintell1,maintell2;

    ProgressDialog pd;

    private SessionManager session;
    String userID = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tenance);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            SensorMAC = extras.getString("SensorMAC"); // retrieve the data using keyName
            //KulturaId = extras.getInt("KulturaId");
        }

        pd = new ProgressDialog(MainTenance.this);
        pd.setMessage("loading");

        stateofpump = (TextView) findViewById(R.id.stateofpump);


        // Setting up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarmain);
        myToolbar.setTitle(R.string.title_maintanance);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);




        // RadioButtons and RadioGroup
        offwaterpump = (RadioButton) findViewById(R.id.offwaterpump);
        onwaterpump = (RadioButton) findViewById(R.id.onwaterpump);
        onwaterpumpwithout = (RadioButton) findViewById(R.id.onwaterpumpwithout);
        rg1 = (RadioGroup) findViewById(R.id.myRadioGroup);
        rg1.setOnCheckedChangeListener(this);


        // Submit Button
        subbuttonpump = findViewById(R.id.buttonpump);
        subbuttonpump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setTitle("ProgressDialog bar example");
                pd.show();
                int radioButtonID = rg1.getCheckedRadioButtonId();
                View radioButton = rg1.findViewById(radioButtonID);
                int idx = rg1.indexOfChild(radioButton);

                submitPumpChanges(idx);
            }
        });

        // EDIT TEXT - MINUTES
        onpumpminutes = (EditText) findViewById(R.id.onpumpminutes);
        offpumpminutes = (EditText) findViewById(R.id.offpumpminutes);
        //actv(false);

        // LINEARLAYOUT
        maintell1 = (LinearLayout) findViewById(R.id.maintell1);
        maintell2 = (LinearLayout) findViewById(R.id.maintell2);
        hideShowEditBox(0);


        session = new SessionManager(getApplicationContext());
        userID = session.getUID();
        getPumpStatus();

        // LIMIT VALUES MAX AND MIN
        onpumpminutes.setFilters(new InputFilter[]{ new MinMaxFilter("0", "1439")});
        offpumpminutes.setFilters(new InputFilter[]{ new MinMaxFilter("0", "1439")});


    }

    private void hideShowEditBox(int i) {
        if (i==1) {
            maintell1.setVisibility(View.VISIBLE);
            maintell2.setVisibility(View.VISIBLE);
        } else {
            maintell1.setVisibility(View.GONE);
            maintell2.setVisibility(View.GONE);
        }
    }

    private void submitPumpChanges(final int idx) {

        String tag_string_req = "submitPumpChanges";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CHANGEPUMPSTATUS_POST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {

                        if (pd.isShowing())
                            pd.dismiss();


                    } else {
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                // progressDialog.hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "changepumpstatus");
                params.put("sensormac", SensorMAC);
                params.put("userid", userID);
                params.put("pumpstate", String.valueOf(idx)); // dobijam iz radio button
                params.put("pumpontime", String.valueOf(onpumpminutes.getText())); // koliko minuta je ukljuceno
                params.put("pumpofftime", String.valueOf(offpumpminutes.getText())); // koliko minuta je iskljuceno

                Log.d("testmiki", String.valueOf(params));
                return params;
            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));

        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
    private void getPumpStatus() {

        String tag_string_req = "pumpcall";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETPUMPSTATUS_POST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {

                        String error_msg = jsonObject.getString("error_msg");
                        int StanjePumpe = jsonObject.getInt("StanjePumpe");
                        int VremePumpeUklj = jsonObject.getInt("VremePumpeUklj");
                        int VremePumpeIskl = jsonObject.getInt("VremePumpeIskl");
                        int StanjePumpeOnOff = jsonObject.getInt("StanjePumpeOnOff");

                        String stanjePumpeStr = "";

                        if (StanjePumpeOnOff == 1) {
                            stanjePumpeStr = "ON";
                        } else {
                            stanjePumpeStr = "OFF";
                        }


                        if (StanjePumpe==0){
                            offwaterpump.setChecked(true);

                        } else if (StanjePumpe==1) {
                            onwaterpump.setChecked(true);
                            actv(true);
                            setMinutesOnEdit(VremePumpeUklj,VremePumpeIskl);
                        } else if (StanjePumpe==2) {
                            onwaterpumpwithout.setChecked(true);
                        }


                        stateofpump.setText("State of pump : "+stanjePumpeStr);

                    } else {
                       String errorMsg = jsonObject.getString("error_msg");
                       Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                // progressDialog.hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "getpumpstatus");
                params.put("sensormac", SensorMAC);
                params.put("userid", userID);
                return params;
            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));

        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void setMinutesOnEdit(int vremePumpeUklj, int vremePumpeIskl) {
        onpumpminutes.setText(String.valueOf(vremePumpeUklj));
        offpumpminutes.setText(String.valueOf(vremePumpeIskl));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maintanace, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch(checkedId)
        {
            case R.id.onwaterpump:
                hideShowEditBox(1);
                break;

            case R.id.offwaterpump:
                hideShowEditBox(0);
                break;

            case R.id.onwaterpumpwithout:
                hideShowEditBox(0);
                break;
        }

        RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
        boolean isChecked = checkedRadioButton.isChecked();
        if (isChecked)
        {
            Log.d("testmiki rb", String.valueOf(checkedRadioButton.getText()));
        }
    }

    // https://stackoverflow.com/questions/27602117/enable-edit-box-only-after-selecting-the-radio-button
    private void actv(final boolean active)
    {
        offpumpminutes.setEnabled(active);
        onpumpminutes.setEnabled(active);
        if (active)
        {
            offpumpminutes.requestFocus();
            onpumpminutes.requestFocus();
        }
    }

    // https://capdroidandroid.wordpress.com/2016/04/07/set-minimum-maximum-value-in-edittext-android/
    public class MinMaxFilter implements InputFilter {

        private int mIntMin, mIntMax;

        public MinMaxFilter(int minValue, int maxValue) {
            this.mIntMin = minValue;
            this.mIntMax = maxValue;
        }

        public MinMaxFilter(String minValue, String maxValue) {
            this.mIntMin = Integer.parseInt(minValue);
            this.mIntMax = Integer.parseInt(maxValue);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(mIntMin, mIntMax, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
