package aust.fyp.pk.project.application.smartaccessrestaurant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {
    private View view;
    private String hotelname,tablenumber;
    private FrameLayout pakistanifoods,fastfoods,beverages,desserts,specialdeals;
    private Intent intent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        hotelname = getArguments().getString("hotelname");
        tablenumber = getArguments().getString("tablenumber");

        pakistanifoods  = view.findViewById(R.id.pakistanifood);
        fastfoods = view.findViewById(R.id.fastfood);
        beverages = view.findViewById(R.id.beverages);
        desserts = view.findViewById(R.id.desserts);
        specialdeals = view.findViewById(R.id.specialdeals);

        pakistanifoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(),FoodItemsList.class);
                intent.putExtra("category","Pakistani Foods");
                intent.putExtra("hotelname",hotelname);
                intent.putExtra("tablenumber",tablenumber);
                startActivity(intent);

            }
        });

        fastfoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(),FoodItemsList.class);
                intent.putExtra("category","Fast Food");
                intent.putExtra("hotelname",hotelname);
                intent.putExtra("tablenumber",tablenumber);
                startActivity(intent);

            }
        });
        beverages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(),FoodItemsList.class);
                intent.putExtra("category","Beverages");
                intent.putExtra("hotelname",hotelname);
                intent.putExtra("tablenumber",tablenumber);
                startActivity(intent);

            }
        });

        desserts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(),FoodItemsList.class);
                intent.putExtra("category","Desserts");
                intent.putExtra("hotelname",hotelname);
                intent.putExtra("tablenumber",tablenumber);
                startActivity(intent);

            }
        });

        specialdeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(),FoodItemsList.class);
                intent.putExtra("category","specialdeals");
                intent.putExtra("hotelname",hotelname);
                intent.putExtra("tablenumber",tablenumber);
                startActivity(intent);

            }
        });

        return view;
    }


}