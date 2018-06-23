package tech.progarden.world;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import tech.progarden.world.app.AppConfig;
import tech.progarden.world.app.AppController;
import tech.progarden.world.app.RequestQueueSingleton;
import tech.progarden.world.app.SessionManager;
import tech.progarden.world.dialogs.ProgressDialogCustom;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvLogin;
    TextInputLayout fullName;
    TextInputLayout emailRegister;
    TextInputLayout passwordRegister;
    EditText etFullName;
    EditText etLastName;
    EditText etEmailRegister;
    EditText etPasswordRegister;
    Button registerButton;

    SessionManager session;

    private ProgressDialogCustom pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initializing Views
        registerButton = (Button) findViewById(R.id.register_button);
        fullName = (TextInputLayout) findViewById(R.id.fullname_registerlayout);
        emailRegister = (TextInputLayout) findViewById(R.id.email_registerlayout);
        passwordRegister = (TextInputLayout) findViewById(R.id.password_registerlayout);
        etFullName = (EditText) findViewById(R.id.fullname_register);
        etLastName = (EditText) findViewById(R.id.lastname_register);
        etEmailRegister = (EditText) findViewById(R.id.email_register);
        etPasswordRegister = (EditText) findViewById(R.id.password_register);
        tvLogin = (TextView) findViewById(R.id.tv_signin);

        //setting toolbar
//        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolBar);

        tvLogin.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        // Progress dialog
        pDialog = new ProgressDialogCustom(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());


        // Check if user is already logged in
        if (session.isLoggedIn()) {
            // User is already logged in. Move to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    DrawerActivity.class);
            startActivity(intent);
            finish();
        }

    }

    /*
    function to register user details in mysql database
     */
    private void registerUser(final String name, final String last_name, final String email,
                              final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.showDialog("Registracija...");
        String url = "";
        try {
            //String email_enc = URLEncoder.encode(email, "utf-8");
            //String pass_enc = URLEncoder.encode(password, "utf-8");
            String name_enc = URLEncoder.encode(name, "utf-8");
            String last_name_enc = URLEncoder.encode(last_name, "utf-8");
            url = String.format(AppConfig.URL_REGISTER_GET, "register", email, password, name_enc, last_name_enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                pDialog.hideDialog();

                try {
                    AppConfig.logInfo("REGISTER_URL", response);
                    JSONObject jObj = new JSONObject(response);
//{"error_msg":"","tag":"register","error":false,"uid":33,"user":{"KomitentIme":"Milan","KomitentPrezime":"Milan","KomitentUserName":"a","email":"a@b.com","created_at":"2016-01-14 19:03:33"}} */

                    boolean success = jObj.getBoolean("success");
                    if (success) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("KomitentIme");
                        String last_name = user.getString("KomitentPrezime");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");

                        /* TODO Remove storing user data at register*/
                        //I don't think storing this is usefull*/
                        AppController.setString(RegisterActivity.this, "uid", uid);
                        AppController.setString(RegisterActivity.this, "name", name);
                        AppController.setString(RegisterActivity.this, "last_name", last_name);
                        AppController.setString(RegisterActivity.this, "email", email);
                        AppController.setString(RegisterActivity.this, "created_at", created_at);


                        AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                        alertDialog.setTitle("Registracija novog korisnika");
                        alertDialog.setMessage("Registracija uspešna!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Launch login activity
                                        Intent intent = new Intent(
                                                RegisterActivity.this,
                                                LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        if (errorMsg.equals("")) errorMsg = "Greška u komunikaciji sa serverom!";
                        //Toast.makeText(getApplicationContext(),
                        //        errorMsg, Toast.LENGTH_LONG).show();
                        showSnack(errorMsg);
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
                pDialog.hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "registrujAndroid");
                params.put("tag", "register");
                params.put("komitentime", name);
                params.put("komitentprezime", last_name);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        //strReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        //AppConfig.logInfo("REGISTER_URL", strReq.getUrl());

        RequestQueue requestQueue = RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue();
        strReq.setTag("RegisterActivity");
        requestQueue.add(strReq);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_signin:
                Intent intent = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(intent);
                finish();
            case R.id.register_button:
                String name = etFullName.getText().toString();
                String last_name = etLastName.getText().toString();
                String email = etEmailRegister.getText().toString();
                String password = etPasswordRegister.getText().toString();
/* TODO  Make better filed validation. Name can't contain spaces and/or UTF-8 letters.*/
                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !last_name.isEmpty()) {
                    registerUser(name, last_name, email, password);
                } else {
                    showSnack("Unesite podatke!");
                }
                break;
        }
    }

    private void showSnack(String msg) {
        Snackbar.make(registerButton, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

}
