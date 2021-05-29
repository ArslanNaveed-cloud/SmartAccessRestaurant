package aust.fyp.pk.project.application.smartaccessrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.BreakIterator;


public class Profile extends Fragment {

    private View view;
    private TextView name,email,passsword,phonenumber;
    private LinearLayout username,userphone,userpassword,useremail,wrapper;
    private boolean isuserloggedin;
    private Button createaccount;
    private TextView message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        createaccount = view.findViewById(R.id.createaccount);
        wrapper = view.findViewById(R.id.wrapper);
        name= view.findViewById(R.id.name);
        email = view.findViewById(R.id.uemail);
        phonenumber = view.findViewById(R.id.uphone);
        passsword = view.findViewById(R.id.pass);

        username = view.findViewById(R.id.username);
        userphone = view.findViewById(R.id.userphone);
        useremail = view.findViewById(R.id.useremail);
        userpassword = view.findViewById(R.id.userpassword);


        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ChangeName.class);
                startActivity(intent);

            }
        });

        userphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ChangePhone.class);
                startActivity(intent);
            }
        });

        useremail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ChangeEmail.class);
                startActivity(intent);
            }
        });

        userpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ChangePassword.class);
                startActivity(intent);
            }
        });

        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SignupScreen.class);
                startActivity(intent);

            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        isuserloggedin =  sharedPreferences.getBoolean("isuserloggedin",false);
        if(isuserloggedin) {
            wrapper.setVisibility(View.GONE);
            String user = sharedPreferences.getString("username",null);
            String phone = sharedPreferences.getString("phone","");
            String emaila = sharedPreferences.getString("email","");
            String passworda = sharedPreferences.getString("passsword","");
            name.setText(user);
            phonenumber.setText(phone);
            email.setText(emaila);
            passsword.setText("********");




        }else{
            wrapper.setVisibility(View.VISIBLE);
        }




            return view;
    }
}