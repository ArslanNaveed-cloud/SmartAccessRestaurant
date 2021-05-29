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
import android.widget.Toast;

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
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.provider.Telephony.Carriers.PASSWORD;

public class SignupScreen extends AppCompatActivity {

    private Button signup;
    private TextInputEditText email,name,password,phone,repassword;
    String EMAIL,NAME,PASSWORD,REPASSWORD,PHONE="";
    private boolean isError;
    private ProgressDialog dialog;
    private TextView signintxt;
    private LinearLayout loader,mainlayout;
    CountryCodePicker ccp;
    int count =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loader = findViewById(R.id.loader);
        loader.setVisibility(View.GONE);
        mainlayout = findViewById(R.id.mainlayout);
        email = findViewById(R.id.email);
        name = findViewById(R.id.fullname);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.repassword);
        signup = findViewById(R.id.signup_btn);
        phone = findViewById(R.id.phone);
        ccp = findViewById(R.id.ccp);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count !=0){
                    Toast.makeText(SignupScreen.this, "Please Wait", Toast.LENGTH_SHORT).show();
                }else{
                    count++;
                    if(!isConnected(SignupScreen.this)) {
                        buildDialog(SignupScreen.this,"We are sorry","Please Check Your Internet Connection.").show();

                    }else{
                        checkContent();
                    }
                }

                }
        });
        signintxt = findViewById(R.id.signintxt);
        signintxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupScreen.this,LoginnScreen.class);
                startActivity(intent);

            }
        });
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
    private void checkContent() {
        EMAIL = email.getText().toString().trim();
        NAME = name.getText().toString().trim();
        PASSWORD = password.getText().toString().trim();
        REPASSWORD = repassword.getText().toString().trim();
        PHONE = phone.getText().toString().trim();

        if(EMAIL.isEmpty() || EMAIL.equals("")){
            email.setError("Field cannot be empty");
            isError=true;
            email.setFocusable(true);
        }else if(!(Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches())){
            email.setError("Incorrect email");
            isError = true;
            email.setFocusable(true);
        }else if(NAME.isEmpty() || NAME.equals("")){
            name.setError("Field cannot be empty");
            isError = true;
            name.setFocusable(true);
        }else if(PASSWORD.isEmpty() || PASSWORD.equals("")){
            password.setError("Field cannot be empty");
            isError = true;
            password.setFocusable(true);
        }else if(PHONE.isEmpty() || PHONE.equals("")){
            phone.setError("Field cannot be empty");
            isError = true;
            phone.setFocusable(true);
        }else if(
                PHONE.length()<11){
            phone.setError("Incorrect Phone Number");
            isError = true;
            phone.setFocusable(true);
        }

        else if(PASSWORD.length()<8){
            password.setError("Must be atleast 8 characters");
            isError = true;
            password.setFocusable(true);
        }else if(REPASSWORD.isEmpty() || REPASSWORD.equals("")){
            repassword.setError("Field cannot be empty");
            isError = true;
            repassword.setFocusable(true);
        }else if(REPASSWORD.length()<8){
            repassword.setError("Must be atleast 8 characters");
            isError = true;
            repassword.setFocusable(true);
        }else if(!PASSWORD.equals(REPASSWORD)){
            repassword.setError("Password Donot Match");
            isError = true;
            password.setFocusable(true);

        }else{
            isError = false;
        }

        if(!isError){
            loader.setVisibility(View.VISIBLE);
            PHONE = ccp.getSelectedCountryCode()+""+PHONE;
           RegisterUser();
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
    public AlertDialog.Builder buildDialog(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                loader.setVisibility(View.GONE);

            }
        });

        return builder;
    }
    public AlertDialog.Builder buildDialog2(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);
        waitforsometime();

        return builder;
    }
    public void RegisterUser() {
        // Instantiate the RequestQueue.
        Snackbar snackbar = Snackbar
                .make(mainlayout, "Please Wait While We Register Your Account", Snackbar.LENGTH_LONG);
        snackbar.show();
        RequestQueue queue = Volley.newRequestQueue(SignupScreen.this);

        String url = Urls.SIGNUP_ACCOUNT;
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
                                count =0;
                                Snackbar snackbar = Snackbar
                                        .make(mainlayout, "Error.!\nPlease Try Again Later", Snackbar.LENGTH_LONG);
                                snackbar.show();

                            }else if(status.equals("409")){
                                count =0;
                                buildDialog(SignupScreen.this,"Error","User Already Exists").show();
                            }

                            else if (status.equals("200")) {
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
                                buildDialog2(SignupScreen.this,"Congratulations","Your Account is Created").show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                params.put("name", NAME);
                params.put("email", EMAIL);
                params.put("password", PASSWORD);
                params.put("phone",PHONE);
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
               Intent intent = new Intent(SignupScreen.this,DashBoard.class);
               intent.putExtra("identifier","signupscreen");
               startActivity(intent);
                finishAffinity();
            }
        }, 1500);
    }
}