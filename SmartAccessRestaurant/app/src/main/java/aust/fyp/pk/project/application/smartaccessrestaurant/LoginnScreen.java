package aust.fyp.pk.project.application.smartaccessrestaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginnScreen extends AppCompatActivity {

    private Button login;
    private TextInputEditText email,password;
   private String EMAIL,PASSWORD;
    private ProgressDialog dialog;
    private TextView signuptxt,forgotpass;
   private boolean isError;
   private LinearLayout mainlayout,loader;
   private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginn_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainlayout = findViewById(R.id.mainlayout);
        loader = findViewById(R.id.loader);
        loader.setVisibility(View.GONE);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        forgotpass = findViewById(R.id.forgotpass);
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginnScreen.this,ForgotPassword.class);
                startActivity(intent);

            }
        });
        signuptxt = findViewById(R.id.signuptxt);
        signuptxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginnScreen.this,SignupScreen.class);
                startActivity(intent);
            }
        });
        login = findViewById(R.id.signin_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected(LoginnScreen.this)){
                    buildDialog(LoginnScreen.this,"Error","Please Check Your Internet Connection And Try Again");
                }else{
                    chechContent();

                }
                }
        });

    }
    private void chechContent() {
        EMAIL = email.getText().toString().trim();
        PASSWORD = password.getText().toString().trim();
        if(EMAIL.isEmpty() || EMAIL.equals("")){
            email.setError("Field cannot be empty");
            email.setFocusable(true);
            isError=true;
        }else if(!(Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches())){
            email.setError("Incorrect email");
            isError = true;
            email.setFocusable(true);
        }else if(PASSWORD.isEmpty() || PASSWORD.equals("")){
            password.setError("Field cannot be empty");
            isError = true;
            password.setFocusable(true);
        }else if(PASSWORD.length()<8){
            password.setError("Must be atleast 8 characters");
            isError = true;
            password.setFocusable(true);
        }else{
            isError = false;
        }if(!isError){
           loader.setVisibility(View.VISIBLE);
            AuthenticateUser();

        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                break;
        }


        return true;
    }

    public AlertDialog.Builder buildDialog2(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);
        waitforsometime();

        return builder;
    }
    public AlertDialog.Builder buildDialog(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder;
    }
    public void AuthenticateUser() {
        // Instantiate the RequestQueue.
        Snackbar snackbar = Snackbar
                .make(mainlayout, "Please Wait While We Check Your Account", Snackbar.LENGTH_LONG);
        snackbar.show();
        RequestQueue queue = Volley.newRequestQueue(LoginnScreen.this);

        String url = Urls.LOGIN;
        Log.i("112233", url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                loader.setVisibility(View.GONE);
                                count =0;
                                Snackbar snackbar = Snackbar
                                        .make(mainlayout, "Error.!\nPlease Try Again Later", Snackbar.LENGTH_LONG);
                                snackbar.show();

                            }else if(status.equals("404")){
                                loader.setVisibility(View.GONE);
                                count =0;
                                buildDialog(LoginnScreen.this,"Error","Email/Password is wrong").show();
                            }

                            else if (status.equals("200")) {
                                loader.setVisibility(View.GONE);
                                count =0;
                                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                String username = reader.getString("username");
                                String phone = reader.getString("phone");
                                String email = reader.getString("email");
                                String password = reader.getString("password");

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isuserloggedin",true);
                                editor.putString("username",username);
                                editor.putString("phone",phone);
                                editor.putString("email",email);
                                editor.putString("password",password);
                                editor.apply();
                                //Toast.makeText(context, "User Added", Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(LoginnScreen.this,DashBoard.class);
                               startActivity(intent);
                               finishAffinity();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.setVisibility(View.GONE);

                Snackbar snackbar = Snackbar
                        .make(mainlayout, error.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                Log.d("aabbcc",""+error.getMessage());
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", EMAIL);
                params.put("password", PASSWORD);

                return params;
            }
        };
        // Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }


    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                loader.setVisibility(View.GONE);
                Intent intent = new Intent(LoginnScreen.this,DashBoard.class);
                intent.putExtra("identifier","signupscreen");
                startActivity(intent);
                finish();
            }
        }, 1500);
    }
    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}