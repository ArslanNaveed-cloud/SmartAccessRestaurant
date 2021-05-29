package aust.fyp.pk.project.application.smartaccessrestaurant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class MyOrders extends Fragment {

    private View view;
    private LinearLayout wrapper;
    private boolean isuserloggedin;
    private String username;
    private LinearLayout loader;
    private ArrayList<OrderDataModel> orderDataModels;
    private Button createaccount;
    private TextView message;
    private ArrayList<OrderDataModel> arrayList;
    private OrderAdaphter adaphter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_orders, container, false);
        wrapper = view.findViewById(R.id.wrapper);
       arrayList = new ArrayList<>();
        loader = view.findViewById(R.id.loader);
        loader.setVisibility(View.GONE);
        message = view.findViewById(R.id.message);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        orderDataModels = new ArrayList<>();
        createaccount = view.findViewById(R.id.createaccount);
        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SignupScreen.class);
                startActivity(intent);

            }
        });
        isuserloggedin =  sharedPreferences.getBoolean("isuserloggedin",false);
             createaccount.setVisibility(View.GONE);
            message.setText("Checking Your Orders..");
            username = sharedPreferences.getString("username","");
            loader.setVisibility(View.VISIBLE);
            getOrders();

        return view;
    }

    private void getOrders() {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.GET_ORDERS;
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
                                buildDialog3(getActivity(), "500", "Internal Server Error").show();
                            }else if (status.equals("200")) {
                                loader.setVisibility(View.GONE);
                                wrapper.setVisibility(View.GONE);
                                //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();
                                JSONArray title = reader.getJSONArray("fooditems");
                                JSONArray orderhotelname = reader.getJSONArray("hotelname");
                                JSONArray orderhoteltable = reader.getJSONArray("tablenumber");
                                JSONArray orderquantity = reader.getJSONArray("productquantity");
                                JSONArray orderbill = reader.getJSONArray("totalbill");
                                JSONArray orderdate = reader.getJSONArray("date");
                                JSONArray orderstatus = reader.getJSONArray("orderstatus");
                                JSONObject jsonObject=null;
                                JSONArray TitlejsonArray;
                                JSONArray QuantityjsonArray;
                                String foodnames="",foodquantity="",totalbill="",date="",tablenumber="",mystatus="";
                                for(int i = 0;i<title.length();i++){
                                     TitlejsonArray = title.getJSONArray(i);
                                     QuantityjsonArray = orderquantity.getJSONArray(i);


                                for(int j = 0;j<TitlejsonArray.length();j++){
                                    foodnames+=TitlejsonArray.getString(j)+",";
                                    foodquantity+=QuantityjsonArray.getString(j)+",";
                                }

                                   totalbill = orderbill.getString(i);
                                   date = orderdate.getString(i);
                                   tablenumber = orderhoteltable.getString(i);
                                   mystatus = orderstatus.getString(i);
                                    OrderDataModel orderDataModel = new OrderDataModel(foodnames,totalbill,foodquantity,date,tablenumber,mystatus);
                                    arrayList.add(orderDataModel);
                                }
                                  adaphter = new OrderAdaphter(arrayList,getActivity(),MyOrders.this);
                                recyclerView = view.findViewById(R.id.orderlist);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));

                                recyclerView.setAdapter(adaphter);


                            }else if(status.equals("404")){
                                createaccount.setVisibility(View.GONE);
                                wrapper.setVisibility(View.VISIBLE);
                                message.setVisibility(View.VISIBLE);
                                loader.setVisibility(View.GONE);
                                message.setText("You Donot Have Any Orders..");
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
                buildDialog3(getActivity(), "Oops..!", "Please try again later").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username",username);

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


        return builder;
    }
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                loader.setVisibility(View.GONE);
               Intent intent = new Intent(getActivity(),DashBoard.class);
               startActivity(intent);

            }
        }, 1000);
    }

}