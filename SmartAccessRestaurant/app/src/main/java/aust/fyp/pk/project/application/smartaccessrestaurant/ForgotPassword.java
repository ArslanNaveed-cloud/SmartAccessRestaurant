package aust.fyp.pk.project.application.smartaccessrestaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import android.view.View;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {

    private TextInputEditText email;
    private String EMAIL="";
    private Button send;
    private int count=0;
    private TextView signupuser;
    private boolean isError=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.email);
        send = findViewById(R.id.send_btn);
        signupuser = findViewById(R.id.signupuser);
        signupuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this,SignupScreen.class);
                startActivity(intent);

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isConnected(ForgotPassword.this)){
                    buildDialog2(ForgotPassword.this,"Error","Please Check Your Internet Connection").show();
                }else{
                    if(count==0){
                        EMAIL = email.getText().toString().trim();
                        if(EMAIL.isEmpty() || EMAIL.equals("")){
                            email.setError("Field cannot be empty");
                            email.setFocusable(true);
                            isError=true;
                        }else if(!(Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches())){
                            email.setError("Incorrect email");
                            isError = true;
                            email.setFocusable(true);
                        }
                        if(!isError){
                            SendPassword();
                        }
                    }else{
                        Toast.makeText(ForgotPassword.this, "Please Wait", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    public void SendPassword()
    {
        // Instantiate the RequestQueue.
        Toast.makeText(this, "Please Wait", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(ForgotPassword.this);

        String url = Urls.FORGOTPASSWORD;
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
                                Toast.makeText(ForgotPassword.this, "Please Try Again", Toast.LENGTH_SHORT).show();

                            }

                            else if (status.equals("200")) {
                                count =0;
                               buildDialog2(ForgotPassword.this,"Congratulations","Password Changed Successfully").show();
                                //Toast.makeText(context, "User Added", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(ForgotPassword.this, "Please Try Again Later", Toast.LENGTH_SHORT).show();
                Log.d("aabbcc",""+error.getMessage());
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", EMAIL);

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

                Intent intent = new Intent(ForgotPassword.this,LoginnScreen.class);
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
    public AlertDialog.Builder buildDialog2(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);
        waitforsometime();

        return builder;
    }

}