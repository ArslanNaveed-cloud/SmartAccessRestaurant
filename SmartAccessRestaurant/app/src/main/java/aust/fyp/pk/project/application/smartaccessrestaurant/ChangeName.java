package aust.fyp.pk.project.application.smartaccessrestaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

public class ChangeName extends AppCompatActivity {


    private TextInputEditText password,name,oldname;
    private Button save,cancel;
    private String oldname1="",newname="",password1="";
    private String username;
    private String userpassword;
    private int count = 0;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        password = findViewById(R.id.password);
        oldname = findViewById(R.id.nameholder);
        name = findViewById(R.id.name);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username","");
        userpassword  = sharedPreferences.getString("password","");
        email = sharedPreferences.getString("email","");
        oldname.setText(username);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == 0){
                    count++;
                    password1 = password.getText().toString().trim();
                    newname = name.getText().toString().trim();

                    if(password1.isEmpty() || password1.equals("")){
                        password.setError("Field Cannot be Empty");
                    }else if(newname.isEmpty() || newname.equals("")){
                        name.setError("Field Cannot be empty");
                    }else{
                        if(password1.equals(userpassword)){
                            if(newname.equals(username)){
                                name.setError("You Enter Old Name");
                                name.setFocusable(true);
                            }else{
                                Updatename();
                            }
                        }else{
                            password.setError("Passsword Doesnot Match Current Password");
                        }
                    }

                }else{
                    Toast.makeText(ChangeName.this, "Please Wait", Toast.LENGTH_SHORT).show();
                }




            }
        });
    }
    public void Updatename() {
        // Instantiate the RequestQueue.
        Toast.makeText(this, "Please Wait", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(ChangeName.this);

        String url = Urls.CHANGENAME;
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
                                Toast.makeText(ChangeName.this, "Please try again later", Toast.LENGTH_SHORT).show();

                            }

                            else if (status.equals("200")) {
                                count =0;
                                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                String username = reader.getString("username");

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                 editor.putString("username",username);
                                 editor.apply();
                                //Toast.makeText(context, "User Added", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ChangeName.this,DashBoard.class);
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

                Toast.makeText(ChangeName.this, "Please Wait...", Toast.LENGTH_SHORT).show();
                Log.d("aabbcc",""+error.getMessage());
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", email);
                params.put("name", newname);

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