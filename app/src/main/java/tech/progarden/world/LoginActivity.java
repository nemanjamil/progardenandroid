package tech.progarden.world;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tech.progarden.world.app.AppConfig;
import tech.progarden.world.app.AppController;
import tech.progarden.world.app.RequestQueueSingleton;
import tech.progarden.world.app.SessionManager;
import tech.progarden.world.dialogs.ProgressDialogCustom;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button registerHere;
    Button signIn;
    TextInputLayout emailLogin;
    TextInputLayout passwordLogin;
    EditText etEmailLogin;
    EditText etPasswordLogin;

    private ProgressDialogCustom progressDialog;
    private SessionManager session;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initializing toolbar
//        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolBar);
        //initializing views
        registerHere = (Button) findViewById(R.id.registerhere_button);
        signIn = (Button) findViewById(R.id.signin_button);
        emailLogin = (TextInputLayout) findViewById(R.id.email_loginlayout);
        passwordLogin = (TextInputLayout) findViewById(R.id.password_loginlayout);
        etEmailLogin = (EditText) findViewById(R.id.email_login);
        etPasswordLogin = (EditText) findViewById(R.id.password_login);
        //setting onclick listeners
        registerHere.setOnClickListener(this);
        signIn.setOnClickListener(this);

        //setting progressDialog
        progressDialog = new ProgressDialogCustom(this);
        progressDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());

        AppConfig.logInfo("PrefName:", session.getName());

        //If the session is logged in move to MainActivity
        if (session.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
            startActivity(intent);
            finish();
        }

        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

    }

    /**
     * function to verify login details
     */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        progressDialog.showDialog("Povezivanje...");

//        String url = String.format(AppConfig.URL_LOGIN_GET, "login", email, password);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN_POST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);
                    Log.d("Response 1 " ,jObj.toString());


                    //String userId= jObj.getString("uid");

                    boolean success = jObj.getBoolean("success");

                    if (success) {
                        // user successfully logged in
                        // Create login session
                        String uid = jObj.getString("uid");

                        session.setLogin(true);
                        session.setUID(uid);

                        // collect user data
                        JSONObject jObj_user_data = jObj.getJSONObject("user");
                        //session.setCity("Gamzigrad");

                        if (!jObj_user_data.isNull("KomitentNaziv"))
                            session.setGeneralName(jObj_user_data.getString("KomitentNaziv"));
                        if (!jObj_user_data.isNull("KomitentIme"))
                            session.setName(jObj_user_data.getString("KomitentIme"));
                        if (!jObj_user_data.isNull("KomitentPrezime"))
                            session.setLastName(jObj_user_data.getString("KomitentPrezime"));
                        if (!jObj_user_data.isNull("KomitentAdresa"))
                            session.setAddress(jObj_user_data.getString("KomitentAdresa"));
                        if (!jObj_user_data.isNull("KomitentPosBroj"))
                            session.setZip(jObj_user_data.getString("KomitentPosBroj"));
                        if (!jObj_user_data.isNull("KomitentMesto"))
                            session.setCity(jObj_user_data.getString("KomitentMesto"));
                        if (!jObj_user_data.isNull("KomitentTelefon"))
                            session.setPhone(jObj_user_data.getString("KomitentTelefon"));
                        if (!jObj_user_data.isNull("KomitentMobTel"))
                            session.setMobile(jObj_user_data.getString("KomitentMobTel"));
                        if (!jObj_user_data.isNull("KomitentEmail"))
                            session.setEmail(jObj_user_data.getString("KomitentEmail"));
                        if (!jObj_user_data.isNull("KomitentUserName"))
                            session.setUsername(jObj_user_data.getString("KomitentUserName"));
                        if (!jObj_user_data.isNull("KomitentTipUsera"))
                            session.setUserType(jObj_user_data.getInt("KomitentTipUsera"));
                        if (!jObj_user_data.isNull("KomitentFirma"))
                            session.setFirmName(jObj_user_data.getString("KomitentFirma"));
                        if (!jObj_user_data.isNull("KomitentMatBr"))
                            session.setFirmId(jObj_user_data.getString("KomitentMatBr"));
                        if (!jObj_user_data.isNull("KomitentPIB"))
                            session.setFirmPIB(jObj_user_data.getString("KomitentPIB"));
                        if (!jObj_user_data.isNull("KomitentFirmaAdresa"))
                            session.setFirmAddress(jObj_user_data.getString("KomitentFirmaAdresa"));

                        // Launching  main activity
                        Intent intent = new Intent(LoginActivity.this,
                                DrawerActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // login error
                        String errorMsg = jObj.getString("error_msg");
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
                progressDialog.hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                //params.put("tag", "login");
                //params.put("email", email);
                //params.put("password", password);

                params.put("action", "povuciPodatkeAndroidKorisnik");
                params.put("tag", "login");
                params.put("email", email);
                params.put("p", password);
                return params;
            }

        };

        //strReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));
        // Adding request to  queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        strReq.setTag("LoginActivity");
        requestQueue.add(strReq);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //on clicking register button move to Register Activity
            case R.id.registerhere_button:
                Intent intent = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(intent);
                break;

            //on clicking the signin button check for the empty field then call the checkLogin() function
            case R.id.signin_button:
                String email = etEmailLogin.getText().toString();
                String password = etPasswordLogin.getText().toString();

                // Check for empty data
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    // login user
                    checkLogin(email, password);
                    //checkLoginPost(email, password);
                } else {
                    // show snackbar to enter credentials
                    Snackbar.make(v, "Unesite podatke!", Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
        }
    }

    private void checkLoginPost(final String email, final String password) {



        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN_POST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean success = jObj.getBoolean("success");
                            Log.d("Response " ,jObj.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("action", "povuciPodatkeAndroidKorisnik");
                params.put("tag", "login");
                params.put("email", email);
                params.put("p", password);
                return params;
            }
        };
        jsonObjectRequest.setTag("miki");
        requestQueue.add(jsonObjectRequest);


    }


}
