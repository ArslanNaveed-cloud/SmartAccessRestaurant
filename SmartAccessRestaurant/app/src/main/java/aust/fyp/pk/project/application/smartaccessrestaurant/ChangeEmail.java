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

public class ChangeEmail extends AppCompatActivity {

    private TextInputEditText password,email,oldemail;
    private Button save,cancel;
    private String oldemail1="",newemail1="",password1="";
    private String username;
    private String userpassword;
    private int count = 0;
    private String email1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        password = findViewById(R.id.password);
        oldemail = findViewById(R.id.emailholder);
        email = findViewById(R.id.email);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userpassword  = sharedPreferences.getString("password","");
        email1 = sharedPreferences.getString("email","");
        oldemail.setText(email1);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == 0){
                    count++;
                    password1 = password.getText().toString().trim();
                    newemail1 = email.getText().toString().trim();

                    if(password1.isEmpty() || password1.equals("")){
                        password.setError("Field Cannot be Empty");
                    }else if(newemail1.isEmpty() || newemail1.equals("")){
                        email.setError("Field Cannot be empty");
                    }else{
                        if(password1.equals(userpassword)){
                            if(newemail1.equals(email1)){
                                email.setError("You Enter Old Name");
                                email.setFocusable(true);
                            }else{
                                UpdateEmail();
                            }
                        }else{
                            password.setError("Passsword Doesnot Match Current Password");
                        }
                    }

                }else{
                    Toast.makeText(ChangeEmail.this, "Please Wait", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
    public void UpdateEmail() {
        // Instantiate the RequestQueue.
        Toast.makeText(this, "Please Wait", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(ChangeEmail.this);

        String url = Urls.CHANGEEMAIL;
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
                                Toast.makeText(ChangeEmail.this, "Please try again later", Toast.LENGTH_SHORT).show();

                            }

                            else if (status.equals("200")) {
                                count =0;
                                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                String email = reader.getString("email");

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email",email);
                                editor.apply();
                                //Toast.makeText(context, "User Added", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ChangeEmail.this,DashBoard.class);
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

                Toast.makeText(ChangeEmail.this, "Please Wait...", Toast.LENGTH_SHORT).show();
                Log.d("aabbcc",""+error.getMessage());
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", email1);
                params.put("newemail", newemail1);

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