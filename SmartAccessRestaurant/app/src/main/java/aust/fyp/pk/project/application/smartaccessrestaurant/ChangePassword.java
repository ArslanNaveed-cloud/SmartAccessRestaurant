package aust.fyp.pk.project.application.smartaccessrestaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {

    private TextInputEditText currentpassword,newpassword;
    private String cpass,npass,email1;
    private Button save,cancel;
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentpassword = findViewById(R.id.currentpassword);
        newpassword = findViewById(R.id.password);

        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        cpass = sharedPreferences.getString("password", "");
        email1 = sharedPreferences.getString("email", "");
        currentpassword.setText(cpass);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == 0) {


                    npass = newpassword.getText().toString().trim();
                    if (cpass.isEmpty() || cpass.equals("")) {
                        currentpassword.setError("Field Cannot Be Empty");
                    } else {
                        if (cpass.equals(npass)) {
                            newpassword.setError("Old Password");
                        } else {
                            UpdatePassword();
                        }

                    }
                } else {
                    Toast.makeText(ChangePassword.this, "Please Wait", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    public void UpdatePassword()
    {
        // Instantiate the RequestQueue.
        Toast.makeText(ChangePassword.this, "Please Wait", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(ChangePassword.this);

        String url = Urls.CHANGEPASSWORD;
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
                                Toast.makeText(ChangePassword.this, "Please try again later", Toast.LENGTH_SHORT).show();

                            }

                            else if (status.equals("200")) {
                                count =0;
                                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                String password = reader.getString("password");

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("password",password);
                                editor.apply();
                                //Toast.makeText(context, "User Added", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ChangePassword.this,DashBoard.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(ChangePassword.this, "Please Wait...", Toast.LENGTH_SHORT).show();
                Log.d("aabbcc",""+error.getMessage());
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", email1);
                params.put("newpassword", npass);

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

        }