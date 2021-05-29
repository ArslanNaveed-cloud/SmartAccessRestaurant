package aust.fyp.pk.project.application.smartaccessrestaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.carteasy.v1.lib.Carteasy;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FoodItemsList extends AppCompatActivity {

    private Intent intent;
    private FoodItemAdaphter adaphter;
    String hotelname,category,tablenumber;
    private String paktitle,pakprice,pakdescription,pakcoveriamge;
    ArrayList<FoodDataModel> pakistaniFoodDataModels;
    private LinearLayout loader;
    LinearLayout mycart;
    Carteasy cs = new Carteasy();
    Button btn;
    private OrderDataModel cartDataModels;
    private ArrayList<OrderDataModel> arrayList;
    private ArrayList<String> arrayList2;
    int count = 0;
    private boolean isuserloggedin;
    private String username="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_items_list);
        pakistaniFoodDataModels = new ArrayList<>();
        loader = findViewById(R.id.loader);
         arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        mycart = findViewById(R.id.mycart);
        mycart.setVisibility(View.GONE);

        cs = new Carteasy();
        cs.persistData(FoodItemsList.this, false);
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        isuserloggedin =  sharedPreferences.getBoolean("isuserloggedin",false);
        username = sharedPreferences.getString("username","");

        btn = findViewById(R.id.btn);
        if(!isConnected(this)){
            buildDialog3(this,"Error","Please Check Your Internet Connection");
        }else{
            intent = getIntent();

            hotelname = sharedPreferences.getString("hotelname","");
            tablenumber = sharedPreferences.getString("tablenumber","");
            category = intent.getStringExtra("category");
            tablenumber = intent.getStringExtra("tablenumber");
            GetFoods();
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(count ==0){
                        buildDialog(FoodItemsList.this,"Are You Sure.?","Do You Want To Confirm Following Order").show();
                        count++;
                    }else{
                        Toast.makeText(FoodItemsList.this, "Please Wait", Toast.LENGTH_SHORT).show();
                    }
                    }
            });
        }

    }
    private void GetFoods()
    {
        final RequestQueue queue = Volley.newRequestQueue(this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.GET_FOODPRODUCTS;
        Log.d("112233","Url Setting");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("112233","Url Setting"+response);
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                loader.setVisibility(View.GONE);
                                buildDialog3(FoodItemsList.this, "500", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                loader.setVisibility(View.GONE);
                                Toast.makeText(FoodItemsList.this, "We Don't Have Foods For This Category", Toast.LENGTH_SHORT).show();
                                waitforsometime();
                            } else if (status.equals("200")) {
                                loader.setVisibility(View.GONE);
                                //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();
                                JSONArray title = reader.getJSONArray("title");
                                JSONArray description = reader.getJSONArray("description");
                                JSONArray coverimage = reader.getJSONArray("coverimage");
                                JSONArray price = reader.getJSONArray("price");
                                pakistaniFoodDataModels.clear();

                                for(int i = 0;i < title.length();i++){
                                    paktitle = title.getString(i);
                                    pakdescription = description.getString(i);
                                    pakcoveriamge = coverimage.getString(i);
                                    pakprice = price.getString(i);

                                    FoodDataModel ratingDataModel = new FoodDataModel(paktitle,pakprice,pakdescription,pakcoveriamge);
                                    pakistaniFoodDataModels.add(ratingDataModel);
                                }

                                adaphter = new FoodItemAdaphter(FoodItemsList.this,pakistaniFoodDataModels);
                                RecyclerView fooditemlist = findViewById(R.id.fooditems);
                                // laborlist.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                fooditemlist.setLayoutManager(new LinearLayoutManager(FoodItemsList.this));
                                fooditemlist.setAdapter(adaphter);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Collection Fragment", error.toString());
                error.printStackTrace();
                loader.setVisibility(View.GONE);
                buildDialog3(FoodItemsList.this, "Oops..!", "Please try again later").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("hotelname",hotelname);
                params.put("category",category);

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
    public AlertDialog.Builder buildDialog3(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        waitforsometime();
        return builder;
    }
    public AlertDialog.Builder buildDialog4(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        waitforsometime3();
        return builder;
    }

    private void waitforsometime3() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Intent intent = new Intent(FoodItemsList.this,DashBoard.class);
                intent.putExtra("identifier","fooditemlist");
                intent.putExtra("hotelname",hotelname);
                intent.putExtra("tablenumber",tablenumber);
                startActivity(intent);
                finish();
            }
        }, 3000);
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
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                loader.setVisibility(View.GONE);
                finish();
            }
        }, 1000);
    }

    public void waitforsometime2(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                loader.setVisibility(View.VISIBLE);
                 PlaceOrder();

            }
        }, 2000);
    }
    public AlertDialog.Builder buildDialog(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<Integer, Map> data;
                Carteasy cs = new Carteasy();
                data = cs.ViewAll(FoodItemsList.this);

                for (Map.Entry<Integer, Map> entry : data.entrySet()) {
                    //get the Id
                    Log.d("Key: ", String.valueOf(entry.getKey()));
                    Log.d("Value: ", String.valueOf(entry.getValue()));
                    String row= new Gson().toJson(entry.getValue());
                    arrayList2.add(row);
                    //Get the items tied to the Id
                }

                waitforsometime2();

            }
        });

        return builder;
    }
    private void PlaceOrder()
    {
        final RequestQueue queue = Volley.newRequestQueue(this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.PLACE_ORDER;
        Log.d("112233","Url Setting");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("112233","Url Setting"+response);
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                loader.setVisibility(View.GONE);
                                buildDialog3(FoodItemsList.this, "500", "Internal Server Error").show();
                            } else if (status.equals("200")) {
                                loader.setVisibility(View.GONE);
                                cs.clearCart(FoodItemsList.this);
                                arrayList2.clear();
                               adaphter.bill = 0;
                                cs.clearPreviousData(FoodItemsList.this);
                                buildDialog4(FoodItemsList.this, "Order Placed Successfully", "See My Orders Option To See The Status Of Your Order").show();


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Collection Fragment", error.toString());
                error.printStackTrace();
                loader.setVisibility(View.GONE);
                buildDialog3(FoodItemsList.this, "Oops..!", "Please try again later").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String data = new Gson().toJson(arrayList2);
                Map<String, String> params = new HashMap<>();
                params.put("hotelname",hotelname);
                params.put("username",username);
                params.put("tablenumber",tablenumber);
                params.put("data",data);
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