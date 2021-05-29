package aust.fyp.pk.project.application.smartaccessrestaurant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderAdaphter extends RecyclerView.Adapter<OrderAdaphter.viewholder> {
    private Context context;
    private ArrayList<OrderDataModel> orderDataModels;
    private MyOrders myOrders;
    private int count = 0;
    public OrderAdaphter(ArrayList<OrderDataModel> orderDataModels,Context context,MyOrders myOrders) {

        this.orderDataModels = orderDataModels;
        this.context = context;
        this.myOrders = myOrders;
    }


    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.myorderslistlayout,parent,false);

        return new OrderAdaphter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {

        holder.loader.setVisibility(View.GONE);
        holder.title.setText(orderDataModels.get(position).getTitle());

        holder.quantity.setText("Quantity: "+orderDataModels.get(position).getQuantity());

        holder.totalbill.setText("Total Bill: RS "+orderDataModels.get(position).getTotalbill());

        holder.date.setText("Date: "+orderDataModels.get(position).getDate());

        holder.status.setText("Order status: "+orderDataModels.get(position).getStatus());
        if(orderDataModels.get(position).getStatus().equals("Preparing") ||orderDataModels.get(position).getStatus().equals("Paid")){
            holder.generatebill.setVisibility(View.GONE);
        }
        holder.generatebill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == 1){
                    Toast.makeText(context, "Please Wait", Toast.LENGTH_SHORT).show();
                }else{
                    holder.loader.setVisibility(View.VISIBLE);
                    GenerateBill();
                    count++;

                }
                    }
        });
    }

    @Override
    public int getItemCount() {
        return orderDataModels.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{

        private TextView title,quantity,totalbill,date,status;
        private Button generatebill;
        private LinearLayout loader;
        public viewholder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            title = itemView.findViewById(R.id.itemname);
            quantity = itemView.findViewById(R.id.quantity);
            totalbill = itemView.findViewById(R.id.totalbill);
            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.itemstatus);
            generatebill = itemView.findViewById(R.id.generatebill);
            loader = itemView.findViewById(R.id.loader);
        }
    }
    private void GenerateBill()
    {
        final RequestQueue queue = Volley.newRequestQueue(context);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.GENERRATE_BILL;
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

                                buildDialog3(context, "500", "Internal Server Error").show();
                            } else if (status.equals("200")) {


                                buildDialog4(context, "Bill Generated Successfully", "See My Orders Option To See The Status Of Your Order").show();


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

                buildDialog3(context, "Oops..!", "Please try again later").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                SharedPreferences sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username","");
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

        waitforsometime();
        return builder;
    }
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                myOrders.waitforsometime();
            }
        }, 1000);
    }
    public AlertDialog.Builder buildDialog4(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        myOrders.waitforsometime();
        return builder;
    }
}
